package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.Payment;

@Repository
public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceId(
            Long invoiceId);
}