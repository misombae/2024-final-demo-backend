package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:5173") // Vue.js 서버 도메인 허용
public class ExampleController {

    @GetMapping("/example-endpoint")
    public String getExample() {
        return "Hello from Java Api!";
    }


}
