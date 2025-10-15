package com.trend.ozitre.service.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
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
import com.trend.ozitre.entity.*;
import com.trend.ozitre.model.*;
import com.trend.ozitre.repository.*;
import com.trend.ozitre.service.PaymentPdfCreatorService;
import com.trend.ozitre.service.RegistryPdfCreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegistryPdfCreatorServiceImpl implements RegistryPdfCreatorService {

    private final PackageRepository packageRepository;

    private final EnrollmentRepository enrollmentRepository;

    Document document;
    PdfDocument pdfDocument;
    float fourCol = 230f;
    float threeCol = 190f;
    float twoCol = 100f;
    float headerCol = 0.2f;
    float twoCol100 = twoCol + 100f;
    float twoCol150 = twoCol + 150f;
    float[] oneColumnWidth = {100f};
    float[] headerWidth = {headerCol, twoCol150};
    float[] twoColumnWidth = {twoCol100, twoCol};
    float[] threeColumnWidth = {threeCol, threeCol, threeCol};
    float[] fourColumnWidth = {fourCol, fourCol, fourCol, fourCol};
    float[] fullWidth = {threeCol * 3};

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
    public void createTnc(List<String> TncList, Boolean lastPage, String imagePath) {
        if (lastPage) {
            Table tb = new Table(fullWidth);
            //tb.addCell(new Cell().add("TERMS AND CONDITIONS\n").setBold().setBorder(Border.NO_BORDER));
            for (String tnc : TncList) {

                tb.addCell(new Cell().add(tnc).setBorder(Border.NO_BORDER));
            }

            document.add(tb);
        } else {
            pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new MyFooter(document, TncList, imagePath));
        }

        document.close();
    }

    @Override
    public void createEvent(StudentsEntity studentEntity) {
        float[] fullwidth = {fourCol * 4};
        Table fourColTable = new Table(fourColumnWidth);

        List<EnrollmentEntity> enrollmentList =
                enrollmentRepository.getEnrollmentEntitiesByStudent_StudentIdAndStatus(studentEntity.getStudentId(), 0);

        BigDecimal totalSum = BigDecimal.ZERO;

        for (EnrollmentEntity enrollment : enrollmentList) {
            StringBuilder days = new StringBuilder();
            for (DayEntity day : enrollment.getDays()) {
                days.append(" ").append(day.getName());
            }
            fourColTable.addCell(new Cell().add(enrollment.getLesson().getLesson().trim() + " danışmanlığı").setFontSize(8)).setWidth(400);
            fourColTable.addCell(new Cell().add(enrollment.getTeacher().getTeacherName() + " " + enrollment.getTeacher().getTeacherSurname())
                    .setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setWidth(520);
            fourColTable.addCell(new Cell().add(String.valueOf(days)).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setWidth(520);
            fourColTable.addCell(new Cell().add(enrollment.getPrice() + " tl").setTextAlignment(TextAlignment.RIGHT).setFontSize(8)).setWidth(520);

            if (enrollment.getPrice() != null) {
                totalSum = totalSum.add(new BigDecimal(enrollment.getPrice().toString()));
            }
        }

        if (studentEntity.getPackageId() != null) {
            PackageEntity packageEntity = packageRepository.getReferenceById(studentEntity.getPackageId());

            int installmentCount = studentEntity.getInstallment() == null ? 0 : studentEntity.getInstallment();
            BigDecimal total = BigDecimal.valueOf(studentEntity.getTotalPrice() == null ? 0 : studentEntity.getTotalPrice());
            BigDecimal advance = BigDecimal.valueOf(studentEntity.getAdvancePrice() == null ? 0 : studentEntity.getAdvancePrice());

            int startMonth = studentEntity.getStartMonth() == null ? LocalDate.now().getMonthValue() : studentEntity.getStartMonth();
            String firstMonthName = Month.of((startMonth - 1 + 12) % 12 + 1)
                    .getDisplayName(TextStyle.FULL, new Locale("tr"));

            if (advance.compareTo(BigDecimal.ZERO) > 0) {
                fourColTable.addCell(new Cell().add(packageEntity.getPackageName().trim()).setFontSize(8)).setWidth(520);
                fourColTable.addCell(new Cell().add(firstMonthName).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setWidth(400);
                fourColTable.addCell(new Cell().add("Peşinat").setTextAlignment(TextAlignment.RIGHT).setFontSize(8)).setWidth(520);
                fourColTable.addCell(new Cell().add(advance.toPlainString() + " tl").setTextAlignment(TextAlignment.RIGHT).setFontSize(8)).setWidth(520);
                totalSum = totalSum.add(advance);
            }

            BigDecimal remaining = total.subtract(advance);
            if (remaining.compareTo(BigDecimal.ZERO) > 0 && installmentCount > 0) {
                BigDecimal[] divRem = remaining.divideAndRemainder(BigDecimal.valueOf(installmentCount));
                BigDecimal base = divRem[0];
                BigDecimal remainder = divRem[1];

                for (int inst = 1; inst <= installmentCount; inst++) {
                    int currentMonth = (startMonth + inst - 1);
                    currentMonth = ((currentMonth - 1) % 12) + 1;
                    String monthName = Month.of(currentMonth).getDisplayName(TextStyle.FULL, new Locale("tr"));

                    BigDecimal amount = base;
                    if (remainder.compareTo(BigDecimal.ZERO) > 0) {
                        amount = amount.add(BigDecimal.ONE);
                        remainder = remainder.subtract(BigDecimal.ONE);
                    }

                    fourColTable.addCell(new Cell().add(packageEntity.getPackageName().trim()).setFontSize(8)).setWidth(520);
                    fourColTable.addCell(new Cell().add(monthName).setTextAlignment(TextAlignment.CENTER).setFontSize(8)).setWidth(400);
                    fourColTable.addCell(new Cell().add(inst + "/" + installmentCount).setTextAlignment(TextAlignment.RIGHT).setFontSize(8)).setWidth(520);
                    fourColTable.addCell(new Cell().add(amount.toPlainString() + " tl").setTextAlignment(TextAlignment.RIGHT).setFontSize(8)).setWidth(520);

                    totalSum = totalSum.add(amount);
                }
            }
        }

        document.add(fourColTable.setMarginBottom(20f));

        float[] oneTwo = {threeCol + 125f, threeCol * 2};
        Table threeColTable4 = new Table(oneTwo);
        threeColTable4.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        threeColTable4.addCell(new Cell().add(fullwidthDashedBorder(fullwidth)).setBorder(Border.NO_BORDER));
        document.add(threeColTable4);

        Table threeColTable3 = new Table(threeColumnWidth);
        threeColTable3.addCell(new Cell().add("").setBorder(Border.NO_BORDER)).setMarginLeft(10f);
        threeColTable3.addCell(new Cell().add("Toplam Tutar").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        threeColTable3.addCell(new Cell().add(totalSum.toPlainString() + " tl").setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)).setMarginRight(15f);

        document.add(threeColTable3);
        document.add(fullwidthDashedBorder(fullwidth));
        document.add(new Paragraph("\n"));
        document.add(getDividerTable(fullwidth).setBorder(new SolidBorder(Color.GRAY, 1)).setMarginBottom(15f));
    }


    @Override
    public void createTableHeader(ProductTableHeader productTableHeader) {
        Paragraph productsParagraph = new Paragraph("Alınan Dersler");
        document.add(productsParagraph.setBold());
        Table fourColTable = new Table(fourColumnWidth);
        fourColTable.setBackgroundColor(Color.BLACK, 0.7f);

        fourColTable.addCell(new Cell().add(productTableHeader.getLesson()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setFontSize(10)).setWidth(4000);
        fourColTable.addCell(new Cell().add(productTableHeader.getTeacher()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        fourColTable.addCell(new Cell().add(productTableHeader.getDays()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        fourColTable.addCell(new Cell().add(productTableHeader.getPrice()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        document.add(fourColTable);
    }

    @Override
    public void createTableHeader(PackageTableHeader packageTableHeader) {
        Paragraph productsParagraph = new Paragraph("Alınan Paketler");
        document.add(productsParagraph.setBold());
        Table fourColTable = new Table(fourColumnWidth);
        fourColTable.setBackgroundColor(Color.BLACK, 0.7f);

        fourColTable.addCell(new Cell().add(packageTableHeader.getPackageName()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setFontSize(10)).setWidth(4000);
        fourColTable.addCell(new Cell().add(packageTableHeader.getMonth()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        fourColTable.addCell(new Cell().add(packageTableHeader.getInstallment()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        fourColTable.addCell(new Cell().add(packageTableHeader.getPrice()).setBorder(Border.NO_BORDER).setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setFontSize(10)).setWidth(520);
        document.add(fourColTable);
    }

    @Override
    public void createAddress(StudentsEntity studentsEntity) {
        LocalDate createDate = studentsEntity.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        AddressDetails addressDetails = new AddressDetails();
        addressDetails.setStudentFullName(studentsEntity.getName() + " " + studentsEntity.getSurname());
        if (studentsEntity.getParent() != null) addressDetails.setStudentParent(studentsEntity.getParent());
        addressDetails.setInvoiceEditDate(createDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        Table twoColTable = new Table(twoColumnWidth);
        twoColTable.addCell(getStudentandShippingCell(addressDetails.getStudentInfoText()));
        document.add(twoColTable.setMarginBottom(2f));

        Table twoColTable2 = new Table(twoColumnWidth);
        twoColTable2.addCell(getCell10fLeft(addressDetails.getStudentFullNameText(), true));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getRegistryDateText(), true));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getStudentFullName(), false));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getInvoiceEditDate(), false));
        document.add(twoColTable2);

        Table oneColTable = new Table(oneColumnWidth);
        oneColTable.addCell(getCell10fLeft(addressDetails.getStudentParentText(), true));
        oneColTable.addCell(getCell10fLeft(addressDetails.getStudentParent(), false));
        document.add(oneColTable.setMarginBottom(10f));

        document.add(fullwidthDashedBorder(fullWidth));
    }

    @Override
    public void createHeader(HeaderDetails header) {
        Table table = new Table(headerWidth);
        table.addCell(getHeaderImageCell(header.getImageBanner()));
        table.addCell(new Cell().add(header.getRegistryTitle()).setFontSize(20f).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        Table nestable = new Table(new float[]{headerCol});
        nestable.addCell(getHeaderAddress("Taşdelen, Güngören Mahallesi, Sultan Nigar Sk. No:2, 34782 Çekmeköy/Istanbul"));
        table.addCell(new Cell().add(nestable).setBorder(Border.NO_BORDER));
        Border gb = new SolidBorder(header.getBorderColor(), 2f);
        document.add(table.setMarginBottom(2));
        document.add(getDividerTable(fullWidth).setBorder(gb).setMarginBottom(7));
    }

    static Table getDividerTable(float[] fullwidth) {
        return new Table(fullwidth);
    }

    static Table fullwidthDashedBorder(float[] fullwidth) {
        Table tableDivider2 = new Table(fullwidth);
        Border dgb = new DashedBorder(Color.GRAY, 0.5f);
        tableDivider2.setBorder(dgb);
        return tableDivider2;
    }

    static Cell getHeaderAddress(String textValue) {
        return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setFontSize(9f);
    }

    static Cell getHeaderImageCell(Image image) {
        return new Cell().add(image).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static Cell getStudentandShippingCell(String textValue) {
        return new Cell().add(textValue).setFontSize(12f).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static Cell getCell10fLeft(String textValue, Boolean isBold) {
        Cell myCell = new Cell().add(textValue).setFontSize(10f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        return isBold ? myCell.setBold() : myCell;
    }

}
