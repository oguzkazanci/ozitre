package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Payments")
@Data
public class PaymentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "payment_id")
    private Long paymentId;
    @Column(name = "event_id")
    private Long eventId;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "payment_date", length = 20)
    private Date paymentDate;
    @Column(name= "payment_status", precision = 9, length = 2)
    private Integer paymentStatus = 0;
    @Column(name = "payment_amount", length = 6)
    private Long paymentAmount;
    @Column(name = "amount_received", length = 6)
    private Long amountReceived;
    @Column(name = "remaining_amount", length = 6)
    private Long remainingAmount;
    @Column(name = "payment_quantity", length = 4)
    private Long paymentQuantity;
    @Column(name = "payment_method_id")
    private Long paymentMethodId;
    @Column(name="payment_type", precision = 2, length = 2)
    private Integer paymentType;
    @Column(name = "explanation", length = 200)
    private String explanation;
    @Column(name = "repeat_interval", length = 1)
    private Integer repeatInterval;
    @Column(name = "company_id")
    private Long companyId;
}
