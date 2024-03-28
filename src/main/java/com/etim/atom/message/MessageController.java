package com.etim.atom.message;

import com.etim.atom.topic.TopicService;
import com.etim.atom.validation.MessageValidation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageService messageService;
    private final MessageValidation messageValidator;

    @PutMapping("/message/{messageId}")
    public ResponseEntity<?> updateMessage(@PathVariable("messageId") String messageIdToUpdate,
                                           @RequestBody Message updatedMessage, BindingResult br) {
        messageValidator.validate(updatedMessage, br);
        if (br.hasErrors()) {
            return ResponseEntity.status(422).body("Validation exception");
        }
        return messageService.update(messageIdToUpdate, updatedMessage);
    }

    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable("messageId") String messageId) {
        return messageService.delete(messageId);
    }
}
