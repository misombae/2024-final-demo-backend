package com.example.demo.controller.user;

import com.example.demo.dto.LoginKaKaoUserInfoDTO;
import com.example.demo.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class loginController {

    private final LoginService loginService;

    @GetMapping(value="/kakao", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginKaKaoUserInfoDTO> kakakoAuth(@RequestParam String code) {
        LoginKaKaoUserInfoDTO userInfoDTO = loginService.kakaoAuth(code);
        return ResponseEntity.ok().body(userInfoDTO);
    }
}
