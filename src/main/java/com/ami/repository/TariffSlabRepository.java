package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.TariffSlab;

@Repository
public interface TariffSlabRepository
        extends JpaRepository<TariffSlab, Long> {

    List<TariffSlab> findByTariffId(
            Long tariffId);
}