package com.etim.atom.topic;

import com.etim.atom.message.Message;
import com.etim.atom.message.MessageService;
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

    @GetMapping("/topic")
    public List<TopicDTO> listAllTopics() {
        return topicService.show();
    }

    @PostMapping("/topic")
    public Topic createTopic(@RequestBody NewTopicRequest request) {
        Topic topic = request.getTopic();
        Message message = request.getMessage();
        topicService.save(topic, message);
        messageService.save(message, topic.getTopicUuid());
        return topic;
    }

    @PostMapping("/topic/{topicId}/message")
    public Topic createMessage(@PathVariable("topicId") String topicId,
                               @RequestBody Message message) {
        messageService.save(message, topicId);
        return topicService.findByUuid(topicId);
    }

    @GetMapping("/topic/{topicId}")
    public Topic getTopic(@PathVariable("topicId") String topicId) {
        return topicService.findByUuid(topicId);
    }

    @PutMapping("/topic/{topicId}")
    public Topic updateTopic(@PathVariable("topicId") String topicId,
                             @RequestBody Topic topic) {
        return topicService.update(topicId, topic);
    }

    @DeleteMapping("/topic/{topicId}")
    public void deleteTopic(@PathVariable("topicId") String topicId) {
        topicService.delete(topicId);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
    }
}