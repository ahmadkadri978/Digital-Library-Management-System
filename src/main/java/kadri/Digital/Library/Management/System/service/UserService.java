package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

public interface UserService {
    public User registerUser(OAuth2User oAuth2User);
    Page<User> getAllUsers(int page,int  size);

    void delete(Long userId);

    String getUser(Long id);

    User getUserById(Long id);

    Optional<User> findByUsername(String username);

    void save(User user);
}
