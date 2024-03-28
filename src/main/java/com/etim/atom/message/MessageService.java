package com.etim.atom.message;

import com.etim.atom.topic.Topic;
import com.etim.atom.topic.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@AllArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    private final TopicRepository topicRepository;

    public Message save(Message message, String topicId) {
        message.setCreatedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());

        message.setTopic(topicRepository.findByTopicUuid(topicId).orElse(null));

        UserDetails personDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        message.setAuthor(personDetails.getUsername());

        return messageRepository.save(message);
    }

    public ResponseEntity<?> update(String messageIdToUpdate, Message updatedMessage) {
        Message messageToUpdate = messageRepository.findByMessageUuid(messageIdToUpdate).orElse(null);

        if (messageToUpdate == null) {
            return ResponseEntity.status(400).body("Invalid ID supplied");
        }

        UserDetails personDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        boolean isAdmin = personDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        if (personDetails.getUsername().equals(messageToUpdate.getAuthor()) || isAdmin) {
            messageToUpdate.setText(updatedMessage.getText());
            messageRepository.save(messageToUpdate);
        }
        return ResponseEntity.status(200).body(topicRepository.
                findByTopicUuid(messageToUpdate.getTopic().getTopicUuid()));
    }

    public ResponseEntity<?> delete(String id) {
        Message message = messageRepository.findByMessageUuid(id).orElse(null);

        if (message == null) {
            return ResponseEntity.status(400).body("Invalid ID supplied");
        }

        UserDetails personDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        boolean isAdmin = personDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        if (personDetails.getUsername().equals(message.getAuthor()) || isAdmin) {
            messageRepository.delete(message);
        }
        return ResponseEntity.status(204).build();
    }
}
