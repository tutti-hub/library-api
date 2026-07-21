package com.jjtutti.library.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public TokenResponse login(LoginRequest loginRequest) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.username(),
                loginRequest.password());

        Authentication authenticated = authenticationManager.authenticate(authentication);

        UserDetails userDetails = (UserDetails) authenticated.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new TokenResponse(token);
    }

}
