package com.etim.atom.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@ToString
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String topicUuid;
    private String topicName;
    private String createdAt;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    private List<Message> messages;

    public void addToMessages(Message message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
    }
}
