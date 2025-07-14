package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.exception.UserNotFoundException;
import kadri.Digital.Library.Management.System.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public User registerUser(OAuth2User oAuth2User) {
        String username = oAuth2User.getAttribute("login");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");

        User newUser = new User(username, name, avatarUrl, "USER");
        newUser.setReservation("INACTIVE");

        logger.info("Registering new user: {}", username);

        return userRepository.save(newUser);
    }

    @Override
    @Cacheable(value = "users", key = "#page + '-' + #size")
    public Page<User> getAllUsers(int page, int size) {
        logger.debug("Fetching users for page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);

        logger.debug("Users fetched: {}", users.getContent());

        return users;
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void delete(Long userId) {
        logger.debug("Attempting to delete user with ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            logger.warn("User with ID {} not found for deletion", userId);
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }

        userRepository.deleteById(userId);
        logger.info("User with ID {} deleted", userId);
    }

    @Override
    public String getUser(Long id) {
        logger.debug("Fetching username for user ID: {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    logger.debug("Found username: {}", user.getUsername());
                    return user.getUsername();
                })
                .orElseThrow(() -> {
                    logger.warn("User with ID {} not found", id);
                    return new UserNotFoundException("User with ID " + id + " not found.");
                });
    }

    @Override
    @Cacheable(value = "usersById", key = "#id")
    public User getUserById(Long id) {
        logger.debug("Fetching user by ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User with ID {} not found", id);
                    return new UserNotFoundException("User with ID " + id + " not found.");
                });
    }

    @Override
    @Cacheable(value = "usersByUsername", key = "#username", unless = "#result == null")
    public Optional<User> findByUsername(String username) {
        logger.debug("Searching for user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void save(User user) {
        logger.debug("Saving user: {}", user.getUsername());
        userRepository.save(user);
    }
}

