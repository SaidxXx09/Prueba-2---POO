package ec.edu.sistemalicencias.model.exceptions;

/**
 * Excepción checked para errores de base de datos.
 * Se lanza cuando ocurre un error en operaciones de persistencia.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class BaseDatosException extends LicenciaException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor con mensaje de error
     * @param mensaje Descripción del error de base de datos
     */
    public BaseDatosException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * @param mensaje Descripción del error de base de datos
     * @param causa Excepción que causó el error (SQLException, etc.)
     */
    public BaseDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
