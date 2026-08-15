package com.jjtutti.library.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.jjtutti.library.security.dao.User;
import com.jjtutti.library.security.dao.UserRepository;

public class CustomUserDetailsServiceTest {
    
    UserRepository userRepository = mock(UserRepository.class);

    CustomUserDetailsService userDetailsService = new CustomUserDetailsService(userRepository);
 
    @Test
    public void shouldLoadUserByUsername() {
        String username = "root";
        User user = new User(username, "pass");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        var userDetails = userDetailsService.loadUserByUsername(username);
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo("pass");
        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void shouldThrowUsernameNotFoundException() {
        String username = "notexists";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining(username);

        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }
}
