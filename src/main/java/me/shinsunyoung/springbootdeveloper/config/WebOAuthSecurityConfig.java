package me.shinsunyoung.springbootdeveloper.config;

import me.shinsunyoung.springbootdeveloper.config.jwt.TokenProvider;
import me.shinsunyoung.springbootdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.shinsunyoung.springbootdeveloper.config.oauth.OAuth2SuccessHandler;
import me.shinsunyoung.springbootdeveloper.config.oauth.OAuth2UserCustomService;
import me.shinsunyoung.springbootdeveloper.config.oauth.TokenAuthenticationFilter;
import me.shinsunyoung.springbootdeveloper.repository.RefreshTokenRepository;
import me.shinsunyoung.springbootdeveloper.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebOAuthSecurityConfig {

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/img/**", "/css/**", "/js/**")
                .requestMatchers("/h2-console/**");
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId("googleClientId") // 환경변수 사용
                .clientSecret("googleClientSecret") // 환경변수 사용
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .clientName("Google")
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           TokenProvider tokenProvider,
                                           OAuth2UserCustomService oAuth2UserCustomService,
                                           OAuth2SuccessHandler oAuth2SuccessHandler,
                                           OAuth2AuthorizationRequestBasedOnCookieRepository cookieRepository) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(new TokenAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/test", "/health", "/db-test").permitAll()  // ← 추가
                        .requestMatchers("/api/token").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                                .authorizationRequestRepository(cookieRepository)
                        )
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(oAuth2UserCustomService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        )
                )
                .build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(TokenProvider tokenProvider,
                                                     RefreshTokenRepository refreshTokenRepository,
                                                     OAuth2AuthorizationRequestBasedOnCookieRepository cookieRepository,
                                                     UserService userService) {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository, cookieRepository, userService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(TokenProvider tokenProvider) {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}