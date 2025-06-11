package com.nana.logsentry;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // http://localhost:8081/test
    @GetMapping("/test")
    public String test() {
        return "Start LogSentry !!";
    }

}
