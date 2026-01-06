package ec.edu.sistemalicencias.model;

/**
 * Clase con constantes para tipos de sangre.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class TipoSangreConstantes {

    // Constantes de tipos de sangre
    public static final String A_POSITIVO = "A+";
    public static final String A_NEGATIVO = "A-";
    public static final String B_POSITIVO = "B+";
    public static final String B_NEGATIVO = "B-";
    public static final String AB_POSITIVO = "AB+";
    public static final String AB_NEGATIVO = "AB-";
    public static final String O_POSITIVO = "O+";
    public static final String O_NEGATIVO = "O-";

    // Array con todos los tipos para uso en ComboBox
    public static final String[] TIPOS_SANGRE = {
            A_POSITIVO,
            A_NEGATIVO,
            B_POSITIVO,
            B_NEGATIVO,
            AB_POSITIVO,
            AB_NEGATIVO,
            O_POSITIVO,
            O_NEGATIVO
    };

    /**
     * Constructor privado para evitar instanciación
     */
    private TipoSangreConstantes() {
        throw new AssertionError("No se debe instanciar esta clase de constantes");
    }

    /**
     * Valida si un tipo de sangre es válido
     * @param tipo Tipo de sangre a validar
     * @return true si es válido
     */
    public static boolean esValido(String tipo) {
        if (tipo == null) return false;
        for (String tipoValido : TIPOS_SANGRE) {
            if (tipoValido.equals(tipo)) {
                return true;
            }
        }
        return false;
    }
}
