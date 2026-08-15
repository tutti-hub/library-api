package com.jjtutti.library.auth.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjtutti.library.auth.application.AuthenticationService;
import com.jjtutti.library.auth.application.JwtService;
import com.jjtutti.library.auth.application.dto.LoginRequest;
import com.jjtutti.library.auth.application.dto.LogoutRequest;
import com.jjtutti.library.auth.application.dto.RefreshTokenRequest;
import com.jjtutti.library.auth.application.dto.TokenResponse;
import com.jjtutti.library.auth.infrastructure.exceptions.RefreshTokenNotActiveException;
import com.jjtutti.library.auth.infrastructure.exceptions.RefreshTokenNotFoundException;
import com.jjtutti.library.security.config.SecurityConfig;

@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class })
public class AuthControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AuthenticationService authenticationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldReturn401WhenCredentialsAreInvalid() throws JsonProcessingException {

        when(authenticationService.login(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        var response = doLogin();

        assertThat(response).hasStatus(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void shouldSuccessfullyLogIn() throws JsonProcessingException {
        String accessToken = "Q1W2E3";
        String refreshToken = "4R5T6Y";

        when(authenticationService.login(any()))
                .thenReturn(new TokenResponse(accessToken, refreshToken));

        var response = doLogin();

        var json = assertThat(response).hasStatus(HttpStatus.OK).bodyJson();
        json.extractingPath("accessToken").isEqualTo(accessToken);
        json.extractingPath("refreshToken").isEqualTo(refreshToken);
    }

    @Test
    public void shouldFailLoginWhenUsernameIsBlank() throws JsonProcessingException {
        var response = doLogin(" ", "pass");
        assertThat(response).hasFailed().hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldFailLoginWhenPasswordIsBlank() throws JsonProcessingException {
        var response = doLogin("root", " ");
        assertThat(response).hasFailed().hasStatus(HttpStatus.BAD_REQUEST);
    }

    // shouldSuccessfullyRefreshToken()
    @Test
    public void shouldSuccessfullyRefreshToken() throws JsonProcessingException {

        String refreshToken = "A1B2C3";
        String newAccessToken = "B2C3D4";
        String newRefreshToken = "C4D5E6";

        when(authenticationService.refreshToken(refreshToken))
                .thenReturn(new TokenResponse(newAccessToken, newRefreshToken));

        var response = doRefresh(refreshToken);

        var bodyJson = assertThat(response).hasStatusOk().bodyJson();

        bodyJson.extractingPath("accessToken").isEqualTo(newAccessToken);
        bodyJson.extractingPath("refreshToken").isEqualTo(newRefreshToken);
    }

    @Test
    public void shouldReturn400WhenRefreshTokenIsBlank() throws JsonProcessingException {
        var response = doRefresh(" ");
        assertThat(response).hasFailed().hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturn404WhenRefreshTokenIsNotFound() throws JsonProcessingException {
        String refreshToken = "A1B2C3";
        when(authenticationService.refreshToken(refreshToken))
                .thenThrow(new RefreshTokenNotFoundException(refreshToken));
        var response = doRefresh(refreshToken);
        assertThat(response).hasFailed().hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturn401WhenRefreshTokenIsNotActive() throws JsonProcessingException {
        when(authenticationService.refreshToken(anyString())).thenThrow(new RefreshTokenNotActiveException(1L));
        var response = doRefresh("ABC123");
        assertThat(response).hasFailed().hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @WithMockUser
    public void shouldSuccessfullyLogout() throws JsonProcessingException {
        String refreshToken = "ABC1234";
        var response = doLogout(refreshToken);
        assertThat(response).hasStatusOk();
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenLogoutTokenIsBlank() throws JsonProcessingException {
        var response = doLogout(" ");

        assertThat(response)
                .hasFailed()
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenLogoutTokenNotFound() throws JsonProcessingException {
        String refreshToken = "ABC1234";

        doThrow(new RefreshTokenNotFoundException(refreshToken))
                .when(authenticationService)
                .logout(refreshToken);

        var response = doLogout(refreshToken);

        assertThat(response)
                .hasFailed()
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    private MockMvcRequestBuilder doLogin(String login, String password) throws JsonProcessingException {

        LoginRequest loginRequest = new LoginRequest(login, password);

        String payload = objectMapper.writeValueAsString(loginRequest);

        return mvc.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
    }

    private MockMvcRequestBuilder doLogin() throws JsonProcessingException {
        return doLogin("justusername", "justpassword");
    }

    private MockMvcRequestBuilder doRefresh(String refreshToken)
            throws JsonProcessingException {

        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        String payload = objectMapper.writeValueAsString(request);

        return mvc.post()
                .uri("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
    }

    private MockMvcRequestBuilder doLogout(String refreshToken)
            throws JsonProcessingException {

        LogoutRequest request = new LogoutRequest(refreshToken);
        String payload = objectMapper.writeValueAsString(request);

        return mvc.post()
                .uri("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
    }

}
