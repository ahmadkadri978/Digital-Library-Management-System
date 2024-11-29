package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.User;

import java.util.Optional;

public interface UserService {
    public User saveUser(User user);
    public Optional<User> findByEmail(String email);
}
