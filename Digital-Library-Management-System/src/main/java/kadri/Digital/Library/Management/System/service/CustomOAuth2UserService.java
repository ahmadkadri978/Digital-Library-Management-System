package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        OAuth2User oAuth2User = super.loadUser(userRequest);
//        System.out.println("User Attributes: " + oAuth2User.getAttributes());

        String username = oAuth2User.getAttribute("login");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        // تحقق إذا كان المستخدم موجودًا
        Optional<User> userOptional = userService.findByUsername(username);
        User user;
        if (userOptional.isEmpty()) {
            // إذا كان المستخدم جديدًا، قم بإنشائه بدور USER
            user = new User(username, name, avatarUrl, "USER");
            user.setReservation("INACTIVE");
            userService.save(user);
        } else {
            user = userOptional.get();
        }

        // إضافة الدور للمستخدم
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                oAuth2User.getAttributes(),
                "login"
        );
    }

}
