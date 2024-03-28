package com.etim.atom.validation;

import com.etim.atom.message.Message;
import com.etim.atom.message.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class MessageValidation implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Message.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Message message = (Message) target;
        if (message.getText().isEmpty() || message.getText().length() > 100) {
            errors.reject("422");
        }
    }
}
