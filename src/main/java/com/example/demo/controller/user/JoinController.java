package com.example.demo.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/join")
public class JoinController {

    @PostMapping("/normal")
    public String joinNormal(@RequestParam String name) {
        return "Hello from Java Api!";
    }

}
