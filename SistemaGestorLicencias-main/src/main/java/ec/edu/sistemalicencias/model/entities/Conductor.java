package ec.edu.sistemalicencias.model.entities;

import ec.edu.sistemalicencias.model.TipoSangreConstantes;
import ec.edu.sistemalicencias.model.exceptions.DatosInvalidosException;
import ec.edu.sistemalicencias.model.exceptions.DocumentoInvalidoException;
import ec.edu.sistemalicencias.model.interfaces.Validable;

import java.time.LocalDate;
import java.time.Period;

/**
 * Entidad que representa a un conductor que solicita una licencia.
 * Implementa encapsulamiento mediante getters/setters y validaciones.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class Conductor implements Validable {

    // Atributos privados (Encapsulamiento)
    private Long id;
    private String cedula;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String email;
    private String tipoSangre;
    private boolean documentosValidados;
    private String observaciones;

    /**
     * Constructor por defecto
     */
    public Conductor() {
        this.documentosValidados = false;
    }

    /**
     * Constructor con parámetros principales
     * @param cedula Número de cédula ecuatoriana
     * @param nombres Nombres del conductor
     * @param apellidos Apellidos del conductor
     * @param fechaNacimiento Fecha de nacimiento
     */
    public Conductor(String cedula, String nombres, String apellidos, LocalDate fechaNacimiento) {
        this();
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getters y Setters con validaciones (Encapsulamiento)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    /**
     * Establece la cédula validando el formato
     * @param cedula Número de cédula (10 dígitos)
     * @throws DatosInvalidosException Si la cédula no tiene el formato correcto
     */
    public void setCedula(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            throw new DatosInvalidosException("La cédula debe contener exactamente 10 dígitos");
        }
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    /**
     * Establece los nombres validando que no sean vacíos
     * @param nombres Nombres del conductor
     * @throws DatosInvalidosException Si los nombres están vacíos
     */
    public void setNombres(String nombres) {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new DatosInvalidosException("Los nombres no pueden estar vacíos");
        }
        this.nombres = nombres.trim().toUpperCase();
    }

    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos validando que no sean vacíos
     * @param apellidos Apellidos del conductor
     * @throws DatosInvalidosException Si los apellidos están vacíos
     */
    public void setApellidos(String apellidos) {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new DatosInvalidosException("Los apellidos no pueden estar vacíos");
        }
        this.apellidos = apellidos.trim().toUpperCase();
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono validando el formato
     * @param telefono Número de teléfono (10 dígitos)
     */
    public void setTelefono(String telefono) {
        if (telefono != null && !telefono.matches("\\d{9,10}")) {
            throw new DatosInvalidosException("El teléfono debe contener 9 o 10 dígitos");
        }
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Establece el email validando el formato básico
     * @param email Correo electrónico
     */
    public void setEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new DatosInvalidosException("El formato del email no es válido");
        }
        this.email = email;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    /**
     * Establece el tipo de sangre validando que sea válido
     * @param tipoSangre Tipo de sangre (usar constantes de TipoSangreConstantes)
     */
    public void setTipoSangre(String tipoSangre) {
        if (tipoSangre != null && !TipoSangreConstantes.esValido(tipoSangre)) {
            throw new DatosInvalidosException("Tipo de sangre inválido: " + tipoSangre);
        }
        this.tipoSangre = tipoSangre;
    }

    public boolean isDocumentosValidados() {
        return documentosValidados;
    }

    public void setDocumentosValidados(boolean documentosValidados) {
        this.documentosValidados = documentosValidados;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Calcula la edad del conductor basándose en la fecha de nacimiento
     * @return Edad en años
     */
    public int calcularEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /**
     * Obtiene el nombre completo del conductor
     * @return Nombres y apellidos concatenados
     */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /**
     * Valida la cédula ecuatoriana usando el algoritmo del módulo 10
     * @param cedula Número de cédula a validar
     * @return true si la cédula es válida, false en caso contrario
     */
    private boolean validarCedulaEcuatoriana(String cedula) {
        if (cedula == null || cedula.length() != 10) {
            return false;
        }

        try {
            // Validar que los dos primeros dígitos correspondan a una provincia válida (01-24)
            int provincia = Integer.parseInt(cedula.substring(0, 2));
            if (provincia < 1 || provincia > 24) {
                return false;
            }

            // Validar el tercer dígito (debe ser menor a 6 para cédulas de personas naturales)
            int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
            if (tercerDigito > 5) {
                return false;
            }

            // Algoritmo de validación del dígito verificador
            int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
            int suma = 0;

            for (int i = 0; i < 9; i++) {
                int valor = Integer.parseInt(cedula.substring(i, i + 1)) * coeficientes[i];
                if (valor >= 10) {
                    valor -= 9;
                }
                suma += valor;
            }

            int digitoVerificador = Integer.parseInt(cedula.substring(9, 10));
            int residuo = suma % 10;
            int resultado = residuo == 0 ? 0 : 10 - residuo;

            return resultado == digitoVerificador;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Implementación del método validar de la interface Validable
     * @return true si todos los datos son válidos
     * @throws DocumentoInvalidoException Si la validación falla
     */
    @Override
    public boolean validar() throws DocumentoInvalidoException {
        StringBuilder errores = new StringBuilder();

        // Validar cédula
        if (!validarCedulaEcuatoriana(cedula)) {
            errores.append("- Cédula ecuatoriana inválida\n");
        }

        // Validar edad mínima (18 años)
        if (calcularEdad() < 18) {
            errores.append("- El conductor debe ser mayor de 18 años\n");
        }

        // Validar campos obligatorios
        if (nombres == null || nombres.trim().isEmpty()) {
            errores.append("- Los nombres son obligatorios\n");
        }

        if (apellidos == null || apellidos.trim().isEmpty()) {
            errores.append("- Los apellidos son obligatorios\n");
        }

        if (fechaNacimiento == null) {
            errores.append("- La fecha de nacimiento es obligatoria\n");
        }

        if (errores.length() > 0) {
            throw new DocumentoInvalidoException("Errores de validación del conductor:\n" + errores.toString());
        }

        return true;
    }

    /**
     * Obtiene un mensaje de validación
     * @return Mensaje descriptivo del estado de validación
     */
    @Override
    public String obtenerMensajeValidacion() {
        try {
            if (validar()) {
                return "Conductor validado correctamente: " + getNombreCompleto();
            }
        } catch (DocumentoInvalidoException e) {
            return e.getMessage();
        }
        return "Error en la validación";
    }

    @Override
    public String toString() {
        return "Conductor{" +
                "cedula='" + cedula + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", edad=" + calcularEdad() +
                ", documentosValidados=" + documentosValidados +
                '}';
    }
}
