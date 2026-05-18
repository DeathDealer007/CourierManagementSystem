package com.capg.smartcourier.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET", "mysecretkeymysecretkeymysecretkeymysecretkeymysecretkey");
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("testuser", 1L, Collections.singletonList("ROLE_USER"));
        assertNotNull(token);
    }

    @Test
    void testExtractUserId() {
        String token = jwtUtil.generateToken("testuser", 1L, Collections.singletonList("ROLE_USER"));
        Long userId = jwtUtil.extractUserId(token);
        assertEquals(1L, userId);
    }
}
