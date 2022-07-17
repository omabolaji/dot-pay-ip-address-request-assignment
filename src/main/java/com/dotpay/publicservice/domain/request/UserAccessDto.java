package com.dotpay.publicservice.domain.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserAccessDto {
    private LocalDateTime date;
    private String ip;
    private String request;
    private int status;
    private String user_agent;
}
