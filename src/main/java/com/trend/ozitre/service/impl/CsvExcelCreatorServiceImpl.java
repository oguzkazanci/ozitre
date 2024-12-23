package com.trend.ozitre.service.impl;

import com.trend.ozitre.entity.*;
import com.trend.ozitre.repository.LessonsRepository;
import com.trend.ozitre.repository.PaymentRepository;
import com.trend.ozitre.repository.StudentsRepository;
import com.trend.ozitre.repository.TeachersRepository;
import com.trend.ozitre.service.CsvExcelCreatorService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvExcelCreatorServiceImpl implements CsvExcelCreatorService {

    private final StudentsRepository studentsRepository;

    private final TeachersRepository teachersRepository;

    private final LessonsRepository lessonsRepository;

    private final PaymentRepository paymentRepository;

    @Override
    public void createPaymentCsv(List<EventsEntity> eventsEntities) {
        try {
            List<String[]> category = paymentList(eventsEntities);

            File csvFile = new File("paymentCsv.csv");
            FileWriter fileWriter = new FileWriter(csvFile);
            for (String[] data : category) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < data.length; i++) {
                    line.append("\"");
                    line.append(data[i].replaceAll("\"","\"\""));
                    line.append("\"");
                    if (i != data.length - 1) {
                        line.append(',');
                    }
                }
                line.append("\n");
                fileWriter.write(line.toString());
            }
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ByteArrayOutputStream createPaymentExcel(List<EventsEntity> eventsEntities, Long studentId) throws IOException {
        StudentsEntity studentsEntity = studentsRepository.getReferenceById(studentId);
        List<String[]> paymentList = paymentList(eventsEntities);
        String[] headerLine = paymentList.get(0);
        paymentList.remove(0);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Workbook workBook = new XSSFWorkbook();
        Sheet sheet = workBook.createSheet("Ödeme");

        Font headerFont = workBook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 16);

        CellStyle headerCellStyle = workBook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle tableCellStyle = workBook.createCellStyle();
        tableCellStyle.setBorderTop(BorderStyle.THIN);
        tableCellStyle.setBorderBottom(BorderStyle.THIN);
        tableCellStyle.setBorderLeft(BorderStyle.THIN);
        tableCellStyle.setBorderRight(BorderStyle.THIN);
        tableCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        tableCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        tableCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle tableCellStyle2 = workBook.createCellStyle();
        tableCellStyle2.setBorderTop(BorderStyle.THIN);
        tableCellStyle2.setBorderBottom(BorderStyle.THIN);
        tableCellStyle2.setBorderLeft(BorderStyle.THIN);
        tableCellStyle2.setBorderRight(BorderStyle.THIN);
        tableCellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
        tableCellStyle2.setAlignment(HorizontalAlignment.RIGHT);
        tableCellStyle2.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        tableCellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle totalCellStyle = workBook.createCellStyle();
        totalCellStyle.setBorderBottom(BorderStyle.THIN);
        totalCellStyle.setBorderLeft(BorderStyle.THIN);
        totalCellStyle.setBorderRight(BorderStyle.THIN);
        totalCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        totalCellStyle.setAlignment(HorizontalAlignment.RIGHT);

        Font infoHeaderFont = workBook.createFont();
        infoHeaderFont.setBold(true);
        infoHeaderFont.setFontHeightInPoints((short) 16);

        Font infoFont = workBook.createFont();
        infoFont.setBold(true);
        infoFont.setFontHeightInPoints((short) 10);

        Font contentFont = workBook.createFont();
        contentFont.setFontHeightInPoints((short) 10);

        CellStyle textInvesioCellStyle = workBook.createCellStyle();
        textInvesioCellStyle.setFont(infoHeaderFont);
        textInvesioCellStyle.setAlignment(HorizontalAlignment.CENTER);
        textInvesioCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle infoHeaderCellStyle = workBook.createCellStyle();
        infoHeaderCellStyle.setFont(infoHeaderFont);

        CellStyle infoCellStyle = workBook.createCellStyle();
        infoCellStyle.setFont(infoFont);

        CellStyle contentCellStyle = workBook.createCellStyle();
        contentCellStyle.setFont(contentFont);

        Row brandRow = sheet.createRow(0);
        brandRow.setHeight((short) 1000);
        brandRow.createCell(3).setCellValue("Fatura");
        brandRow.setRowStyle(textInvesioCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));

        Row infoRow = sheet.createRow(2);
        infoRow.setHeight((short) 550);
        infoRow.createCell(0).setCellValue("Öğrenci Bilgisi");
        infoRow.setRowStyle(infoHeaderCellStyle);

        Row sRow = sheet.createRow(3);
        sRow.setHeight((short) 500);
        Cell stuFNTextCell = sRow.createCell(0);
        stuFNTextCell.setCellValue("Adı Soyadı");
        stuFNTextCell.setCellStyle(infoCellStyle);
        Cell stuFNCell = sRow.createCell(1);
        stuFNCell.setCellValue(studentsEntity.getName() + " " + studentsEntity.getSurname());
        stuFNCell.setCellStyle(contentCellStyle);
        Cell editDateTextCell = sRow.createCell(2);
        editDateTextCell.setCellValue("Düzenlenme Tarihi");
        editDateTextCell.setCellStyle(infoCellStyle);
        Cell editDateCell = sRow.createCell(3);
        editDateCell.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        editDateCell.setCellStyle(contentCellStyle);

        Row spRow = sheet.createRow(4);
        spRow.setHeight((short) 500);
        Cell stuPTextCell = spRow.createCell(0);
        stuPTextCell.setCellValue("Veli");
        stuPTextCell.setCellStyle(infoCellStyle);
        Cell stuPCell = spRow.createCell(1);
        stuPCell.setCellValue(studentsEntity.getParent());
        stuPCell.setCellStyle(contentCellStyle);

        Row emptyRow = sheet.createRow(5);
        emptyRow.setHeight((short) 500);

        Row headerRow = sheet.createRow(6);
        headerRow.setHeight((short) 600);

        for (int i = 0; i < headerLine.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerLine[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 7;
        int total = 0;

        for (String[] data : paymentList) {
            if (rowNum %2==1) {
                Row row = sheet.createRow(rowNum++);
                row.setHeight((short) 450);
                Cell rowOne = row.createCell(0);
                rowOne.setCellStyle(tableCellStyle);
                rowOne.setCellValue(data[0]);
                Cell rowTwo = row.createCell(1);
                rowTwo.setCellStyle(tableCellStyle);
                rowTwo.setCellValue(data[1]);
                Cell rowThree = row.createCell(2);
                rowThree.setCellStyle(tableCellStyle);
                rowThree.setCellValue(data[2]);
                Cell rowFour = row.createCell(3);
                rowFour.setCellStyle(tableCellStyle);
                rowFour.setCellValue(data[3] + " ₺");
                total = total + Integer.parseInt(data[3]);
            } else {
                Row row = sheet.createRow(rowNum++);
                row.setHeight((short) 450);
                Cell rowOne = row.createCell(0);
                rowOne.setCellStyle(tableCellStyle2);
                rowOne.setCellValue(data[0]);
                Cell rowTwo = row.createCell(1);
                rowTwo.setCellStyle(tableCellStyle2);
                rowTwo.setCellValue(data[1]);
                Cell rowThree = row.createCell(2);
                rowThree.setCellStyle(tableCellStyle2);
                rowThree.setCellValue(data[2]);
                Cell rowFour = row.createCell(3);
                rowFour.setCellStyle(tableCellStyle2);
                rowFour.setCellValue(data[3] + " ₺");
                total = total + Integer.parseInt(data[3]);
            }
        }

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));
        Row totalRow = sheet.createRow(rowNum);
        totalRow.setHeight((short) 600);
        Cell totalCellText = totalRow.createCell(0);
        totalCellText.setCellStyle(totalCellStyle);
        totalCellText.setCellValue("Toplam Tutar");
        Cell totalCell = totalRow.createCell(3);
        totalCell.setCellValue(total + " ₺");
        totalCell.setCellStyle(totalCellStyle);

        String imagePath = "/trendders-logo-xl.png";
        InputStream my_banner_image = getClass().getResourceAsStream(imagePath);
        byte[] bytes = IOUtils.toByteArray(my_banner_image);
        int my_picture_id = workBook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        my_banner_image.close();
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor my_anchor = new XSSFClientAnchor();

        my_anchor.setCol1(0);
        my_anchor.setRow1(0);
        my_anchor.setCol2(2);
        my_anchor.setRow2(1);

        XSSFPicture my_picture = drawing.createPicture(my_anchor, my_picture_id);

        for (int i = 0; i < headerLine.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workBook.write(stream);
        workBook.close();

        return stream;
    }

    private List<String[]> paymentList(List<EventsEntity> eventsEntities) {
        List<String[]> paymentList = new ArrayList<>();
        String[] headerLine = new String[]{"    DERS    ", "     ÖĞRETMEN     ", "    DERS TARİHİ    ", "   DERS ÜCRETİ    "};
        paymentList.add(0, headerLine);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        for (EventsEntity product:eventsEntities)
        {
            LessonEntity lessonEntity = lessonsRepository.getReferenceById(product.getLessonId());
            TeacherEntity teacherEntity = teachersRepository.getReferenceById(product.getTeacherId());
            PaymentEntity paymentEntity = paymentRepository.findByEventIdAndPaymentType(product.getEventId(), 0);
            String[] stringList = new String[]{lessonEntity.getLesson(), teacherEntity.getTeacherName() + " " + teacherEntity.getTeacherSurname(), df.format(product.getDate()), paymentEntity.getPaymentAmount().toString()};

            paymentList.add(stringList);
        }

        return paymentList;
    }
}
