package unittests;

import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.exception.UserNotFoundException;
import kadri.Digital.Library.Management.System.repository.UserRepository;

import kadri.Digital.Library.Management.System.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2User oAuth2User;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("kadri", "Kadri Name", "http://avatar", "USER");
        user.setId(1L);
        user.setReservation("INACTIVE");
    }

    @Test
    void registerUser_ShouldRegisterNewUser() {
        when(oAuth2User.getAttribute("login")).thenReturn("kadri");
        when(oAuth2User.getAttribute("name")).thenReturn("Kadri Name");
        when(oAuth2User.getAttribute("avatar_url")).thenReturn("http://avatar");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(oAuth2User);
        assertEquals("kadri", result.getUsername());
        assertEquals("INACTIVE", result.getReservation());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getAllUsers_ShouldReturnPaginatedUsers() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(PageRequest.of(0, 5))).thenReturn(page);

        Page<User> result = userService.getAllUsers(0, 5);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getUser_ShouldReturnUsername() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        String result = userService.getUser(1L);
        assertEquals("kadri", result);
    }

    @Test
    void getUser_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUser(99L));
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1L);
        assertEquals(user, result);
    }

    @Test
    void getUserById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        when(userRepository.findByUsername("kadri")).thenReturn(Optional.of(user));
        Optional<User> result = userService.findByUsername("kadri");
        assertTrue(result.isPresent());
    }

    @Test
    void save_ShouldCallRepositorySave() {
        userService.save(user);
        verify(userRepository).save(user);
    }

    @Test
    void delete_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrow_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.delete(99L));
    }
}
