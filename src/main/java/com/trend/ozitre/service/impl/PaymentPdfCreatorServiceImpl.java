package com.trend.ozitre.service.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.trend.ozitre.dto.EventWithPaymentDto;
import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.dto.PaymentDto;
import com.trend.ozitre.entity.*;
import com.trend.ozitre.model.*;
import com.trend.ozitre.repository.*;
import com.trend.ozitre.service.PaymentPdfCreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PaymentPdfCreatorServiceImpl implements PaymentPdfCreatorService {

    private final TeachersRepository teachersRepository;

    private final LessonsRepository lessonsRepository;

    private final StudentsRepository studentsRepository;

    private final PackageRepository packageRepository;

    Document document;
    PdfDocument pdfDocument;
    float fourCol=230f;
    float threeCol=190f;
    float twoCol=100f;
    float headerCol=0.2f;
    float twoCol100 = twoCol + 100f;
    float twoCol150 = twoCol + 150f;
    float[] oneColumnWidth = { 100f };
    float[] headerWidth = { headerCol, twoCol150};
    float[] twoColumnWidth = { twoCol100, twoCol};
    float[] threeColumnWidth = { threeCol, threeCol, threeCol };
    float[] fourColumnWidth = { fourCol, fourCol, fourCol, fourCol};
    float[] fullWidth = { threeCol * 3};

    @Override
    public void createDocument() throws IOException {
        String pdfPath = "yeniPdfName.pdf";
        PdfWriter pdfWriter = new PdfWriter(pdfPath);

        FontProgram fontProgram = FontProgramFactory.createFont();
        PdfFont font = PdfFontFactory.createFont(fontProgram, "Cp1254");
        pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        this.document = new Document(pdfDocument);
        this.document.setFont(font);
    }

    @Override
    public void createTnc(List<String> TncList,Boolean lastPage,String imagePath) {
        if(lastPage) {
            Table tb = new Table(fullWidth);
            //tb.addCell(new Cell().add("TERMS AND CONDITIONS\n").setBold().setBorder(Border.NO_BORDER));
            for (String tnc : TncList) {

                tb.addCell(new Cell().add(tnc).setBorder(Border.NO_BORDER));
            }

            document.add(tb);
        }else {
            pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new MyFooter(document,TncList,imagePath));
        }

        document.close();
    }

    @Override
    public void createEvent(List<EventWithPaymentDto> eventWithPaymentList,
                            StudentsEntity student,
                            List<PaymentDto> allPackagePayments) {
        float[] fullwidth = { fourCol * 4 };
        Table fourColTable = new Table(fourColumnWidth);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        BigDecimal totalPrice = BigDecimal.valueOf(student.getTotalPrice() == null ? 0 : student.getTotalPrice());
        BigDecimal advance    = BigDecimal.valueOf(student.getAdvancePrice() == null ? 0 : student.getAdvancePrice());

        long paidInstallments = 0L;
        long remainingInstallments = 0L;
        BigDecimal paidTotal = BigDecimal.ZERO;
        BigDecimal remainingTotal = BigDecimal.ZERO;

        for (PaymentDto p : allPackagePayments) {
            BigDecimal remain   = BigDecimal.valueOf(p.getRemainingAmount() == null ? 0 : p.getRemainingAmount());
            BigDecimal received = BigDecimal.valueOf(p.getAmountReceived() == null ? 0 : p.getAmountReceived());
            paidTotal = paidTotal.add(received);
            remainingTotal = remainingTotal.add(remain);
        }
        for (PaymentDto p : allPackagePayments) {
            long remain = p.getRemainingAmount() == null ? 0L : p.getRemainingAmount();
            long amount = p.getPaymentAmount() == null ? 0L : p.getPaymentAmount();
            boolean isPaid = (remain == 0 && amount > 0);
            if (isPaid) paidInstallments++; else remainingInstallments++;
        }

        Table summary = new Table(new float[]{ threeCol, threeCol, threeCol });
        summary.addCell(cellKeyVal("Toplam Paket Tutarı", fmt(totalPrice)));
        summary.addCell(cellKeyVal("Peşinat", fmt(advance)));
        summary.addCell(cellKeyVal("Ödenen Toplam", fmt(paidTotal)));
        summary.addCell(cellKeyVal("Kalan Toplam", fmt(remainingTotal)));
        summary.addCell(cellKeyVal("Ödenen Taksit", String.valueOf(paidInstallments)));
        summary.addCell(cellKeyVal("Kalan Taksit", String.valueOf(remainingInstallments)));

        document.add(new Paragraph("Paket Özeti").setBold());
        document.add(summary.setMarginBottom(12f));
        document.add(fullwidthDashedBorder(fullwidth));

        float totalSum = 0;
        float remainingTotalSum = 0;

        List<EventWithPaymentDto> packageRows = new ArrayList<>();
        List<EventWithPaymentDto> lessonRows  = new ArrayList<>();
        for (EventWithPaymentDto ewp : eventWithPaymentList) {
            EventsDto ev = ewp.getEvent();
            if (ev.getTeacherId() == null || ev.getLessonId() == null) {
                packageRows.add(ewp);
            } else {
                lessonRows.add(ewp);
            }
        }

        packageRows.sort((a, b) -> {
            int ra = rankForPackageTitle(a.getEvent().getTitle());
            int rb = rankForPackageTitle(b.getEvent().getTitle());
            if (ra != rb) return Integer.compare(ra, rb);
            Date da = a.getEvent().getDate();
            Date db = b.getEvent().getDate();
            return da.compareTo(db);
        });

        lessonRows.sort(Comparator.comparing(o -> o.getEvent().getDate()));

        List<EventWithPaymentDto> ordered = new ArrayList<>(packageRows);
        ordered.addAll(lessonRows);

        for (EventWithPaymentDto ewp : ordered) {
            EventsDto event = ewp.getEvent();
            PaymentDto pay  = ewp.getPayment();

            if (event.getTeacherId() != null && event.getLessonId() != null) {
                try {
                    LessonEntity lessonEntity = lessonsRepository.getReferenceById(event.getLessonId());
                    TeacherEntity teacherEntity = teachersRepository.getReferenceById(event.getTeacherId());
                    fourColTable.addCell(new Cell().add(lessonEntity.getLesson().trim() + " danışmanlığı").setFontSize(8)).setWidth(400);
                    fourColTable.addCell(new Cell().add(teacherEntity.getTeacherName() + " " + teacherEntity.getTeacherSurname())
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setWidth(520);
                    fourColTable.addCell(new Cell().add(df.format(event.getDate()))
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setWidth(520);
                    fourColTable.addCell(new Cell().add((pay.getPaymentAmount() == null ? 0 : pay.getPaymentAmount()) + " tl")
                            .setTextAlignment(TextAlignment.RIGHT).setFontSize(8)).setWidth(520);

                    totalSum += pay.getPaymentAmount() == null ? 0 : pay.getPaymentAmount();
                    remainingTotalSum += pay.getRemainingAmount() == null ? 0 : pay.getRemainingAmount();
                } catch (Exception ignore) {}
            } else {
                StudentsEntity s = studentsRepository.getReferenceById(event.getStudentId());
                PackageEntity pkg = packageRepository.getReferenceById(s.getPackageId());

                String monthName = event.getDate().toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate().getMonth()
                        .getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("tr"));

                String installmentLabel;
                if (isAdvanceTitle(event.getTitle())) {
                    installmentLabel = "Peşinat";
                } else if (event.getTitle() != null && event.getTitle().contains("Taksit")) {
                    installmentLabel = event.getTitle().replace("Paket Dersi Düzenli - ", "");
                } else {
                    installmentLabel = "—";
                }

                fourColTable.addCell(new Cell().add(pkg.getPackageName().trim()).setFontSize(8)).setWidth(520);
                fourColTable.addCell(new Cell().add(monthName).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setWidth(400);
                fourColTable.addCell(new Cell().add(installmentLabel).setTextAlignment(TextAlignment.RIGHT).setFontSize(8)).setWidth(520);
                fourColTable.addCell(new Cell().add((pay.getPaymentAmount() == null ? 0 : pay.getPaymentAmount()) + " tl")
                        .setTextAlignment(TextAlignment.RIGHT).setFontSize(8)).setWidth(520);

                totalSum += pay.getPaymentAmount() == null ? 0 : pay.getPaymentAmount();
                remainingTotalSum += pay.getRemainingAmount() == null ? 0 : pay.getRemainingAmount();
            }
        }

        document.add(new Paragraph("Aylık Detay").setBold().setMarginTop(10f));
        document.add(fourColTable.setMarginBottom(20f));

        float[] oneTwo = { threeCol + 125f, threeCol * 2 };
        Table threeColTable4 = new Table(oneTwo);
        threeColTable4.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        threeColTable4.addCell(new Cell().add(fullwidthDashedBorder(fullwidth)).setBorder(Border.NO_BORDER));
        document.add(threeColTable4);

        Table totals = new Table(threeColumnWidth);
        totals.addCell(new Cell().add("").setBorder(Border.NO_BORDER)).setMarginLeft(10f);
        totals.addCell(new Cell().add("Aylık Toplam").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        totals.addCell(new Cell().add(String.valueOf(totalSum) + " tl").setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)).setMarginRight(15f);
        totals.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        totals.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        totals.addCell(new Cell().add(fullwidthDashedBorder(fullwidth)).setBorder(Border.NO_BORDER));
        totals.addCell(new Cell().add("").setBorder(Border.NO_BORDER)).setMarginLeft(10f);
        totals.addCell(new Cell().add("Aylık Kalan Toplam").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        totals.addCell(new Cell().add(String.valueOf(remainingTotalSum) + " tl").setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)).setMarginRight(15f);

        document.add(totals);
        document.add(fullwidthDashedBorder(fullwidth));
        document.add(new Paragraph("\n"));
        document.add(getDividerTable(fullwidth).setBorder(new SolidBorder(Color.GRAY,1)).setMarginBottom(15f));
    }

    private String fmt(BigDecimal v) { return v == null ? "0 tl" : v.toPlainString() + " tl"; }
    private Cell cellKeyVal(String key, String val) {
        Table t = new Table(new float[]{ threeCol, threeCol });
        t.addCell(new Cell().add(key).setBorder(Border.NO_BORDER).setFontSize(9).setBold());
        t.addCell(new Cell().add(val).setBorder(Border.NO_BORDER).setFontSize(9).setTextAlignment(TextAlignment.RIGHT));
        return new Cell().add(t).setBorder(Border.NO_BORDER).setPadding(4);
    }

    private boolean isAdvanceTitle(String title) {
        return title != null && title.trim().equalsIgnoreCase("Peşinat");
    }

    private int rankForPackageTitle(String title) {
        if (isAdvanceTitle(title)) return 0;

        if (title != null) {
            Matcher m = Pattern.compile("Taksit\\s+(\\d+)").matcher(title);
            if (m.find()) {
                try { return Integer.parseInt(m.group(1)); } catch (NumberFormatException ignore) {}
            }
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public void createTableHeader(ProductTableHeader productTableHeader) {
        Paragraph productsParagraph = new Paragraph("Alınan Dersler");
        document.add(productsParagraph.setBold());
        Table fourColTable = new Table(fourColumnWidth);
        fourColTable.setBackgroundColor(Color.BLACK,0.7f);

        fourColTable.addCell(new Cell().add(productTableHeader.getLesson()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setFontSize(10)).setWidth(4000);
        fourColTable.addCell(new Cell().add(productTableHeader.getTeacher()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        fourColTable.addCell(new Cell().add(productTableHeader.getDate()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        fourColTable.addCell(new Cell().add(productTableHeader.getPrice()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        document.add(fourColTable);
    }

    @Override
    public void createTableHeader(PackageTableHeader packageTableHeader) {
        Paragraph productsParagraph = new Paragraph("Alınan Paketler");
        document.add(productsParagraph.setBold());
        Table fourColTable = new Table(fourColumnWidth);
        fourColTable.setBackgroundColor(Color.BLACK,0.7f);

        fourColTable.addCell(new Cell().add(packageTableHeader.getPackageName()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setFontSize(10)).setWidth(4000);
        fourColTable.addCell(new Cell().add(packageTableHeader.getMonth()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        fourColTable.addCell(new Cell().add(packageTableHeader.getInstallment()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        fourColTable.addCell(new Cell().add(packageTableHeader.getPrice()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        document.add(fourColTable);
    }

    @Override
    public void createAddress(Long studentId) {
        StudentsEntity studentsEntity = studentsRepository.getReferenceById(studentId);
        AddressDetails addressDetails=new AddressDetails();
        addressDetails.setStudentFullName(studentsEntity.getName() + " " + studentsEntity.getSurname());
        if (studentsEntity.getParent() != null) addressDetails.setStudentParent(studentsEntity.getParent());
        addressDetails.setInvoiceEditDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        Table twoColTable= new Table(twoColumnWidth);
        twoColTable.addCell(getStudentandShippingCell(addressDetails.getStudentInfoText()));
        document.add(twoColTable.setMarginBottom(2f));

        Table twoColTable2=new Table(twoColumnWidth);
        twoColTable2.addCell(getCell10fLeft(addressDetails.getStudentFullNameText(),true));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getInvoiceEditDateText(),true));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getStudentFullName(),false));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getInvoiceEditDate(),false));
        document.add(twoColTable2);

        Table oneColTable = new Table(oneColumnWidth);
        oneColTable.addCell(getCell10fLeft(addressDetails.getStudentParentText(),true));
        oneColTable.addCell(getCell10fLeft(addressDetails.getStudentParent(),false));
        document.add(oneColTable.setMarginBottom(10f));

        document.add(fullwidthDashedBorder(fullWidth));
    }

    @Override
    public void createHeader(HeaderDetails header) {
        Table table=new Table(headerWidth);
        table.addCell(getHeaderImageCell(header.getImageBanner()));
        //table.addCell(new Cell().add(header.getInvoiceTitle()).setFontSize(20f).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        Table nestable=new Table(new float[]{headerCol});
        nestable.addCell(getHeaderAddress("Taşdelen, Güngören Mahallesi, Sultan Nigar Sk. No:2, 34782 Çekmeköy/Istanbul"));
        table.addCell(new Cell().add(nestable).setBorder(Border.NO_BORDER));
        Border gb = new SolidBorder(header.getBorderColor(),2f);
        document.add(table.setMarginBottom(2));
        document.add(getDividerTable(fullWidth).setBorder(gb).setMarginBottom(7));
    }

    static  Table getDividerTable(float[] fullwidth)
    {
        return new Table(fullwidth);
    }
    static Table fullwidthDashedBorder(float[] fullwidth)
    {
        Table tableDivider2=new Table(fullwidth);
        Border dgb=new DashedBorder(Color.GRAY,0.5f);
        tableDivider2.setBorder(dgb);
        return tableDivider2;
    }
    static Cell getHeaderAddress(String textValue)
    {
        return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setFontSize(9f);
    }
    static Cell getHeaderImageCell(Image image)
    {
        return new Cell().add(image).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }
    static Cell getStudentandShippingCell(String textValue)
    {
        return new Cell().add(textValue).setFontSize(12f).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static  Cell getCell10fLeft(String textValue,Boolean isBold){
        Cell myCell=new Cell().add(textValue).setFontSize(10f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        return  isBold ?myCell.setBold():myCell;
    }

}
