package com.trend.ozitre.repository;

import com.trend.ozitre.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity,Long> {
}
