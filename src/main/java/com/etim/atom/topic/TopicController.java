package com.etim.atom.topic;

import com.etim.atom.message.Message;
import com.etim.atom.message.MessageService;
import com.etim.atom.validation.MessageValidation;
import com.etim.atom.NewTopicRequest;
import com.etim.atom.validation.TopicValidation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class TopicController {
    private final TopicService topicService;
    private final MessageService messageService;
    private final TopicValidation topicValidator;
    private final MessageValidation messageValidator;

    @GetMapping("/topic")
    public ResponseEntity<?> listAllTopics() {
        return ResponseEntity.status(200).body(topicService.show());
    }

    @PostMapping("/topic")
    public ResponseEntity<?> createTopic(@RequestBody NewTopicRequest request, BindingResult br) {
        Topic topic = request.getTopic();
        Message message = request.getMessage();
        topicValidator.validate(topic, br);
        messageValidator.validate(message, br);
        if (br.hasErrors()) {
            return ResponseEntity.status(422).body("Validation exception");
        }
        topicService.save(topic, message);
        messageService.save(message, topic.getTopicUuid());
        return ResponseEntity.status(200).body(topic);
    }

    @PostMapping("/topic/{topicId}/message")
    public ResponseEntity<?> createMessage(@PathVariable("topicId") String topicId,
                                           @RequestBody Message message, BindingResult br) {
        if (topicService.findByUuid(topicId).isEmpty()) {
            return ResponseEntity.status(400).body("Invalid topic ID");
        }
        messageValidator.validate(message, br);
        if (br.hasErrors()) {
            return ResponseEntity.status(422).body("Validation exception");
        }
        messageService.save(message, topicId);
        return ResponseEntity.status(200).body(topicService.findByUuid(topicId));
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<?> getTopic(@PathVariable("topicId") String topicId) {
        return topicService.getTopic(topicId);
    }

    @PutMapping("/topic/{topicId}")
    public ResponseEntity<?> updateTopic(@PathVariable("topicId") String topicId,
                            @RequestBody Topic topic, BindingResult br) {
        topicValidator.validate(topic, br);
        if (br.hasErrors()) {
            return ResponseEntity.status(422).body("Validation exception");
        }
        return topicService.update(topicId, topic);
    }

    @DeleteMapping("/topic/{topicId}")
    public ResponseEntity<?> deleteTopic(@PathVariable("topicId") String topicId) {
        return topicService.delete(topicId);
    }
}