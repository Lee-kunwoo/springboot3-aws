package me.shinsunyoung.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.shinsunyoung.springbootdeveloper.config.jwt.JwtProperties;
import me.shinsunyoung.springbootdeveloper.config.jwt.TokenProvider;
import me.shinsunyoung.springbootdeveloper.domain.RefreshToken;
import me.shinsunyoung.springbootdeveloper.domain.User;
import me.shinsunyoung.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.shinsunyoung.springbootdeveloper.repository.RefreshTokenRepository;
import me.shinsunyoung.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    TokenProvider tokenProvider; // Refresh Token 생성 및 유효성 검증을 위해 추가

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
        refreshTokenRepository.deleteAll(); // 테스트 격리를 위해 RefreshToken도 삭제
    }

    @DisplayName("createNewAccessToken: 새로운 액세스 토큰을 발급한다.")
    @Test
    void createNewAccessToken() throws Exception {
        // given
        final String url = "/api/token";

        // 1. 테스트 유저 생성 및 저장 (ID 1L)
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // 2. Refresh Token 생성 (TokenProvider 사용)
        String refreshToken = tokenProvider.generateToken(
                testUser,
                Duration.ofDays(14) // 14일 유효 기간
        );

        // 3. Refresh Token DB에 저장
        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        // 4. 요청 본문 (Refresh Token 포함)
        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);

        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        // /api/token 엔드포인트로 토큰 재발급 요청 전송
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        // 응답 상태가 201 Created 이고, accessToken이 비어있지 않은지 확인
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    // 이 위치에 이전에 있었던 TokenProviderTest 클래스가 삭제되었습니다.
}
