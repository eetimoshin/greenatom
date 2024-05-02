package com.etim.atom.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicWithMessageRequest {
    private EmptyTopicRequest emptyTopicRequest;
    private MessageRequest messageRequest;
}
