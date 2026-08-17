package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.ChatConversation;

@Repository
public interface ChatConversationRepository
        extends JpaRepository<ChatConversation, Long> {

    List<ChatConversation> findByUserId(
            String userId);

    List<ChatConversation> findByModule(
            String module);

    List<ChatConversation>
    findByUserIdOrderByCreatedAtDesc(
            String userId);
}