package com.etim.atom.repositories;

import com.etim.atom.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, String> {
    Optional<Message> findByMessageUuid(String id);
}
