package com.etim.atom.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String messageUuid;
    private String text;
    private String author;
    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "topicUuid")
    @JsonBackReference
    private Topic topic;
}
