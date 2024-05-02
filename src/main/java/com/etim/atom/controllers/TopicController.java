package com.etim.atom.controllers;

import com.etim.atom.services.MessageService;
import com.etim.atom.requests.MessageRequest;
import com.etim.atom.requests.EmptyTopicRequest;
import com.etim.atom.requests.TopicWithMessageRequest;
import com.etim.atom.models.Topic;
import com.etim.atom.models.EmptyTopicResponse;
import com.etim.atom.services.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class TopicController {
    private final TopicService topicService;
    private final MessageService messageService;

    @Operation(summary = "Get all topics")
    @GetMapping("/topic")
    public List<EmptyTopicResponse> listAllTopics() {
        return topicService.show();
    }

    @Operation(summary = "Create topic")
    @PostMapping("/topic")
    public Topic createTopic(@RequestBody TopicWithMessageRequest topicWithMessageRequest) {
        EmptyTopicRequest emptyTopicRequest = topicWithMessageRequest.getEmptyTopicRequest();
        MessageRequest messageRequest = topicWithMessageRequest.getMessageRequest();
        Topic topic = topicService.save(emptyTopicRequest, messageRequest);
        messageService.save(messageRequest, topic.getTopicUuid());
        return topic;
    }

    @Operation(summary = "Create message in the topic")
    @PostMapping("/topic/{topicId}/message")
    public Topic createMessage(@PathVariable("topicId") String topicId,
                               @RequestBody MessageRequest messageRequest) {
        messageService.save(messageRequest, topicId);
        return topicService.findByUuid(topicId);
    }

    @Operation(summary = "Get all messages in the topic")
    @GetMapping("/topic/{topicId}")
    public Topic getTopic(@PathVariable("topicId") String topicId) {
        return topicService.findByUuid(topicId);
    }

    @Operation(summary = "Update topic")
    @PutMapping("/topic/{topicId}")
    public Topic updateTopic(@PathVariable("topicId") String topicId,
                             @RequestBody EmptyTopicRequest emptyTopicRequest) {
        return topicService.update(topicId, emptyTopicRequest);
    }

    @Operation(summary = "Delete topic")
    @DeleteMapping("/topic/{topicId}")
    public void deleteTopic(@PathVariable("topicId") String topicId) {
        topicService.delete(topicId);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
    }
}