package ec.edu.sistemalicencias.model.exceptions;

/**
 * Excepción checked para documentos inválidos.
 * Se lanza cuando un documento no cumple con los requisitos de validación.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class DocumentoInvalidoException extends LicenciaException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor con mensaje de error
     * @param mensaje Descripción del error de validación
     */
    public DocumentoInvalidoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * @param mensaje Descripción del error de validación
     * @param causa Excepción que causó el error
     */
    public DocumentoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
