package ec.edu.sistemalicencias.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import ec.edu.sistemalicencias.model.TipoLicenciaConstantes;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.entities.Licencia;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad para generar documentos PDF de licencias de conducir.
 * Usa iText para la generación de documentos.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class PDFGenerator {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
    private static final Font FONT_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE);
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
    private static final Font FONT_CAMPO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);

    /**
     * Genera un documento PDF de licencia de conducir
     * @param licencia Licencia a generar
     * @param conductor Conductor propietario
     * @param prueba Prueba psicométrica asociada (puede ser null)
     * @param rutaArchivo Ruta donde se guardará el PDF
     * @throws DocumentException Si hay error al generar el documento
     * @throws IOException Si hay error al escribir el archivo
     */
    public static void generarLicenciaPDF(Licencia licencia, Conductor conductor,
                                          PruebaPsicometrica prueba, String rutaArchivo)
            throws DocumentException, IOException {

        // Crear documento PDF
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));

        documento.open();

        // Agregar contenido al documento
        agregarEncabezado(documento);
        agregarDatosConductor(documento, conductor);
        agregarDatosLicencia(documento, licencia);

        if (prueba != null) {
            agregarDatosPrueba(documento, prueba);
        }

        agregarPiePagina(documento);

        documento.close();
    }

    /**
     * Agrega el encabezado del documento
     */
    private static void agregarEncabezado(Document documento) throws DocumentException {
        // Título principal
        Paragraph titulo = new Paragraph("REPÚBLICA DEL ECUADOR", FONT_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        documento.add(titulo);

        // Subtítulo
        Paragraph subtitulo = new Paragraph("AGENCIA NACIONAL DE TRÁNSITO", FONT_SUBTITULO);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(5);
        documento.add(subtitulo);

        // Tipo de documento
        Paragraph tipoDoc = new Paragraph("LICENCIA DE CONDUCIR", FONT_SUBTITULO);
        tipoDoc.setAlignment(Element.ALIGN_CENTER);
        tipoDoc.setSpacingAfter(20);
        documento.add(tipoDoc);

        // Línea separadora
        LineSeparator linea = new LineSeparator();
        linea.setLineColor(BaseColor.BLUE);
        documento.add(linea);
        documento.add(Chunk.NEWLINE);
    }

    /**
     * Agrega los datos del conductor
     */
    private static void agregarDatosConductor(Document documento, Conductor conductor)
            throws DocumentException {

        Paragraph seccion = new Paragraph("DATOS DEL CONDUCTOR", FONT_SUBTITULO);
        seccion.setSpacingBefore(10);
        seccion.setSpacingAfter(10);
        documento.add(seccion);

        // Crear tabla para datos del conductor
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(15);

        // Configurar anchos de columnas
        float[] columnWidths = {40f, 60f};
        tabla.setWidths(columnWidths);

        // Agregar filas
        agregarFilaTabla(tabla, "Cédula:", conductor.getCedula());
        agregarFilaTabla(tabla, "Nombres:", conductor.getNombres());
        agregarFilaTabla(tabla, "Apellidos:", conductor.getApellidos());
        agregarFilaTabla(tabla, "Fecha de Nacimiento:",
                conductor.getFechaNacimiento().format(FORMATO_FECHA));
        agregarFilaTabla(tabla, "Edad:", conductor.calcularEdad() + " años");

        if (conductor.getTipoSangre() != null) {
            agregarFilaTabla(tabla, "Tipo de Sangre:", conductor.getTipoSangre().toString());
        }

        if (conductor.getDireccion() != null && !conductor.getDireccion().isEmpty()) {
            agregarFilaTabla(tabla, "Dirección:", conductor.getDireccion());
        }

        if (conductor.getTelefono() != null && !conductor.getTelefono().isEmpty()) {
            agregarFilaTabla(tabla, "Teléfono:", conductor.getTelefono());
        }

        if (conductor.getEmail() != null && !conductor.getEmail().isEmpty()) {
            agregarFilaTabla(tabla, "Email:", conductor.getEmail());
        }

        documento.add(tabla);
    }

    /**
     * Agrega los datos de la licencia
     */
    private static void agregarDatosLicencia(Document documento, Licencia licencia)
            throws DocumentException {

        Paragraph seccion = new Paragraph("DATOS DE LA LICENCIA", FONT_SUBTITULO);
        seccion.setSpacingBefore(10);
        seccion.setSpacingAfter(10);
        documento.add(seccion);

        // Crear tabla para datos de licencia
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(15);

        float[] columnWidths = {40f, 60f};
        tabla.setWidths(columnWidths);

        // Agregar filas
        agregarFilaTabla(tabla, "Número de Licencia:", licencia.getNumeroLicencia());
        agregarFilaTabla(tabla, "Tipo:", TipoLicenciaConstantes.obtenerNombre(licencia.getTipoLicencia()));
        agregarFilaTabla(tabla, "Fecha de Emisión:",
                licencia.getFechaEmision().format(FORMATO_FECHA));
        agregarFilaTabla(tabla, "Fecha de Vencimiento:",
                licencia.getFechaVencimiento().format(FORMATO_FECHA));
        agregarFilaTabla(tabla, "Estado:", licencia.obtenerEstado());

        documento.add(tabla);

        // Cuadro destacado con información importante
        PdfPTable cuadroImportante = new PdfPTable(1);
        cuadroImportante.setWidthPercentage(100);
        cuadroImportante.setSpacingBefore(10);
        cuadroImportante.setSpacingAfter(15);

        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(new BaseColor(230, 240, 255));
        celda.setBorderColor(BaseColor.BLUE);
        celda.setBorderWidth(2);
        celda.setPadding(10);

        Paragraph importante = new Paragraph();
        importante.add(new Chunk("VÁLIDA HASTA: ", FONT_CAMPO));
        importante.add(new Chunk(licencia.getFechaVencimiento().format(FORMATO_FECHA),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.RED)));
        importante.setAlignment(Element.ALIGN_CENTER);

        celda.addElement(importante);
        cuadroImportante.addCell(celda);
        documento.add(cuadroImportante);
    }

    /**
     * Agrega los datos de la prueba psicométrica
     */
    private static void agregarDatosPrueba(Document documento, PruebaPsicometrica prueba)
            throws DocumentException {

        Paragraph seccion = new Paragraph("PRUEBA PSICOMÉTRICA", FONT_SUBTITULO);
        seccion.setSpacingBefore(10);
        seccion.setSpacingAfter(10);
        documento.add(seccion);

        // Crear tabla para notas
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(15);

        float[] columnWidths = {60f, 40f};
        tabla.setWidths(columnWidths);

        // Agregar filas con notas
        agregarFilaTabla(tabla, "Reacción:", String.format("%.2f", prueba.getNotaReaccion()));
        agregarFilaTabla(tabla, "Atención:", String.format("%.2f", prueba.getNotaAtencion()));
        agregarFilaTabla(tabla, "Coordinación:", String.format("%.2f", prueba.getNotaCoordinacion()));
        agregarFilaTabla(tabla, "Percepción:", String.format("%.2f", prueba.getNotaPercepcion()));
        agregarFilaTabla(tabla, "Evaluación Psicológica:", String.format("%.2f", prueba.getNotaPsicologica()));

        // Fila de promedio destacada
        PdfPCell celdaCampo = new PdfPCell(new Phrase("PROMEDIO:", FONT_CAMPO));
        celdaCampo.setBackgroundColor(new BaseColor(240, 240, 240));
        celdaCampo.setPadding(5);
        tabla.addCell(celdaCampo);

        PdfPCell celdaValor = new PdfPCell(new Phrase(
                String.format("%.2f", prueba.calcularPromedio()),
                FONT_CAMPO
        ));
        celdaValor.setBackgroundColor(new BaseColor(240, 240, 240));
        celdaValor.setPadding(5);
        tabla.addCell(celdaValor);

        // Fila de estado
        celdaCampo = new PdfPCell(new Phrase("ESTADO:", FONT_CAMPO));
        celdaCampo.setBackgroundColor(new BaseColor(240, 240, 240));
        celdaCampo.setPadding(5);
        tabla.addCell(celdaCampo);

        Font fontEstado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11,
                prueba.estaAprobado() ? BaseColor.GREEN : BaseColor.RED);

        celdaValor = new PdfPCell(new Phrase(prueba.obtenerEstado(), fontEstado));
        celdaValor.setBackgroundColor(new BaseColor(240, 240, 240));
        celdaValor.setPadding(5);
        tabla.addCell(celdaValor);

        documento.add(tabla);
    }

    /**
     * Agrega el pie de página
     */
    private static void agregarPiePagina(Document documento) throws DocumentException {
        documento.add(Chunk.NEWLINE);

        // Línea separadora
        LineSeparator linea = new LineSeparator();
        linea.setLineColor(BaseColor.LIGHT_GRAY);
        documento.add(linea);

        // Texto legal
        Paragraph piePagina = new Paragraph(
                "Este documento es generado electrónicamente por el Sistema de Licencias de Conducir del Ecuador.\n" +
                        "Documento válido únicamente con sello y firma de la autoridad competente.",
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)
        );
        piePagina.setAlignment(Element.ALIGN_CENTER);
        piePagina.setSpacingBefore(10);
        documento.add(piePagina);

        // Fecha de generación
        Paragraph fechaGen = new Paragraph(
                "Fecha de generación: " + java.time.LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                ),
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)
        );
        fechaGen.setAlignment(Element.ALIGN_CENTER);
        documento.add(fechaGen);
    }

    /**
     * Helper para agregar una fila a la tabla
     */
    private static void agregarFilaTabla(PdfPTable tabla, String campo, String valor) {
        PdfPCell celdaCampo = new PdfPCell(new Phrase(campo, FONT_CAMPO));
        celdaCampo.setBorder(Rectangle.NO_BORDER);
        celdaCampo.setPadding(5);
        tabla.addCell(celdaCampo);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, FONT_NORMAL));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPadding(5);
        tabla.addCell(celdaValor);
    }
}
