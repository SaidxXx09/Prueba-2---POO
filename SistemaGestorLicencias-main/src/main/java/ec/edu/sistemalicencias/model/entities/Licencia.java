package ec.edu.sistemalicencias.model.entities;

import ec.edu.sistemalicencias.model.TipoLicenciaConstantes;
import ec.edu.sistemalicencias.model.exceptions.DatosInvalidosException;
import ec.edu.sistemalicencias.model.exceptions.DocumentoInvalidoException;
import ec.edu.sistemalicencias.model.interfaces.Validable;

import java.time.LocalDate;

/**
 * Entidad que representa una licencia de conducir.
 * Implementa validaciones y encapsulamiento de datos.
 * Usa constantes en lugar de enums.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class Licencia implements Validable {

    // Atributos privados (Encapsulamiento)
    private Long id;
    private String numeroLicencia;
    private Long conductorId;
    private String tipoLicencia; // Usa constantes de TipoLicenciaConstantes
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private boolean activa;
    private String observaciones;
    private Long pruebaPsicometricaId;

    /**
     * Constructor por defecto
     */
    public Licencia() {
        this.activa = true;
        this.fechaEmision = LocalDate.now();
    }

    /**
     * Constructor con parámetros principales
     * @param conductorId ID del conductor
     * @param tipoLicencia Tipo de licencia (usar constantes de TipoLicenciaConstantes)
     */
    public Licencia(Long conductorId, String tipoLicencia) {
        this.activa = true;
        this.fechaEmision = LocalDate.now();
        this.conductorId = conductorId;
        setTipoLicencia(tipoLicencia);
        // Las licencias en Ecuador tienen validez de 5 años
        this.fechaVencimiento = fechaEmision.plusYears(5);
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public Long getConductorId() {
        return conductorId;
    }

    public void setConductorId(Long conductorId) {
        this.conductorId = conductorId;
    }

    public String getTipoLicencia() {
        return tipoLicencia;
    }

    /**
     * Establece el tipo de licencia validando que sea válido
     * @param tipoLicencia Tipo (usar constantes de TipoLicenciaConstantes)
     */
    public void setTipoLicencia(String tipoLicencia) {
        if (tipoLicencia != null && !TipoLicenciaConstantes.esValido(tipoLicencia)) {
            throw new DatosInvalidosException("Tipo de licencia inválido: " + tipoLicencia);
        }
        this.tipoLicencia = tipoLicencia;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getPruebaPsicometricaId() {
        return pruebaPsicometricaId;
    }

    public void setPruebaPsicometricaId(Long pruebaPsicometricaId) {
        this.pruebaPsicometricaId = pruebaPsicometricaId;
    }

    /**
     * Genera un número de licencia único basado en parámetros
     * Formato: EC-[TIPO]-[AÑO]-[ID]
     * @param conductorCedula Cédula del conductor
     */
    public void generarNumeroLicencia(String conductorCedula) {
        String tipo = TipoLicenciaConstantes.obtenerNombreCorto(tipoLicencia);
        String anio = String.valueOf(fechaEmision.getYear());
        String ultimosDigitos = conductorCedula.substring(conductorCedula.length() - 4);

        this.numeroLicencia = String.format("EC-%s-%s-%s", tipo, anio, ultimosDigitos);
    }

    /**
     * Verifica si la licencia está vigente
     * @return true si está activa y no ha vencido
     */
    public boolean estaVigente() {
        return activa && !estaVencida();
    }

    /**
     * Verifica si la licencia está vencida
     * @return true si la fecha actual es posterior a la fecha de vencimiento
     */
    public boolean estaVencida() {
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    /**
     * Calcula los días restantes hasta el vencimiento
     * @return Número de días hasta el vencimiento (negativo si ya venció)
     */
    public long diasHastaVencimiento() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
    }

    /**
     * Implementación del método validar de la interface Validable
     * @return true si la licencia es válida
     * @throws DocumentoInvalidoException Si la validación falla
     */
    @Override
    public boolean validar() throws DocumentoInvalidoException {
        StringBuilder errores = new StringBuilder();

        // Validar que tenga conductor asignado
        if (conductorId == null) {
            errores.append("- La licencia debe estar asociada a un conductor\n");
        }

        // Validar tipo de licencia
        if (tipoLicencia == null || !TipoLicenciaConstantes.esValido(tipoLicencia)) {
            errores.append("- Debe especificarse un tipo de licencia válido\n");
        }

        // Validar fechas
        if (fechaEmision == null) {
            errores.append("- La fecha de emisión es obligatoria\n");
        }

        if (fechaVencimiento == null) {
            errores.append("- La fecha de vencimiento es obligatoria\n");
        }

        if (fechaEmision != null && fechaVencimiento != null) {
            if (fechaVencimiento.isBefore(fechaEmision)) {
                errores.append("- La fecha de vencimiento no puede ser anterior a la fecha de emisión\n");
            }
        }

        // Validar número de licencia
        if (numeroLicencia == null || numeroLicencia.trim().isEmpty()) {
            errores.append("- El número de licencia es obligatorio\n");
        }

        if (errores.length() > 0) {
            throw new DocumentoInvalidoException("Errores de validación de licencia:\n" + errores.toString());
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
                return "Licencia validada correctamente: " + numeroLicencia +
                        " (" + TipoLicenciaConstantes.obtenerNombre(tipoLicencia) + ")";
            }
        } catch (DocumentoInvalidoException e) {
            return e.getMessage();
        }
        return "Error en la validación de licencia";
    }

    /**
     * Obtiene el estado completo de la licencia
     * @return Estado descriptivo
     */
    public String obtenerEstado() {
        if (!activa) {
            return "INACTIVA";
        }
        if (estaVencida()) {
            return "VENCIDA";
        }
        long dias = diasHastaVencimiento();
        if (dias < 30) {
            return "POR VENCER (" + dias + " días)";
        }
        return "VIGENTE";
    }

    @Override
    public String toString() {
        return "Licencia{" +
                "numeroLicencia='" + numeroLicencia + '\'' +
                ", tipoLicencia=" + TipoLicenciaConstantes.obtenerNombre(tipoLicencia) +
                ", estado=" + obtenerEstado() +
                ", fechaEmision=" + fechaEmision +
                ", fechaVencimiento=" + fechaVencimiento +
                '}';
    }
}
