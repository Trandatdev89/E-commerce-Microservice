package com.project01.profileservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "profile")
public class ProfileEntity {
    @MongoId
    private String id;
    private String userid;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
}
