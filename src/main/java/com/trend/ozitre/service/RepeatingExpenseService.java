package com.trend.ozitre.service;

import com.trend.ozitre.entity.PaymentEntity;
import com.trend.ozitre.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepeatingExpenseService implements Job {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("Giderler İçin Tekrarlı İşlem Başlatıldı!!!");

        LocalDate today = LocalDate.now();
        int dayOfMonth = today.getDayOfMonth();

        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        int lastDayOfMonth = yearMonth.lengthOfMonth();
        List<PaymentEntity> paymentEntities = new ArrayList<>();

        if (dayOfMonth == 1) {
            System.out.println("Bugün işlem günü: " + today);
            paymentEntities = paymentRepository.findByRepeatInterval(1);
        } else if (dayOfMonth == 15) {
            System.out.println("Bugün işlem günü: " + today);
            paymentEntities = paymentRepository.findByRepeatInterval(2);
        } else if (dayOfMonth == lastDayOfMonth) {
            System.out.println("Bugün işlem günü: " + today);
            paymentEntities = paymentRepository.findByRepeatInterval(3);
        }

        for (PaymentEntity payment: paymentEntities) {
            PaymentEntity newExpense = getNewExpense(payment);

            // Eski ödemenin tekrarını kaldır...
            payment.setRepeatInterval(0);
            paymentRepository.save(payment);
            paymentRepository.save(newExpense);
        }
    }

    private static PaymentEntity getNewExpense(PaymentEntity payment) {
        PaymentEntity newExpense = new PaymentEntity();
        newExpense.setPaymentDate(new Date());
        newExpense.setPaymentStatus(payment.getPaymentStatus());
        newExpense.setPaymentType(payment.getPaymentType());
        newExpense.setPaymentQuantity(payment.getPaymentQuantity());
        newExpense.setPaymentAmount(payment.getPaymentAmount());
        newExpense.setRemainingAmount(payment.getRemainingAmount());
        newExpense.setPaymentMethodId(payment.getPaymentMethodId());
        newExpense.setCompanyId(payment.getCompanyId());
        newExpense.setRepeatInterval(payment.getRepeatInterval());
        newExpense.setExplanation(payment.getExplanation());
        newExpense.setCreatedDate(new Date());
        newExpense.setCreatedBy("SYSTEM");
        return newExpense;
    }
}
