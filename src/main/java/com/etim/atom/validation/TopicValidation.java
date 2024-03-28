package com.etim.atom.validation;

import com.etim.atom.topic.Topic;
import com.etim.atom.topic.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class TopicValidation implements Validator {
    private final TopicService topicService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Topic.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Topic topic = (Topic) target;
        if (topicService.findByName(topic.getTopicName()).isPresent()
                || topic.getTopicName().isEmpty() || topic.getTopicName().length() > 20) {
            errors.reject("422");
        }
    }
}
