package ec.edu.sistemalicencias.model;

/**
 * Clase con constantes para tipos de licencias de conducir en Ecuador.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class TipoLicenciaConstantes {

    // Constantes de tipos de licencia
    public static final String TIPO_A = "TIPO_A";
    public static final String TIPO_B = "TIPO_B";
    public static final String TIPO_C = "TIPO_C";
    public static final String TIPO_D = "TIPO_D";
    public static final String TIPO_E = "TIPO_E";
    public static final String TIPO_F = "TIPO_F";

    // Nombres descriptivos
    public static final String NOMBRE_TIPO_A = "Tipo A - Motocicletas y ciclomotores";
    public static final String NOMBRE_TIPO_B = "Tipo B - Vehículos livianos hasta 3500 kg";
    public static final String NOMBRE_TIPO_C = "Tipo C - Vehículos pesados de carga";
    public static final String NOMBRE_TIPO_D = "Tipo D - Transporte público de pasajeros";
    public static final String NOMBRE_TIPO_E = "Tipo E - Vehículos especiales y maquinaria";
    public static final String NOMBRE_TIPO_F = "Tipo F - Transporte comercial profesional";

    // Array con todos los tipos para uso en ComboBox
    public static final String[] TIPOS_LICENCIA = {
            TIPO_A, TIPO_B, TIPO_C, TIPO_D, TIPO_E, TIPO_F
    };

    // Array con nombres descriptivos para ComboBox
    public static final String[] NOMBRES_TIPOS_LICENCIA = {
            NOMBRE_TIPO_A,
            NOMBRE_TIPO_B,
            NOMBRE_TIPO_C,
            NOMBRE_TIPO_D,
            NOMBRE_TIPO_E,
            NOMBRE_TIPO_F
    };

    /**
     * Constructor privado para evitar instanciación
     */
    private TipoLicenciaConstantes() {
        throw new AssertionError("No se debe instanciar esta clase de constantes");
    }

    /**
     * Obtiene el nombre descriptivo de un tipo de licencia
     * @param tipo Código del tipo (TIPO_A, TIPO_B, etc.)
     * @return Nombre descriptivo
     */
    public static String obtenerNombre(String tipo) {
        switch (tipo) {
            case TIPO_A: return NOMBRE_TIPO_A;
            case TIPO_B: return NOMBRE_TIPO_B;
            case TIPO_C: return NOMBRE_TIPO_C;
            case TIPO_D: return NOMBRE_TIPO_D;
            case TIPO_E: return NOMBRE_TIPO_E;
            case TIPO_F: return NOMBRE_TIPO_F;
            default: return "Tipo desconocido";
        }
    }

    /**
     * Obtiene el código del tipo desde un nombre descriptivo
     * @param nombreDescriptivo Nombre completo
     * @return Código del tipo
     */
    public static String obtenerCodigoDesdeNombre(String nombreDescriptivo) {
        if (nombreDescriptivo.startsWith("Tipo A")) return TIPO_A;
        if (nombreDescriptivo.startsWith("Tipo B")) return TIPO_B;
        if (nombreDescriptivo.startsWith("Tipo C")) return TIPO_C;
        if (nombreDescriptivo.startsWith("Tipo D")) return TIPO_D;
        if (nombreDescriptivo.startsWith("Tipo E")) return TIPO_E;
        if (nombreDescriptivo.startsWith("Tipo F")) return TIPO_F;
        return TIPO_B; // Por defecto
    }

    /**
     * Obtiene solo el nombre corto del tipo (A, B, C, etc.)
     * @param tipo Código del tipo
     * @return Letra del tipo
     */
    public static String obtenerNombreCorto(String tipo) {
        return tipo.replace("TIPO_", "");
    }

    /**
     * Valida si un tipo de licencia es válido
     * @param tipo Código del tipo a validar
     * @return true si es válido
     */
    public static boolean esValido(String tipo) {
        return TIPO_A.equals(tipo) || TIPO_B.equals(tipo) || TIPO_C.equals(tipo) ||
                TIPO_D.equals(tipo) || TIPO_E.equals(tipo) || TIPO_F.equals(tipo);
    }
}
