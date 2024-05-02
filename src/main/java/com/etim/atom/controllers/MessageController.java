package com.etim.atom.controllers;

import com.etim.atom.services.MessageService;
import com.etim.atom.requests.MessageRequest;
import com.etim.atom.models.Topic;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Update message")
    @PutMapping("/message/{messageId}")
    public Topic updateMessage(@PathVariable("messageId") String messageIdToUpdate,
                               @RequestBody MessageRequest updatedMessageRequest) {
        return messageService.update(messageIdToUpdate, updatedMessageRequest);
    }

    @Operation(summary = "Delete message")
    @DeleteMapping("/message/{messageId}")
    public void deleteMessage(@PathVariable("messageId") String messageId) {
        messageService.delete(messageId);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
    }
}
