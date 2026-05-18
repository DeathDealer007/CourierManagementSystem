package com.capg.smartcourier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.capg.smartcourier.entity.User;
import com.capg.smartcourier.exception.ResourceNotFoundException;
import com.capg.smartcourier.repository.AuthRepository;
import com.capg.smartcourier.repository.RoleRepository;
import com.capg.smartcourier.security.JwtUtil;
import com.capg.smartcourier.entity.Role;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock // mock annotation is used for specifying that we are mocking this files
    private AuthRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RoleRepository roleRepo;

    @InjectMocks
    private AuthService service;



    // ✅ TEST 1: Register User with default role
    @Test
    void testRegisterSuccessDefaultRole() {
        User user = new User();
        user.setUsername("sameer");
        user.setPassword("1234");

        when(encoder.encode("1234")).thenReturn("encodedPass");
        when(roleRepo.findByName("USER")).thenReturn(Optional.of(new Role(1L, "USER")));
        when(repo.save(user)).thenReturn(user);

        String result = service.register(user);

        assertEquals("User registered", result);
        verify(repo, times(1)).save(user);
    }

    // ✅ TEST 1.1: Register User with existing roles
    @Test
    void testRegisterSuccessWithRoles() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin123");
        Role adminRole = new Role(null, "ADMIN");
        user.setRoles(Collections.singleton(adminRole));

        when(encoder.encode("admin123")).thenReturn("encodedAdmin");
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        when(repo.save(user)).thenReturn(user);

        String result = service.register(user);

        assertEquals("User registered", result);
    }

    // ✅ TEST 1.2: Register User with new role (creates role)
    @Test
    void testRegisterSuccessWithNewRole() {
        User user = new User();
        user.setUsername("manager");
        user.setPassword("mgr123");
        Role managerRole = new Role(null, "MANAGER");
        user.setRoles(Collections.singleton(managerRole));

        when(encoder.encode("mgr123")).thenReturn("encodedMgr");
        when(roleRepo.findByName("MANAGER")).thenReturn(Optional.empty());
        when(roleRepo.save(any(Role.class))).thenReturn(new Role(3L, "MANAGER"));
        when(repo.save(user)).thenReturn(user);

        String result = service.register(user);

        assertEquals("User registered", result);
    }

    // ✅ TEST 1.3: Register Exception
    @Test
    void testRegisterException() {
        User user = new User();
        user.setUsername("fail");
        
        when(roleRepo.findByName("USER")).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> {
            service.register(user);
        });
    }

    // ✅ TEST 2: Login Success
    @Test
    void testLoginSuccess() {

        User input = new User();
        input.setUsername("sameer");
        input.setPassword("1234");

        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setUsername("sameer");
        dbUser.setPassword("encodedPass");
        dbUser.setRoles(Collections.singleton(new Role(1L, "USER")));

        when(repo.findByUsername("sameer")).thenReturn(Optional.of(dbUser));
        when(encoder.matches("1234", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("token123");

        String result = service.login(input);

        assertEquals("token123", result);
    }

    // ❌ TEST 3: User Not Found
    @Test
    void testLoginUserNotFound() {

        when(repo.findByUsername("sameer")).thenReturn(Optional.empty());

        User input = new User();
        input.setUsername("sameer");

        assertThrows(ResourceNotFoundException.class, () -> {
            service.login(input);
        });
    }

    // ❌ TEST 4: Invalid Password
    @Test
    void testLoginInvalidPassword() {

        User dbUser = new User();
        dbUser.setUsername("sameer");
        dbUser.setPassword("encodedPass");

        when(repo.findByUsername("sameer")).thenReturn(Optional.of(dbUser));
        when(encoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        User input = new User();
        input.setUsername("sameer");
        input.setPassword("wrongPass");

        assertThrows(IllegalArgumentException.class, () -> {
            service.login(input);
        });
    }

    // ✅ TEST 5: Login Generic Exception
    @Test
    void testLoginGenericException() {
        User input = new User();
        input.setUsername("error");
        
        when(repo.findByUsername("error")).thenThrow(new RuntimeException("Fatal Error"));

        assertThrows(RuntimeException.class, () -> {
            service.login(input);
        });
    }
}