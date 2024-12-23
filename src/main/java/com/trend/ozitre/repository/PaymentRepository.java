package com.trend.ozitre.repository;

import com.trend.ozitre.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity,Long> {

    PaymentEntity findByEventIdAndPaymentType(Long eventId, Integer paymentType);

    List<PaymentEntity> findByEventIdAndPaymentTypeAndPaymentStatus(Long eventId, Integer paymentType, Integer paymentStatus);

    @Query("SELECT SUM(p.paymentAmount) FROM PaymentEntity p where p.paymentStatus = 0 and p.paymentType = 0 and p.companyId = :#{#companyId}")
    Long sumTotalAmountOfReceivable(Long companyId);

    @Query("SELECT SUM(p.paymentAmount) FROM PaymentEntity p where p.paymentStatus = 1 and p.paymentMethodId = 0 and p.paymentType = 0 and p.companyId = :#{#companyId}")
    Long sumTotalAmountOfCash(Long companyId);

    @Query("SELECT SUM(p.paymentAmount) FROM PaymentEntity p where p.paymentStatus = 1 and p.paymentMethodId = 1 and p.paymentType = 0 and p.companyId = :#{#companyId}")
    Long sumTotalAmountOfTransfer(Long companyId);

    @Query("SELECT SUM(p.paymentAmount) FROM PaymentEntity p where p.paymentStatus = 1 and p.paymentType = 0 and p.companyId = :#{#companyId}")
    Long sumTotalAmountOfIncome(Long companyId);

    @Query("SELECT SUM(p.paymentAmount) FROM PaymentEntity p where p.paymentStatus = 1 and p.paymentType = 1 or p.paymentType = 2 and p.companyId = :#{#companyId}")
    Long sumTotalAmountOfExpense(Long companyId);

    @Query("SELECT COUNT(*), SUM(p.paymentAmount), p.paymentDate, p.paymentMethodId, p.paymentQuantity, p.explanation FROM PaymentEntity p WHERE p.createdDate BETWEEN :#{#startDate} AND :#{#endDate} AND p.paymentType = :#{#paymentType} AND p.paymentStatus = :#{#paymentStatus} AND p.companyId = :#{#companyId} GROUP BY p.paymentDate, p.paymentMethodId, p.paymentQuantity, p.explanation")
    List<String> expenseTeacherQuery(Long paymentStatus, Long paymentType, Date startDate, Date endDate, Long companyId);

    List<PaymentEntity> findByPaymentTypeAndPaymentStatusAndCreatedDateBetweenAndCompanyId(Integer paymentType, Integer paymentStatus,
                                                                                           Date endDate, Date startDate, Long companyId);

    List<PaymentEntity> findByRepeatInterval(Integer repeatInterval);
}
