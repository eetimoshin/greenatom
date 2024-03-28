package com.etim.atom;

import com.etim.atom.message.Message;
import com.etim.atom.topic.Topic;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class NewTopicRequest {
    private Topic topic;
    private Message message;
}
