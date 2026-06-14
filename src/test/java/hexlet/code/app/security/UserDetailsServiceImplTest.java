package hexlet.code.app.security;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("hashedPassword123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser = userRepository.save(testUser);
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        UserDetails userDetails = userDetailsService.loadUserByUsername(testUser.getEmail());

        assertNotNull(userDetails);
        assertEquals(testUser.getEmail(), userDetails.getUsername());
        assertEquals(testUser.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        String nonExistentEmail = "nonexistent@example.com";

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(nonExistentEmail);
        });
    }

    @Test
    void testLoadUserByUsernameReturnsCorrectAuthorities() {
        UserDetails userDetails = userDetailsService.loadUserByUsername(testUser.getEmail());

        assertNotNull(userDetails.getAuthorities());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsernamePasswordNotEncoded() {
        UserDetails userDetails = userDetailsService.loadUserByUsername(testUser.getEmail());

        assertEquals(testUser.getPassword(), userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsernameWithDifferentUsers() {
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("anotherPassword");
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        userRepository.save(anotherUser);

        UserDetails userDetails1 = userDetailsService.loadUserByUsername(testUser.getEmail());
        UserDetails userDetails2 = userDetailsService.loadUserByUsername(anotherUser.getEmail());

        assertNotNull(userDetails1);
        assertNotNull(userDetails2);
        assertEquals(testUser.getEmail(), userDetails1.getUsername());
        assertEquals(anotherUser.getEmail(), userDetails2.getUsername());
    }
}
