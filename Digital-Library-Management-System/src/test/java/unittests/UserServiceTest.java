package unittests;

import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.exception.UserNotFoundException;
import kadri.Digital.Library.Management.System.repository.UserRepository;
import kadri.Digital.Library.Management.System.service.UserService;
import kadri.Digital.Library.Management.System.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OAuth2User oAuth2User;

    @Test
    void testRegisterUser(){
        String username = "testuser";
        String name = "Test User";
        String avatarUrl = "http://avatar.url";

        when(oAuth2User.getAttribute("login")).thenReturn(username);
        when(oAuth2User.getAttribute("name")).thenReturn(name);
        when(oAuth2User.getAttribute("avatar_url")).thenReturn(avatarUrl);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(oAuth2User);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(name, result.getName());
        assertEquals(avatarUrl, result.getAvatarUrl());
        assertEquals("INACTIVE", result.getReservation());
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void testGetAllUsers(){
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page,size);
        Page<User> userPage = new PageImpl<>(List.of(new User("user1", "User 1", "url1", "USER")));

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.getAllUsers(page,size);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository,times(1)).findAll(pageable);
    }

    @Test
    void testDeleteUserExist(){
        Long userId = 1l;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository,times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUserNotExist() {

        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));

        verify(userRepository,never()).deleteById(userId);
    }

    @Test
    void testGetUser() {

        Long userId=1L;
        User user = new User("testuser", "Test User", "url", "USER");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String result = userService.getUser(userId);

        assertEquals("testuser", result);

        verify(userRepository,times(1)).findById(userId);
    }

    @Test
    void testGetUserById() {

        Long userId = 1L;
        User user = new User("testuser", "Test User", "url", "USER");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository,times(1)).findById(userId);
    }

    @Test
    void testFindByUsername() {

        String username = "testuser";
        User user = new User(username, "Test User", "url", "USER");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository,times(1)).findByUsername(username);
    }

    @Test
    void testSaveUser() {

        User user = new User("testuser", "Test User", "url", "USER");

        userService.save(user);

        verify(userRepository,times(1)).save(user);

    }

}
