package com.etim.atom.topic;

import com.etim.atom.message.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public Topic save(Topic topic, Message message) {
        topic.setCreatedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        topic.addToMessages(message);
        return topicRepository.save(topic);
    }

    public List<TopicDTO> show() {
        List<Topic> topics = topicRepository.findAll();
        List<TopicDTO> newTopics = new ArrayList<>();
        for (Topic topic : topics) {
            TopicDTO newTopic = TopicDTO.builder()
                    .topicId(topic.getTopicUuid())
                    .topicName(topic.getTopicName())
                    .createdAt(topic.getCreatedAt())
                    .build();
            newTopics.add(newTopic);
        }
        return newTopics;
    }

    public Optional<Topic> findByUuid(String id) {
        return topicRepository.findByTopicUuid(id);
    }

    public ResponseEntity<?> getTopic(String id) {
        if (topicRepository.findByTopicUuid(id).isEmpty()) {
            return ResponseEntity.status(400).body("Invalid topic ID");
        }
        else return ResponseEntity.status(200).body(topicRepository.findByTopicUuid(id));
    }

    public Optional<Topic> findByName(String name) {
        return topicRepository.findByTopicName(name);
    }

    public ResponseEntity<?> update(String id, Topic topic) {
        Topic newTopic = topicRepository.findByTopicUuid(id).orElse(null);
        if (newTopic == null) {
            return ResponseEntity.status(400).body("Invalid ID supplied");
        }

        newTopic.setTopicName(topic.getTopicName());
        return ResponseEntity.status(200).body(topicRepository.save(newTopic));
    }

    public ResponseEntity<?> delete(String id) {
        if (topicRepository.findByTopicUuid(id).isEmpty()) {
            return ResponseEntity.status(400).body("Invalid ID supplied");
        }
        UserDetails personDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        boolean isAdmin = personDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        if (isAdmin)
            topicRepository.deleteById(id);

        return ResponseEntity.status(204).build();
    }
}
