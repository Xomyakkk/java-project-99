package hexlet.code.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=my-super-secret-key-that-is-at-least-256-bits-long-for-hs256",
    "jwt.expiration=86400000"
})
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
    }

    @Test
    void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(testEmail);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGetEmailFromToken() {
        String token = jwtTokenProvider.generateToken(testEmail);

        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        assertEquals(testEmail, extractedEmail);
    }

    @Test
    void testValidateTokenWithValidToken() {
        String token = jwtTokenProvider.generateToken(testEmail);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithNullToken() {
        boolean isValid = jwtTokenProvider.validateToken(null);

        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithEmptyToken() {
        boolean isValid = jwtTokenProvider.validateToken("");

        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithMalformedToken() {
        String malformedToken = "eyJhbGciOiJIUzI1NiJ9.malformed";

        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        assertFalse(isValid);
    }

    @Test
    void testGenerateTokenDifferentEmails() {
        String token1 = jwtTokenProvider.generateToken("user1@example.com");
        String token2 = jwtTokenProvider.generateToken("user2@example.com");

        assertNotNull(token1);
        assertNotNull(token2);
        assertFalse(token1.equals(token2));
    }

    @Test
    void testGetEmailFromTokenConsistency() {
        String token = jwtTokenProvider.generateToken(testEmail);

        String email1 = jwtTokenProvider.getEmailFromToken(token);
        String email2 = jwtTokenProvider.getEmailFromToken(token);

        assertEquals(email1, email2);
        assertEquals(testEmail, email1);
    }
}