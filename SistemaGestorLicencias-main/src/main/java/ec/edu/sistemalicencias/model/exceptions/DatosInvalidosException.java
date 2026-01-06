package ec.edu.sistemalicencias.model.exceptions;

/**
 * Excepción unchecked (RuntimeException) para datos inválidos en tiempo de ejecución.
 * Se utiliza para validaciones que no se esperan en el flujo normal del programa.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class DatosInvalidosException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor con mensaje de error
     * @param mensaje Descripción de los datos inválidos
     */
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * @param mensaje Descripción de los datos inválidos
     * @param causa Excepción que causó el error
     */
    public DatosInvalidosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
