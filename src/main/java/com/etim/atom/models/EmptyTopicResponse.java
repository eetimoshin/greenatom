package com.etim.atom.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmptyTopicResponse {
    private String topicId;
    private String topicName;
    private String createdAt;
}
