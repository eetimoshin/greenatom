package com.etim.atom.topic;

import com.etim.atom.message.Message;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
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
