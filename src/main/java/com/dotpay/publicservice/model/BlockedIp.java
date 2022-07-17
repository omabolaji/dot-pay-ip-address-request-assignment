package com.dotpay.publicservice.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity @Table(name = "blocked_ip") @Data
public class BlockedIp implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @CreatedDate
    private LocalDateTime createdAt= LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime modifiedAt=LocalDateTime.now();
//    @ManyToOne
//    @JoinColumn(name = "user_access_log_id")
//    private UserAccessLog userAccessLog;
    private String ip;
    private long requestNumber;
    private String comment;
}
