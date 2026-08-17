package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.Tariff;

@Repository
public interface TariffRepository
        extends JpaRepository<Tariff, Long> {

    List<Tariff> findByActive(
            Boolean active);
}