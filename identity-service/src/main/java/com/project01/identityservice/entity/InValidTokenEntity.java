package com.project01.identityservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token_invalid")
public class InValidTokenEntity extends AbstractEntity<String>{

    @Column
    private String jwtId;

    @Column
    private Date expiryTime;

    @ManyToOne
    @JoinColumn(name = "userid")
    private AccountEntity account;

}
