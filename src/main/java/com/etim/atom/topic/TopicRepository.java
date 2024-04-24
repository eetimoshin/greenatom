package com.etim.atom.topic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, String> {
    Optional<Topic> findByTopicUuid(String id);

}