package com.example.demo.service;

import com.example.demo.dto.LoginKaKaoUserInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Data
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final WebClient webClient;

    //TODO: 추후 env환경 적용..귀찮음..
    @Value("https://kauth.kakao.com/oauth/token")
    private String kakaoTokenUri;

    @Value("https://kapi.kakao.com/v2/user/me")
    private String kakaoUserUri;

    @Value("f50c62fca9c5e7699e918b5fb5db692d")
    private String kakaoClientId;

    @Value("http://localhost:8080/oauth/kakao")
    private String redirectUri;

    @Value("3xweouQLjkDNxacVxpXWScEcFGt5scMj")
    private String kakaoClientSecret;


    public LoginKaKaoUserInfoDTO kakaoAuth(String code) {
        //토큰을 받는다.
        /**
         * Mono<T>는 Spring WebFlux에서 제공하는 반응형 타입 중 하나로, 0개 또는 1개의 비동기 결과를 나타냅니다.
         * Mono는 기본적으로 하나의 결과를 반환하는 비동기 작업에 사용됩니다.
         * WebClient는 Spring WebFlux에서 제공하는 비동기 HTTP 클라이언트입니다. 기존의 RestTemplate 대신 사용되며, 비동기 요청과 반응형 프로그래밍을 지원합니다.
         */

        Mono monoTokenResult = webClient.get()
                        .uri(kakaoTokenUri, uriBuilder -> uriBuilder
                                        .queryParam("grant_type", "authorization_code")
                                        .queryParam("client_id", kakaoClientId)
                                        .queryParam("redirect_uri", redirectUri)
                                        .queryParam("code", code)
                                        .queryParam("client_secret", kakaoClientSecret)
                                        .build())
                        .header("Content-type", "Content-type: application/x-www-form-urlencoded;charset=utf-8")
                        // 에러 처리를 위해 retrieve() -> exchange() 사용.
                        /**
                         * WebClient에서 retrieve() 대신 exchange()를 사용하는 이유는 주로 더 세밀한 에러 처리를 위해서입니다.
                         * retrieve(): 간단하게 응답 본문을 처리할 때 유용합니다.
                         * exchange(): 더 많은 세부 정보를 직접 제어할 수 있습니다.
                         */
                        .exchangeToMono(clientResponse -> {
                            if (clientResponse.statusCode()
                                            .is2xxSuccessful()) {
                                return clientResponse.bodyToMono(Object.class);
                            } else if (clientResponse.statusCode()
                                            .is4xxClientError()) {
                                return clientResponse.bodyToMono(Object.class);
                            } else {
                                return clientResponse.createException()
                                                .flatMap(error -> Mono.error(error));
                            }
                        });

        // 동기 작업.(엑세스 토큰 결과 받을때까지 기다린다.)
        Object tokenResult = monoTokenResult.block();
        if(tokenResult == null) {
           return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        Map tokenResultMap = mapper.convertValue(tokenResult, Map.class);

        //TODO: 토큰 DTO에 정보 담기.. 귀찮으니까 나중에
        String accessToken = (String) tokenResultMap.get("access_token");

        //사용자 정보 받기
        Mono monoUserResult = webClient.get()
                        .uri(kakaoUserUri)
                        .header("Content-type", "Content-type: application/x-www-form-urlencoded;charset=utf-8")
                        .header("Authorization", "Bearer " + accessToken)
                        .exchangeToMono(clientResponse -> {
                            if (clientResponse.statusCode()
                                            .is2xxSuccessful()) {
                                return clientResponse.bodyToMono(Object.class);
                            } else if (clientResponse.statusCode()
                                            .is4xxClientError()) {
                                return clientResponse.bodyToMono(Object.class);
                            } else {
                                return clientResponse.createException()
                                                .flatMap(error -> Mono.error(error));
                            }
                        });
        //동기 작업
        Object userResult = monoUserResult.block();
        if(userResult == null) {
            return null;
        }
        //사용자 정보 저장
        Map userResultMap = mapper.convertValue(userResult, Map.class);
        LoginKaKaoUserInfoDTO userInfoDTO = LoginKaKaoUserInfoDTO.builder()
                            .id(userResultMap.containsKey("id") ? userResultMap.get("id").toString().trim(): "")
                            .build();

        return userInfoDTO;
    }
}
