package com.dotpay.publicservice.api;

import com.dotpay.publicservice.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api")
public class PublicServiceController {

    private final PublicService publicService;


//    @GetMapping("/file6")
//    public void getFile6(@RequestParam String file) throws IOException {
//        publicService.pushDataToDb(file);
//    }

    @GetMapping(path = "/search")
    public void search(@RequestParam String accessFile, @RequestParam String start, @RequestParam String duration, @RequestParam int limit) throws Exception {
        publicService.search(accessFile,start,duration,limit);
    }

}
