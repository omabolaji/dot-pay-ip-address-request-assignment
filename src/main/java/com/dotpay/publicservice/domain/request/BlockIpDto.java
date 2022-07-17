package com.dotpay.publicservice.domain.request;

import lombok.Data;

@Data
public class BlockIpDto {
    private String ip;
    private long requestNumber;
    private String comment;
}
