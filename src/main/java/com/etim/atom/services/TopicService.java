package com.etim.atom.services;

import com.etim.atom.models.Message;
import com.etim.atom.requests.MessageRequest;
import com.etim.atom.requests.EmptyTopicRequest;
import com.etim.atom.models.Topic;
import com.etim.atom.models.EmptyTopicResponse;
import com.etim.atom.repositories.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public Topic save(EmptyTopicRequest emptyTopicRequest, MessageRequest messageRequest) {
        validateTopicRequest(emptyTopicRequest);
        if (messageRequest.text().isEmpty() || messageRequest.text().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Text of message is invalid");
        }
        Topic topic = new Topic();
        topic.setTopicName(emptyTopicRequest.topicName());
        topic.setCreatedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        Message message = new Message();
        message.setText(messageRequest.text());
        message.setCreatedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        message.setAuthor(getCurrentUsername());
        topic.addToMessages(message);
        return topicRepository.save(topic);
    }

    public List<EmptyTopicResponse> show() {
        List<Topic> topics = topicRepository.findAll();
        List<EmptyTopicResponse> newTopics = new ArrayList<>();
        for (Topic topic : topics) {
            EmptyTopicResponse newTopic = EmptyTopicResponse.builder()
                    .topicId(topic.getTopicUuid())
                    .topicName(topic.getTopicName())
                    .createdAt(topic.getCreatedAt())
                    .build();
            newTopics.add(newTopic);
        }
        return newTopics;
    }

    public Topic findByUuid(String id) {
        return topicRepository.findByTopicUuid(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
    }

    public Topic update(String id, EmptyTopicRequest emptyTopicRequest) {
        Topic newTopic = findByUuid(id);

        validateTopicRequest(emptyTopicRequest);
        Topic topic = new Topic();
        topic.setTopicName(emptyTopicRequest.topicName());
        newTopic.setTopicName(topic.getTopicName());
        return topicRepository.save(newTopic);
    }

    public void delete(String id) {
        UserDetails personDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        boolean isAdmin = personDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        if (isAdmin)
            topicRepository.deleteById(id);
    }

    private void validateTopicRequest(EmptyTopicRequest emptyTopicRequest) {
        if (emptyTopicRequest.topicName().isEmpty() || emptyTopicRequest.topicName().length() > 20){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic invalid");
        }
    }

    private String getCurrentUsername() {
        UserDetails personDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return personDetails.getUsername();
    }
}
