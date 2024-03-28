package com.etim.atom.topic;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TopicDTO {
    private String topicId;
    private String topicName;
    private String createdAt;
}
