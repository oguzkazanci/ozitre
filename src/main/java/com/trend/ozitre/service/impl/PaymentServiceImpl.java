package com.trend.ozitre.service.impl;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.trend.ozitre.advice.UserNotFound;
import com.trend.ozitre.dto.EventWithPaymentDto;
import com.trend.ozitre.dto.EventsDto;
import com.trend.ozitre.dto.PaymentDto;
import com.trend.ozitre.dto.StudentsDto;
import com.trend.ozitre.dto.request.ExpenseRequest;
import com.trend.ozitre.entity.EventsEntity;
import com.trend.ozitre.entity.PaymentEntity;
import com.trend.ozitre.model.HeaderDetails;
import com.trend.ozitre.model.PackageTableHeader;
import com.trend.ozitre.model.ProductTableHeader;
import com.trend.ozitre.repository.EventsRepository;
import com.trend.ozitre.repository.PaymentRepository;
import com.trend.ozitre.service.CsvExcelCreatorService;
import com.trend.ozitre.service.EventsService;
import com.trend.ozitre.service.PaymentPdfCreatorService;
import com.trend.ozitre.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final EventsRepository eventsRepository;

    @Autowired
    @Lazy
    private EventsService eventsService;

    private final PaymentPdfCreatorService paymentPdfCreatorService;

    private final CsvExcelCreatorService csvExcelCreatorService;

    private final ModelMapper modelMapper;

    @Override
    public PaymentDto savePayment(PaymentDto paymentDto, String username, Long companyId) {
        PaymentEntity payment = modelMapper.map(paymentDto, PaymentEntity.class);
        payment.setCreatedDate(new Date());
        payment.setCreatedBy(username);
        payment.setCompanyId(companyId);
        payment.setRemainingAmount(payment.getPaymentAmount());
        return modelMapper.map(paymentRepository.save(payment), PaymentDto.class);
    }

    @Override
    public PaymentDto updatePayment(PaymentDto paymentDto, String username) {
        PaymentEntity payment = paymentRepository.findByEventIdAndPaymentType(paymentDto.getEventId(), paymentDto.getPaymentType());
        payment.setPaymentDate(new Date());
        payment.setUpdatedDate(new Date());
        payment.setUpdatedBy(username);
        payment.setAmountReceived(paymentDto.getAmountReceived());
        if (Objects.equals(payment.getAmountReceived(), payment.getRemainingAmount())) {
            payment.setPaymentStatus(1);
            payment.setRemainingAmount(0L);
        } else {
            payment.setPaymentStatus(0);
            if (payment.getRemainingAmount() != null) {
                payment.setRemainingAmount(payment.getRemainingAmount() - payment.getAmountReceived());
            } else {
                payment.setRemainingAmount(payment.getPaymentAmount() - payment.getAmountReceived());
            }
        }
        if (payment.getPaymentStatus() == 0) {
            payment.setPaymentDate(null);
        }
        if (paymentDto.getPayBack() != null && paymentDto.getPayBack().equals(1)) {
            payment.setPaymentStatus(0);
            payment.setPaymentDate(null);
            payment.setRemainingAmount(payment.getPaymentAmount());
        }
        payment.setPaymentMethodId(paymentDto.getPaymentMethodId());
        payment.setExplanation(payment.getExplanation());
        return modelMapper.map(paymentRepository.save(payment), PaymentDto.class);
    }

    @Override
    public PaymentDto getPaymentByEventId(Long eventId, Integer paymentType) {
        PaymentEntity paymentEntity = paymentRepository.findByEventIdAndPaymentType(eventId, paymentType);
        if(!paymentEntity.getPaymentId().toString().isEmpty()) {
            return modelMapper.map(paymentEntity, PaymentDto.class);
        }
        else return null;
    }

    @Override
    public PaymentDto getPaymentByEventIdAndStatus(Long eventId, Integer paymentType, Integer paymentStatus) {
        List<PaymentEntity> paymentEntity = paymentRepository.findByEventIdAndPaymentTypeAndPaymentStatus(eventId, paymentType, paymentStatus);

        if(!paymentEntity.isEmpty()) {
            return modelMapper.map(paymentEntity.get(0), PaymentDto.class);
        }else
            return modelMapper.map(paymentEntity, PaymentDto.class);
    }

    @Override
    public byte[] getPaymentPdf(Long studentId, Integer month, Long seasonId) throws IOException, ParseException, URISyntaxException {
        URL res = getClass().getResource("/trendders-logo-xl.png");
        assert res != null;

        String imagePath;
        try (InputStream inputStream = res.openStream()) {
            File file = File.createTempFile("trendders-logo", ".png");
            file.deleteOnExit();
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            imagePath = file.getAbsolutePath();
        }

        paymentPdfCreatorService.createDocument();

        // Create Header start
        HeaderDetails header = new HeaderDetails();

        // Image nesnesini oluştur
        Image companyLogo = new Image(ImageDataFactory.create(new File(imagePath).toURL()));

        companyLogo.scale(0.4F, 0.4F);
        header.setImageBanner(companyLogo);
        header.setInvoiceDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        paymentPdfCreatorService.createHeader(header);
        //Header End

        //Create Address start
        paymentPdfCreatorService.createAddress(studentId);
        //Address end

        List<EventWithPaymentDto> eventsWithPayments = eventsService.getEventsByStudentId(studentId, seasonId, month);

        List<EventsDto> enrollmentEvents = new ArrayList<>();
        List<EventsDto> packageEvents = new ArrayList<>();
        for (EventWithPaymentDto eventWithPaymentDto: eventsWithPayments) {
            EventsDto event = eventWithPaymentDto.getEvent();
            if (event.getLessonId() != null && event.getTeacherId() != null) {
                enrollmentEvents.add(event);
            } else {
                packageEvents.add(event);
            }
        }
        if (!enrollmentEvents.isEmpty()) {
            ProductTableHeader productTableHeader = new ProductTableHeader();
            paymentPdfCreatorService.createTableHeader(productTableHeader);

            paymentPdfCreatorService.createEvent(eventsWithPayments);
        }
        if (!packageEvents.isEmpty()) {
            PackageTableHeader packageTableHeader = new PackageTableHeader();
            paymentPdfCreatorService.createTableHeader(packageTableHeader);

            paymentPdfCreatorService.createEvent(eventsWithPayments);
        }

        //Term and Condition Start
        List<String> TncList = new ArrayList<>();
        TncList.add("****** Mali Değeri Yoktur ******");
        //TncList.add("2. The Seller warrants the product for one (1) year from the date of shipment");
        paymentPdfCreatorService.createTnc(TncList, false, imagePath);

        // Term and condition end
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Path source = Paths.get("yeniPdfName.pdf");
        Files.copy(source, outputStream);
        Files.delete(source);

        return outputStream.toByteArray();
    }

    @Override
    public byte[] getPaymentExcel(Long studentId, Integer month, Long seasonId) throws IOException {
        List<Date> monthToDate = eventsService.monthToDate(month + 1, seasonId);
        List<EventsEntity> events = eventsRepository.findByDateBetweenAndStudentIdAndEventStatus(monthToDate.get(0), monthToDate.get(1),
                studentId, true);
        return csvExcelCreatorService.createPaymentExcel(events, studentId).toByteArray();
    }

    @Override
    public Long getTotalAmount(Long type, Long companyId) {
        if (type == 0) {
            return paymentRepository.sumTotalAmountOfReceivable(companyId);
        } else if (type == 1) {
            return paymentRepository.sumTotalAmountOfCash(companyId);
        } else if (type == 2){
            return paymentRepository.sumTotalAmountOfTransfer(companyId);
        } else if (type == 3){
            return paymentRepository.sumTotalAmountOfIncome(companyId);
        } else {
            return paymentRepository.sumTotalAmountOfExpense(companyId);
        }
    }

    @Override
    public List<PaymentDto> getExpenseTotalState(ExpenseRequest expenseRequest) {
        List<Date> getMonthToDate = eventsService.monthToDate(expenseRequest.getMonth() + 1, expenseRequest.getSeasonId());
        List<PaymentEntity> paymentList = paymentRepository.findByPaymentTypeAndPaymentStatusAndCreatedDateBetweenAndCompanyId(expenseRequest.getPaymentType().intValue(),
                expenseRequest.getPaymentStatus().intValue(), getMonthToDate.get(0), getMonthToDate.get(1), expenseRequest.getCompanyId());
        PaymentEntity teacherPayment = new PaymentEntity();
        teacherPayment.setPaymentAmount(0L);
        teacherPayment.setPaymentQuantity(0L);
        PaymentEntity teacherPayment1 = new PaymentEntity();
        teacherPayment1.setPaymentAmount(0L);
        teacherPayment1.setPaymentQuantity(0L);
        paymentList.removeIf(payment1 -> {
            if (payment1.getPaymentType().equals(1) && payment1.getPaymentStatus().equals(0)) {
                teacherPayment.setExplanation("Öğretmen");
                Long remainingAmount = payment1.getRemainingAmount();
                if (remainingAmount == null) remainingAmount = 0L;
                teacherPayment.setPaymentAmount(teacherPayment.getPaymentAmount() + remainingAmount);
                teacherPayment.setPaymentQuantity(teacherPayment.getPaymentQuantity() + 1);
                teacherPayment.setPaymentDate(payment1.getPaymentDate());
                teacherPayment.setPaymentMethodId(payment1.getPaymentMethodId());
                teacherPayment.setPaymentStatus(0);
                if (payment1.getRemainingAmount() != null && payment1.getRemainingAmount() > 0) {
                    teacherPayment1.setExplanation("Öğretmen");
                    Long receivedAmount = payment1.getAmountReceived();
                    if (receivedAmount == null) receivedAmount = 0L;
                    teacherPayment1.setPaymentAmount(teacherPayment1.getPaymentAmount() + receivedAmount);
                    teacherPayment1.setPaymentQuantity(teacherPayment1.getPaymentQuantity() + 1);
                    teacherPayment1.setPaymentDate(payment1.getPaymentDate());
                    teacherPayment1.setPaymentMethodId(payment1.getPaymentMethodId());
                    teacherPayment1.setPaymentStatus(1);
                }
                return true;
            } else if (payment1.getPaymentType().equals(1) && payment1.getPaymentStatus().equals(1)) {
                teacherPayment1.setExplanation("Öğretmen");
                Long receivedAmount = payment1.getAmountReceived();
                if (receivedAmount == null) receivedAmount = 0L;
                teacherPayment1.setPaymentAmount(teacherPayment1.getPaymentAmount() + receivedAmount);
                teacherPayment1.setPaymentQuantity(teacherPayment1.getPaymentQuantity() + 1);
                teacherPayment1.setPaymentDate(payment1.getPaymentDate());
                teacherPayment1.setPaymentMethodId(payment1.getPaymentMethodId());
                teacherPayment1.setPaymentStatus(1);
                return true;
            }
            return false;
        });
        if (teacherPayment.getPaymentAmount() > 0) {
            paymentList.add(teacherPayment);
        }
        if (teacherPayment1.getPaymentAmount() > 0) {
            paymentList.add(teacherPayment1);
        }
        return paymentList.stream().map(payment -> modelMapper.map(payment, PaymentDto.class)).collect(Collectors.toList());
    }
}
