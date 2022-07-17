package com.dotpay.publicservice.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "user_access_log")
@Data
public class UserAccessLog implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @CreatedDate
    private LocalDateTime createdAt= LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime modifiedAt=LocalDateTime.now();
    private LocalDateTime date;
    private String ip;
    private String request;
    private int status;
    private String user_agent;
}
