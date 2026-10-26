package com.nothingtothetable.survivalpool.controller.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController
{
    @GetMapping("/test")
    public String test()
    {
        return "time to start working on pool";
    }
}
