package com.etim.atom.topic;

import com.etim.atom.requests.MessageRequest;
import com.etim.atom.requests.TopicRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewTopicRequest {
    private TopicRequest topicRequest;
    private MessageRequest messageRequest;
}
