package com.dotpay.publicservice.service;

import com.dotpay.publicservice.domain.constant.Duration;
import com.dotpay.publicservice.domain.mapper.UserMapper;
import com.dotpay.publicservice.domain.request.BlockIpDto;
import com.dotpay.publicservice.domain.request.UserAccessDto;
import com.dotpay.publicservice.model.BlockedIp;
import com.dotpay.publicservice.model.UserAccessLog;
import com.dotpay.publicservice.repository.BlockedIpRepository;
import com.dotpay.publicservice.repository.UserAccessLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service @RequiredArgsConstructor @Slf4j
public class PublicService {


    private static final String COMMA_DELIMITER = "|";
    private final UserAccessLogRepository userAccessLogRepository;
    private final BlockedIpRepository blockedIpRepository;
    private final UserMapper userMapper;

    private static String FORMAT_TYPE = "text/csv";
    private static String[] HEADERS = {"date","ip","request","status","user_agent"};

    private boolean hasCorrectFormat(MultipartFile file){
        if(!FORMAT_TYPE.equals(file.getContentType()))
            return false;
        return true;
    }


    private void pushDataToDb(String file){
        try {
            URL url = new URL(file);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withDelimiter('|'));
            List<UserAccessLog> refinedData = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                UserAccessDto dto = new UserAccessDto();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                LocalDateTime date = LocalDateTime.parse(csvRecord.get(0), dtf);
                dto.setDate(date);
                dto.setIp(csvRecord.get(1));
                dto.setRequest(csvRecord.get(2));
                dto.setStatus(Integer.valueOf(csvRecord.get(3)));
                dto.setUser_agent(csvRecord.get(4));
                //todo: mapped data to model
                UserAccessLog accessLog = userMapper.createUserLog(dto);
                refinedData.add(accessLog);
            }
            //todo: save all data once to db
            userAccessLogRepository.saveAll(refinedData);
            System.out.println("I save all data from file to user_access_log table");
        } catch (IOException e) {
            throw new RuntimeException("fail to parse "+FORMAT_TYPE+" file: " + e.getMessage());
        }
    }

    public void search(String accessFile, String start,String duration, int limit ) throws Exception {
        try {
            //todo: call check if user_access_log table is not empty to load data
            long checkData = userAccessLogRepository.logCount();
            if(checkData < 1){
                //todo: load data to user_access_log table
                log.info("loading data from file and processing it in DB.....");
                pushDataToDb(accessFile);
                log.info("DB table successfully loaded!!!!!");
            }
            log.info("DB table already loaded with data from file");
            //todo: proceed to search query
            searchFilter(start,duration, limit);

        }catch (Exception ex){
            throw new Exception("An error occurred during filter: "+ex.getMessage());
        }
    }


    private void searchFilter(String start,String duration, int limit) throws Exception, ParseException {
        try {
            if(duration.equalsIgnoreCase(Duration.hourly.toString())){
                //todo: do hourly query
                //todo: max limit for hourly request is 200
                if(limit <= 200){
                    hourlyRequest(start,limit);
                }else {
                    throw new Exception("Maximum limit for HOURLY request can not be more than 200");
                }
            } else if(duration.equalsIgnoreCase(Duration.daily.toString())){
                //todo: do daily query
                //todo: max limit for daily request is 500
                if(limit <= 500){
                    dailyRequest(start, limit);
                }else {
                    throw new Exception("Maximum limit for DAILY request can not be more than 500");
                }
            } else {
                throw new Exception("Duration type not supported or not found");
            }
        }catch (ParseException pe){
            throw new Exception("Date format is: yyyy-MM-dd.HH:mm:ss");
        } catch (Exception ex){
            throw new Exception("An error occurred: "+ex.getMessage());
        }
    }

    private void hourlyRequest(String start, int limit) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
        LocalDateTime plusOneHour = LocalDateTime.parse(start, formatter);
        LocalDateTime endDateTime = plusOneHour.plusHours(1);
        String endDate = endDateTime.format(formatter);

        List<UserAccessLog> accessLogs = userAccessLogRepository.findBlockedIPRequest(start,endDate,limit);
        if(accessLogs.size() < 1 ){
            throw new Exception("No data found for this request");
        }

        List<BlockIpDto> refinedBlockedData = new ArrayList<>();
        for(UserAccessLog logs: accessLogs){
            long reqNumber = userAccessLogRepository.ipRequestCount(logs.getIp(),start,endDate);
//            log.info("ip : {} ", logs.getIp(),"requestNumber : {} ", reqNumber, "comment : {} ", logs.getUser_agent());
            System.out.println("IP: "+logs.getIp()+ " , RequestNumber: "+reqNumber+" , comment: "+logs.getUser_agent());
            BlockIpDto dto = new BlockIpDto();
            dto.setComment(logs.getUser_agent());
            dto.setIp(logs.getIp());
            dto.setRequestNumber(reqNumber);
            refinedBlockedData.add(dto);
        }
        //todo: save all blocked IP
        List<BlockedIp> blockedIps = userMapper.createBlockedId(refinedBlockedData);
        blockedIpRepository.saveAll(blockedIps);
        log.info("All Blocked Ip saved........ : {} ");
    }

    private void dailyRequest(String start, int limit) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
        LocalDateTime plusDay = LocalDateTime.parse(start, formatter);
        LocalDateTime endDateTime = plusDay.plusDays(1);
        String endDate = endDateTime.format(formatter);

        List<UserAccessLog> accessLogs = userAccessLogRepository.findBlockedIPRequest(start,endDate,limit);
        if(accessLogs.size() < 1 ){
            throw new Exception("No data found for this request");
        }

        List<BlockIpDto> refinedBlockedData = new ArrayList<>();
        for(UserAccessLog logs: accessLogs){
            long reqNumber = userAccessLogRepository.ipRequestCount(logs.getIp(),start,endDate);
//            log.info("ip : {} ", logs.getIp(),"requestNumber : {} ", reqNumber, "comment : {} ", logs.getUser_agent());
            System.out.println("IP: "+logs.getIp()+ " , RequestNumber: "+reqNumber+" , comment: "+logs.getUser_agent());
            BlockIpDto dto = new BlockIpDto();
            dto.setComment(logs.getUser_agent());
            dto.setIp(logs.getIp());
            dto.setRequestNumber(reqNumber);
            refinedBlockedData.add(dto);
        }
        //todo: save all blocked IP
        List<BlockedIp> blockedIps = userMapper.createBlockedId(refinedBlockedData);
        blockedIpRepository.saveAll(blockedIps);
        log.info("All Blocked Ip saved........ : {} ");
    }
}
