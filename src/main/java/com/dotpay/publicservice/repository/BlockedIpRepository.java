package com.dotpay.publicservice.repository;

import com.dotpay.publicservice.model.BlockedIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedIpRepository extends JpaRepository<BlockedIp, Long> {

}
