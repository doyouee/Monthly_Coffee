package com.secondproject.monthlycoffee.token;

import com.secondproject.monthlycoffee.config.security.JwtProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "토큰 재발급  API")
@RequiredArgsConstructor
@RestController
public class TokenAPIController {

    private final TokenService tokenService;

    @Operation(summary = "액세스 토큰을 재발급 받습니다.", description = "리프레시 토큰이 있어야합니다.")
    @PostMapping(JwtProperties.REISSUE_TOKEN_URI)
    public ResponseEntity<TokenResponseDto> postReissueAccessToken(HttpServletRequest request) {
        String refresh = request.getHeader(JwtProperties.REFRESH_HEADER_NAME);
        String access = tokenService.reissue(refresh);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, access);
        headers.add(JwtProperties.REFRESH_HEADER_NAME, refresh);
        return new ResponseEntity<>(new TokenResponseDto("액세스 토큰이 재발급되었습니다", true), headers, HttpStatus.CREATED);
    }
}
