package com.dotpay.publicservice.repository;

import com.dotpay.publicservice.model.UserAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccessLogRepository extends JpaRepository<UserAccessLog, Long> {

    @Query(value = "select * from user_access_log where ip=?1 order by date desc LIMIT 1 ", nativeQuery = true)
    Optional<UserAccessLog> findByLatestIp(String ip);

    @Query(value = "select COUNT(*) from user_access_log", nativeQuery = true)
    long logCount();

    @Query(value = "SELECT *, COUNT(*) FROM user_access_log WHERE date BETWEEN ?1 AND ?2 GROUP BY ip HAVING COUNT(*) > ?3 ", nativeQuery = true)
    List<UserAccessLog> findBlockedIPRequest(String start, String end, int limit);

    @Query(value = "SELECT COUNT(*) FROM user_access_log WHERE ip=?1 AND date BETWEEN ?2 AND ?3 ", nativeQuery = true)
    long ipRequestCount(String ip,String start, String end);
}
