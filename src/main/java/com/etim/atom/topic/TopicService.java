package com.etim.atom.topic;

import com.etim.atom.message.Message;
import com.etim.atom.requests.MessageRequest;
import com.etim.atom.requests.TopicRequest;
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

    public Topic save(TopicRequest topicRequest, MessageRequest messageRequest) {
        validateTopicRequest(topicRequest);
        if (messageRequest.text().isEmpty() || messageRequest.text().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Text of message is invalid");
        }
        Message message = new Message();
        message.setText(messageRequest.text());
        Topic topic = new Topic();
        topic.setTopicName(topicRequest.topicName());
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

    public Topic findByUuid(String id) {
        return topicRepository.findByTopicUuid(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
    }

    public Topic update(String id, Topic topic) {
        Topic newTopic = findByUuid(id);

        validateTopic(topic);

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

    private void validateTopicRequest(TopicRequest topicRequest) {
        if (topicRequest.topicName().isEmpty() || topicRequest.topicName().length() > 20){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic invalid");
        }
    }
}
