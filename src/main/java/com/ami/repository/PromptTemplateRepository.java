package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.PromptTemplate;

@Repository
public interface PromptTemplateRepository
        extends JpaRepository<PromptTemplate, Long> {

    List<PromptTemplate> findByModule(
            String module);

    List<PromptTemplate> findByActive(
            Boolean active);
}