package com.trend.ozitre.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PaymentMethods")
@Data
public class PaymentMethodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "payment_method_id")
    private Long paymentMethodId;
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
}
