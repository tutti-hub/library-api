package com.jjtutti.library.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    // private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationService authenticationService
        // , RefreshTokenService refreshTokenService
    ) {
        this.authenticationService = authenticationService;
        // this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        throw new IllegalStateException("Not implemented"); 
    }
    

}
