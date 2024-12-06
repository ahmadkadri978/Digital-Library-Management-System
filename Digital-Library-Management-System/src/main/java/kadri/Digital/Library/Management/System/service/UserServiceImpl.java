package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(OAuth2User oAuth2User) {
        String username = oAuth2User.getAttribute("login");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");

        Optional<User> existingUser = userRepository.findByUsername(username);
        if(existingUser.isPresent()) return existingUser.get();

        User newUser = new User(username, name, avatarUrl, "USER");
        return userRepository.save(newUser);
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return userRepository.findAll(pageable);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public String getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent())
        return user.get().getUsername();
        else return null;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.of(userRepository.findByUsername(username).get());
    }
}
