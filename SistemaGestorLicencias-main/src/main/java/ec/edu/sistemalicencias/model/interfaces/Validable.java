package ec.edu.sistemalicencias.model.interfaces;

import ec.edu.sistemalicencias.model.exceptions.DocumentoInvalidoException;

/**
 * Interface que define el comportamiento de validación.
 * Aplicación del principio de ABSTRACCIÓN en POO.
 */
public interface Validable {

    /**
     * Valida la entidad según sus reglas de negocio
     * @return true si la validación es exitosa
     * @throws DocumentoInvalidoException si hay errores de validación
     */
    boolean validar() throws DocumentoInvalidoException;

    /**
     * Obtiene un mensaje descriptivo del resultado de validación
     * @return Mensaje de validación
     */
    String obtenerMensajeValidacion();
}
