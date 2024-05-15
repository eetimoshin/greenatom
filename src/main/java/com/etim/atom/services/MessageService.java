package com.etim.atom.services;

import com.etim.atom.models.Message;
import com.etim.atom.repositories.MessageRepository;
import com.etim.atom.requests.MessageRequest;
import com.etim.atom.models.Topic;
import com.etim.atom.repositories.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    private final TopicRepository topicRepository;

    public Message save(MessageRequest messageRequest, String topicId) {
        validateMessageRequest(messageRequest);
        Message message = new Message();
        message.setText(messageRequest.text());
        message.setCreatedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
        message.setTopic(topicRepository.findByTopicUuid(topicId).orElse(null));
        message.setAuthor(getCurrentUsername());
        return messageRepository.save(message);
    }

    public Message update(MessageRequest updatedMessageRequest, String messageIdToUpdate) {
        Message messageToUpdate = findByUuid(messageIdToUpdate);
        validateMessageRequest(updatedMessageRequest);

        if (getCurrentUsername().equals(messageToUpdate.getAuthor()) || ifAdmin()) {
            messageToUpdate.setText(updatedMessageRequest.text());
            return messageRepository.save(messageToUpdate);
        }

        return null;
    }

    public Topic update(String messageIdToUpdate, MessageRequest updatedMessageRequest) {
        Message messageToUpdate = findByUuid(messageIdToUpdate);
        validateMessageRequest(updatedMessageRequest);

        if (getCurrentUsername().equals(messageToUpdate.getAuthor()) || ifAdmin()) {
            messageToUpdate.setText(updatedMessageRequest.text());
            messageRepository.save(messageToUpdate);
        }

        return topicRepository.findByTopicUuid(messageToUpdate.getTopic().getTopicUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
    }

    public void delete(String id) {
        Message message = findByUuid(id);
        if (getCurrentUsername().equals(message.getAuthor()) || ifAdmin()) {
            messageRepository.delete(message);
        }
    }

    private String getCurrentUsername() {
        UserDetails personDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return personDetails.getUsername();
    }

    private Boolean ifAdmin() {
        UserDetails personDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return personDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
    }

    private void validateMessageRequest(MessageRequest messageRequest) {
        if (messageRequest.text().isEmpty() || messageRequest.text().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Text of message is invalid");
        }
    }

    private Message findByUuid(String id) {
        return messageRepository.findByMessageUuid(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + id));
    }
}
