package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginKaKaoUserInfoDTO {

     @JsonProperty("id")
     private String id;

     @JsonProperty("name")
     private String name;

     @JsonProperty("email")
     private String email;

     @JsonProperty("phone")
     private String phone;

     @JsonProperty("address")
     private String address;
}
