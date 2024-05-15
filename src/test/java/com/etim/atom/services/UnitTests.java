package com.etim.atom.services;

import com.etim.atom.models.Message;
import com.etim.atom.models.Topic;
import com.etim.atom.repositories.MessageRepository;
import com.etim.atom.repositories.TopicRepository;
import com.etim.atom.requests.EmptyTopicRequest;
import com.etim.atom.requests.MessageRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnitTests {
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private TopicRepository topicRepository;
    @InjectMocks
    private MessageService messageService;
    @InjectMocks
    private TopicService topicService;

    @Test
    @Tag("unit")
    public void testCreateTopic() {
        setUser();
        String topicId = "test_topic_id";

        EmptyTopicRequest emptyTopicRequest = new EmptyTopicRequest("Test Topic");
        MessageRequest messageRequest = new MessageRequest("Hello!");

        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic savedTopic = invocation.getArgument(0);
            savedTopic.setTopicUuid(topicId);
            return savedTopic;
        });

        Topic savedTopic = topicService.save(emptyTopicRequest, messageRequest);

        assertNotNull(savedTopic);
        assertNotNull(savedTopic.getMessages());
        assertNotNull(savedTopic.getCreatedAt());
        assertEquals(topicId, savedTopic.getTopicUuid());
        assertEquals(emptyTopicRequest.topicName(), savedTopic.getTopicName());

        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    @Tag("unit")
    public void testUpdateTopic() {
        setUser();
        String topicId = "test_topic_id";

        EmptyTopicRequest emptyTopicRequest = new EmptyTopicRequest("Test Topic");
        MessageRequest messageRequest = new MessageRequest("Hello!");

        when(topicRepository.findByTopicUuid(topicId)).thenReturn(Optional.of(new Topic()));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic savedTopic = invocation.getArgument(0);
            savedTopic.setTopicUuid(topicId);
            return savedTopic;
        });

        topicService.save(emptyTopicRequest, messageRequest);

        EmptyTopicRequest topicRequestToUpdate = new EmptyTopicRequest("Updated topic name");
        Topic updatedTopic = topicService.update(topicId, topicRequestToUpdate);

        assertNotNull(updatedTopic);
        assertEquals(topicRequestToUpdate.topicName(), updatedTopic.getTopicName());

        verify(topicRepository, times(1)).findByTopicUuid(topicId);
        verify(topicRepository, times(2)).save(any(Topic.class));
    }

    @Test
    @Tag("unit")
    public void testDeleteTopicByUser() {
        setUser();
        String topicId = "test_topic_id";

        EmptyTopicRequest emptyTopicRequest = new EmptyTopicRequest("Test Topic");
        MessageRequest messageRequest = new MessageRequest("Hello!");

        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic savedTopic = invocation.getArgument(0);
            savedTopic.setTopicUuid(topicId);
            return savedTopic;
        });

        topicService.save(emptyTopicRequest, messageRequest);
        topicService.delete(topicId);

        verify(topicRepository, times(1)).save(any(Topic.class));
        verify(topicRepository, times(0)).deleteById(topicId);
    }

    @Test
    @Tag("unit")
    public void testDeleteTopicByAdmin() {
        setAdmin();
        String topicId = "test_topic_id";

        EmptyTopicRequest emptyTopicRequest = new EmptyTopicRequest("Test Topic");
        MessageRequest messageRequest = new MessageRequest("Hello!");

        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic savedTopic = invocation.getArgument(0);
            savedTopic.setTopicUuid(topicId);
            return savedTopic;
        });

        topicService.save(emptyTopicRequest, messageRequest);
        topicService.delete(topicId);

        verify(topicRepository, times(1)).save(any(Topic.class));
        verify(topicRepository, times(1)).deleteById(topicId);
    }

    @Test
    @Tag("unit")
    public void testSaveMessage() {
        setUser();
        MessageRequest messageRequestToSave = new MessageRequest("Test message");
        String topicId = "test_topic_id";
        String messageId = "test_message_id";

        when(topicRepository.findByTopicUuid(topicId)).thenReturn(Optional.of(new Topic()));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message savedMessage = invocation.getArgument(0);
            savedMessage.setMessageUuid(messageId);
            return savedMessage;
        });

        Message savedMessage = messageService.save(messageRequestToSave, topicId);

        assertNotNull(savedMessage);
        assertNotNull(savedMessage.getCreatedAt());
        assertNotNull(savedMessage.getTopic());
        assertNotNull(savedMessage.getAuthor());
        assertNotNull(savedMessage.getMessageUuid());
        assertEquals(messageId, savedMessage.getMessageUuid());
        assertNotNull(savedMessage.getText());
        assertEquals(messageRequestToSave.text(), savedMessage.getText());

        verify(topicRepository, times(1)).findByTopicUuid(topicId);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @Tag("unit")
    public void testUpdateOwnMessage() {
        setUser();
        MessageRequest messageRequestToSave = new MessageRequest("Test message");
        String topicId = "test_topic_id";
        String messageId = "test_message_id";

        when(topicRepository.findByTopicUuid(topicId)).thenReturn(Optional.of(new Topic()));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message savedMessage = invocation.getArgument(0);
            savedMessage.setMessageUuid(messageId);
            return savedMessage;
        });

        Message savedMessage = messageService.save(messageRequestToSave, topicId);
        when(messageRepository.findByMessageUuid(messageId)).thenReturn(Optional.of(savedMessage));

        MessageRequest messageRequestToUpdate = new MessageRequest("Updated text message");
        Message updatedMessage = messageService.update(messageRequestToUpdate, messageId);

        assertNotNull(updatedMessage);
        assertEquals(messageRequestToUpdate.text(), updatedMessage.getText());

        verify(topicRepository, times(1)).findByTopicUuid(topicId);
        verify(messageRepository, times(1)).findByMessageUuid(messageId);
        verify(messageRepository, times(2)).save(any(Message.class));
    }

    @Test
    @Tag("unit")
    public void testUpdateOtherUsersMessage() {
        setUser();
        MessageRequest messageRequestToSave = new MessageRequest("Test message");
        String topicId = "test_topic_id";
        String messageId = "test_message_id";
        String author = "other_user";

        when(topicRepository.findByTopicUuid(topicId)).thenReturn(Optional.of(new Topic()));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message savedMessage = invocation.getArgument(0);
            savedMessage.setMessageUuid(messageId);
            savedMessage.setAuthor(author);
            return savedMessage;
        });

        Message savedMessage = messageService.save(messageRequestToSave, topicId);
        when(messageRepository.findByMessageUuid(messageId)).thenReturn(Optional.of(savedMessage));

        MessageRequest messageRequestToUpdate = new MessageRequest("Updated text message");
        Message updatedMessage = messageService.update(messageRequestToUpdate, messageId);

        assertNull(updatedMessage);

        verify(topicRepository, times(1)).findByTopicUuid(topicId);
        verify(messageRepository, times(1)).findByMessageUuid(messageId);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @Tag("unit")
    public void testUpdateOtherUsersMessageByAdmin() {
        setAdmin();
        MessageRequest messageRequestToSave = new MessageRequest("Test message");
        String topicId = "test_topic_id";
        String messageId = "test_message_id";
        String author = "other_user";

        when(topicRepository.findByTopicUuid(topicId)).thenReturn(Optional.of(new Topic()));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message savedMessage = invocation.getArgument(0);
            savedMessage.setMessageUuid(messageId);
            savedMessage.setAuthor(author);
            return savedMessage;
        });

        Message savedMessage = messageService.save(messageRequestToSave, topicId);
        when(messageRepository.findByMessageUuid(messageId)).thenReturn(Optional.of(savedMessage));

        MessageRequest messageRequestToUpdate = new MessageRequest("Updated text message");
        Message updatedMessage = messageService.update(messageRequestToUpdate, messageId);

        assertNotNull(updatedMessage);
        assertEquals(messageRequestToUpdate.text(), updatedMessage.getText());

        verify(topicRepository, times(1)).findByTopicUuid(topicId);
        verify(messageRepository, times(1)).findByMessageUuid(messageId);
        verify(messageRepository, times(2)).save(any(Message.class));
    }

    @Test
    @Tag("unit")
    public void testDeleteOwnMessage() {
        setUser();
        MessageRequest messageRequestToSave = new MessageRequest("Test message");
        String topicId = "test_topic_id";
        String messageId = "test_message_id";

        when(topicRepository.findByTopicUuid(topicId)).thenReturn(Optional.of(new Topic()));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message savedMessage = invocation.getArgument(0);
            savedMessage.setMessageUuid(messageId);
            return savedMessage;
        });

        Message savedMessage = messageService.save(messageRequestToSave, topicId);
        when(messageRepository.findByMessageUuid(messageId)).thenReturn(Optional.of(savedMessage));

        messageService.delete(messageId);

        verify(messageRepository, times(1)).save(any(Message.class));
        verify(messageRepository, times(1)).findByMessageUuid(messageId);
        verify(messageRepository, times(1)).delete(savedMessage);
    }

    @Test
    @Tag("unit")
    public void testDeleteOtherUsersMessage() {
        setUser();
        MessageRequest messageRequestToSave = new MessageRequest("Test message");
        String topicId = "test_topic_id";
        String messageId = "test_message_id";
        String author = "other_user";

        when(topicRepository.findByTopicUuid(topicId)).thenReturn(Optional.of(new Topic()));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message savedMessage = invocation.getArgument(0);
            savedMessage.setMessageUuid(messageId);
            savedMessage.setAuthor(author);
            return savedMessage;
        });

        Message savedMessage = messageService.save(messageRequestToSave, topicId);
        when(messageRepository.findByMessageUuid(messageId)).thenReturn(Optional.of(savedMessage));

        messageService.delete(messageId);

        verify(messageRepository, times(1)).save(any(Message.class));
        verify(messageRepository, times(1)).findByMessageUuid(messageId);
        verify(messageRepository, times(0)).delete(savedMessage);
    }

    @Test
    @Tag("unit")
    public void testDeleteOtherUsersMessageByAdmin() {
        setAdmin();
        MessageRequest messageRequestToSave = new MessageRequest("Test message");
        String topicId = "test_topic_id";
        String messageId = "test_message_id";
        String author = "other_user";

        when(topicRepository.findByTopicUuid(topicId)).thenReturn(Optional.of(new Topic()));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message savedMessage = invocation.getArgument(0);
            savedMessage.setMessageUuid(messageId);
            savedMessage.setAuthor(author);
            return savedMessage;
        });

        Message savedMessage = messageService.save(messageRequestToSave, topicId);
        when(messageRepository.findByMessageUuid(messageId)).thenReturn(Optional.of(savedMessage));

        messageService.delete(messageId);

        verify(messageRepository, times(1)).save(any(Message.class));
        verify(messageRepository, times(1)).findByMessageUuid(messageId);
        verify(messageRepository, times(1)).delete(savedMessage);
    }

    private void setAdmin() {
        UserDetails userDetails = new User("admin", "password",
                Collections.singleton(new SimpleGrantedAuthority("ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }

    private void setUser() {
        UserDetails userDetails = new User("user", "password",
                Collections.singleton(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }
}
