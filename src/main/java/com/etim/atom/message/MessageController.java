package com.etim.atom.message;

import com.etim.atom.message.Message;
import com.etim.atom.message.MessageService;
import com.etim.atom.topic.Topic;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageService messageService;

    @PutMapping("/message/{messageId}")
    public Topic updateMessage(@PathVariable("messageId") String messageIdToUpdate,
                               @RequestBody Message updatedMessage) {
        return messageService.update(messageIdToUpdate, updatedMessage);
    }

    @DeleteMapping("/message/{messageId}")
    public void deleteMessage(@PathVariable("messageId") String messageId) {
        messageService.delete(messageId);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
    }
}
