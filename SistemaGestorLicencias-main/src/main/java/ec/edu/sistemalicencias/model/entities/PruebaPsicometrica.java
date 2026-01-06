package ec.edu.sistemalicencias.model.entities;

import ec.edu.sistemalicencias.model.exceptions.DatosInvalidosException;

import java.time.LocalDateTime;

/**
 * Entidad que representa una prueba psicométrica realizada por un conductor.
 * Encapsula las notas y el resultado de las evaluaciones psicológicas.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class PruebaPsicometrica {

    // Constantes para las notas
    private static final double NOTA_MINIMA = 0.0;
    private static final double NOTA_MAXIMA = 100.0;
    private static final double NOTA_APROBACION = 70.0;

    // Atributos privados (Encapsulamiento)
    private Long id;
    private Long conductorId;
    private double notaReaccion;        // Velocidad de reacción
    private double notaAtencion;        // Capacidad de atención y concentración
    private double notaCoordinacion;    // Coordinación motora
    private double notaPercepcion;      // Percepción visual y espacial
    private double notaPsicologica;     // Evaluación psicológica
    private LocalDateTime fechaRealizacion;
    private String observaciones;

    /**
     * Constructor por defecto
     */
    public PruebaPsicometrica() {
        this.fechaRealizacion = LocalDateTime.now();
    }

    /**
     * Constructor con ID de conductor
     * @param conductorId ID del conductor que realiza la prueba
     */
    public PruebaPsicometrica(Long conductorId) {
        this();
        this.conductorId = conductorId;
    }

    // Getters y Setters con validaciones

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConductorId() {
        return conductorId;
    }

    public void setConductorId(Long conductorId) {
        this.conductorId = conductorId;
    }

    public double getNotaReaccion() {
        return notaReaccion;
    }

    /**
     * Establece la nota de reacción validando el rango
     * @param notaReaccion Nota entre 0 y 100
     * @throws DatosInvalidosException Si la nota está fuera de rango
     */
    public void setNotaReaccion(double notaReaccion) {
        validarNota(notaReaccion, "reacción");
        this.notaReaccion = notaReaccion;
    }

    public double getNotaAtencion() {
        return notaAtencion;
    }

    /**
     * Establece la nota de atención validando el rango
     * @param notaAtencion Nota entre 0 y 100
     * @throws DatosInvalidosException Si la nota está fuera de rango
     */
    public void setNotaAtencion(double notaAtencion) {
        validarNota(notaAtencion, "atención");
        this.notaAtencion = notaAtencion;
    }

    public double getNotaCoordinacion() {
        return notaCoordinacion;
    }

    /**
     * Establece la nota de coordinación validando el rango
     * @param notaCoordinacion Nota entre 0 y 100
     * @throws DatosInvalidosException Si la nota está fuera de rango
     */
    public void setNotaCoordinacion(double notaCoordinacion) {
        validarNota(notaCoordinacion, "coordinación");
        this.notaCoordinacion = notaCoordinacion;
    }

    public double getNotaPercepcion() {
        return notaPercepcion;
    }

    /**
     * Establece la nota de percepción validando el rango
     * @param notaPercepcion Nota entre 0 y 100
     * @throws DatosInvalidosException Si la nota está fuera de rango
     */
    public void setNotaPercepcion(double notaPercepcion) {
        validarNota(notaPercepcion, "percepción");
        this.notaPercepcion = notaPercepcion;
    }

    public double getNotaPsicologica() {
        return notaPsicologica;
    }

    /**
     * Establece la nota psicológica validando el rango
     * @param notaPsicologica Nota entre 0 y 100
     * @throws DatosInvalidosException Si la nota está fuera de rango
     */
    public void setNotaPsicologica(double notaPsicologica) {
        validarNota(notaPsicologica, "psicológica");
        this.notaPsicologica = notaPsicologica;
    }

    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDateTime fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Valida que una nota esté en el rango permitido
     * @param nota Nota a validar
     * @param nombrePrueba Nombre de la prueba para el mensaje de error
     * @throws DatosInvalidosException Si la nota está fuera de rango
     */
    private void validarNota(double nota, String nombrePrueba) {
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new DatosInvalidosException(
                    "La nota de " + nombrePrueba + " debe estar entre " +
                            NOTA_MINIMA + " y " + NOTA_MAXIMA
            );
        }
    }

    /**
     * Calcula el promedio de todas las pruebas psicométricas
     * @return Promedio de las 5 notas
     */
    public double calcularPromedio() {
        return (notaReaccion + notaAtencion + notaCoordinacion +
                notaPercepcion + notaPsicologica) / 5.0;
    }

    /**
     * Determina si el conductor aprobó las pruebas psicométricas
     * @return true si el promedio es mayor o igual a la nota de aprobación
     */
    public boolean estaAprobado() {
        return calcularPromedio() >= NOTA_APROBACION;
    }

    /**
     * Obtiene el estado de la prueba en formato de texto
     * @return "APROBADO" o "REPROBADO"
     */
    public String obtenerEstado() {
        return estaAprobado() ? "APROBADO" : "REPROBADO";
    }

    /**
     * Genera un reporte detallado de la prueba psicométrica
     * @return String con el detalle de todas las notas y el resultado
     */
    public String generarReporte() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE PRUEBA PSICOMÉTRICA ===\n");
        reporte.append(String.format("Fecha: %s\n", fechaRealizacion));
        reporte.append(String.format("Nota Reacción: %.2f\n", notaReaccion));
        reporte.append(String.format("Nota Atención: %.2f\n", notaAtencion));
        reporte.append(String.format("Nota Coordinación: %.2f\n", notaCoordinacion));
        reporte.append(String.format("Nota Percepción: %.2f\n", notaPercepcion));
        reporte.append(String.format("Nota Psicológica: %.2f\n", notaPsicologica));
        reporte.append(String.format("PROMEDIO: %.2f\n", calcularPromedio()));
        reporte.append(String.format("ESTADO: %s\n", obtenerEstado()));
        if (observaciones != null && !observaciones.isEmpty()) {
            reporte.append(String.format("Observaciones: %s\n", observaciones));
        }
        reporte.append("=====================================");
        return reporte.toString();
    }

    /**
     * Obtiene la nota mínima de aprobación
     * @return Nota mínima para aprobar
     */
    public static double getNotaAprobacion() {
        return NOTA_APROBACION;
    }

    @Override
    public String toString() {
        return "PruebaPsicometrica{" +
                "conductorId=" + conductorId +
                ", promedio=" + String.format("%.2f", calcularPromedio()) +
                ", estado=" + obtenerEstado() +
                ", fecha=" + fechaRealizacion +
                '}';
    }
}
