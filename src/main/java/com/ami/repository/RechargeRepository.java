package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.Recharge;

@Repository
public interface RechargeRepository
        extends JpaRepository<Recharge, Long> {

    List<Recharge> findByCustomerId(
            String customerId);
}