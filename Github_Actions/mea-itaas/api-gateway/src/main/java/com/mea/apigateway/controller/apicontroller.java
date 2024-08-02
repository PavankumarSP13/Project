package com.mea.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class apicontroller {


    @GetMapping("/test")
    public String test(){
        return "APi-Gateway Service is Deployed";
    }
}
