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

public class PDFGenerator {

    // ===================== FORMATOS =====================
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ===================== FUENTES =====================
    private static final Font FONT_TITULO =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);

    private static final Font FONT_SUBTITULO =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.BLACK);

    private static final Font FONT_CAMPO =
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);

    private static final Font FONT_NORMAL =
            FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);

    // ===================== MÉTODO PRINCIPAL =====================
    public static void generarLicenciaPDF(Licencia licencia,
                                          Conductor conductor,
                                          PruebaPsicometrica prueba,
                                          String rutaArchivo)
            throws DocumentException, IOException {

        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));

        documento.open();

        agregarEncabezado(documento);
        agregarDatosConductor(documento, conductor);
        agregarDatosLicencia(documento, licencia);

        if (prueba != null) {
            agregarDatosPrueba(documento, prueba);
        }

        agregarPiePagina(documento);
        documento.close();
    }

    // ===================== ENCABEZADO =====================
    private static void agregarEncabezado(Document documento) throws DocumentException {

        Paragraph titulo = new Paragraph("REPÚBLICA DEL ECUADOR", FONT_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);

        Paragraph subtitulo = new Paragraph("AGENCIA NACIONAL DE TRÁNSITO", FONT_SUBTITULO);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(subtitulo);

        Paragraph tipo = new Paragraph("LICENCIA DE CONDUCIR", FONT_SUBTITULO);
        tipo.setAlignment(Element.ALIGN_CENTER);
        tipo.setSpacingAfter(15);
        documento.add(tipo);

        LineSeparator linea = new LineSeparator();
        linea.setLineColor(BaseColor.BLUE);
        documento.add(linea);
        documento.add(Chunk.NEWLINE);
    }

    // ===================== DATOS CONDUCTOR =====================
    private static void agregarDatosConductor(Document documento, Conductor c)
            throws DocumentException {

        documento.add(new Paragraph("DATOS DEL CONDUCTOR", FONT_SUBTITULO));

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(15);

        agregarFilaTabla(tabla, "Cédula:", c.getCedula());
        agregarFilaTabla(tabla, "Nombres:", c.getNombres());
        agregarFilaTabla(tabla, "Apellidos:", c.getApellidos());
        agregarFilaTabla(tabla, "Fecha de Nacimiento:",
                c.getFechaNacimiento().format(FORMATO_FECHA));
        agregarFilaTabla(tabla, "Edad:", c.calcularEdad() + " años");

        documento.add(tabla);
    }

    // ===================== DATOS LICENCIA =====================
    private static void agregarDatosLicencia(Document documento, Licencia l)
            throws DocumentException {

        documento.add(new Paragraph("DATOS DE LA LICENCIA", FONT_SUBTITULO));

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(15);

        agregarFilaTabla(tabla, "Número:", l.getNumeroLicencia());
        agregarFilaTabla(tabla, "Tipo:",
                TipoLicenciaConstantes.obtenerNombre(l.getTipoLicencia()));
        agregarFilaTabla(tabla, "Emisión:",
                l.getFechaEmision().format(FORMATO_FECHA));
        agregarFilaTabla(tabla, "Vencimiento:",
                l.getFechaVencimiento().format(FORMATO_FECHA));
        agregarFilaTabla(tabla, "Estado:", l.obtenerEstado());

        documento.add(tabla);
    }

    // ===================== PRUEBA PSICOMÉTRICA =====================
    private static void agregarDatosPrueba(Document documento, PruebaPsicometrica p)
            throws DocumentException {

        documento.add(new Paragraph("PRUEBA PSICOMÉTRICA", FONT_SUBTITULO));

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);

        agregarFilaTabla(tabla, "Reacción:", String.valueOf(p.getNotaReaccion()));
        agregarFilaTabla(tabla, "Atención:", String.valueOf(p.getNotaAtencion()));
        agregarFilaTabla(tabla, "Coordinación:", String.valueOf(p.getNotaCoordinacion()));
        agregarFilaTabla(tabla, "Percepción:", String.valueOf(p.getNotaPercepcion()));
        agregarFilaTabla(tabla, "Psicológica:", String.valueOf(p.getNotaPsicologica()));

        documento.add(tabla);
    }

    // ===================== PIE DE PÁGINA =====================
    private static void agregarPiePagina(Document documento) throws DocumentException {

        documento.add(Chunk.NEWLINE);

        LineSeparator linea = new LineSeparator();
        linea.setLineColor(BaseColor.LIGHT_GRAY);
        documento.add(linea);

        Paragraph texto = new Paragraph(
                "Documento generado electrónicamente por el Sistema de Licencias",
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)
        );
        texto.setAlignment(Element.ALIGN_CENTER);
        documento.add(texto);
    }

    private static void agregarFilaTabla(PdfPTable tabla, String campo, String valor) {

        if (valor == null) valor = "N/A";

        PdfPCell c1 = new PdfPCell(new Phrase(campo, FONT_CAMPO));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(5);

        PdfPCell c2 = new PdfPCell(new Phrase(valor, FONT_NORMAL));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPadding(5);

        tabla.addCell(c1);
        tabla.addCell(c2);
    }
}
