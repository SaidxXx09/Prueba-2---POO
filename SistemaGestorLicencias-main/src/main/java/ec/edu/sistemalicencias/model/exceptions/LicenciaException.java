package ec.edu.sistemalicencias.model.exceptions;

/**
 * Excepción base (checked) para el sistema de licencias.
 * Todas las excepciones específicas del dominio heredan de esta clase.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class LicenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor sin parámetros
     */
    public LicenciaException() {
        super();
    }

    /**
     * Constructor con mensaje de error
     * @param mensaje Descripción del error
     */
    public LicenciaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * @param mensaje Descripción del error
     * @param causa Excepción que causó el error
     */
    public LicenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    /**
     * Constructor con causa
     * @param causa Excepción que causó el error
     */
    public LicenciaException(Throwable causa) {
        super(causa);
    }
}
