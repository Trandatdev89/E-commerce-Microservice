package com.project01.chatservice.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Document(value = "conversation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Conversation {

    @MongoId
    private String id;
    private String type; //DIRECT,GROUP
    private String participants;

    List<ParticipantInfo> participantInfos;

    private Instant createdDate;
    private Instant modifiedDate;
}
