package com.dotpay.publicservice.domain.mapper;

import com.dotpay.publicservice.domain.request.BlockIpDto;
import com.dotpay.publicservice.domain.request.UserAccessDto;
import com.dotpay.publicservice.model.BlockedIp;
import com.dotpay.publicservice.model.UserAccessLog;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class  UserMapper {

    public abstract UserAccessLog createUserLog(UserAccessDto data);
    public abstract BlockedIp createBlockedId(BlockIpDto blockIp);
    public abstract List<BlockedIp> createBlockedId(List<BlockIpDto> blockIps);
}
