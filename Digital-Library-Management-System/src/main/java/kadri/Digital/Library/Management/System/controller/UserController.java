package kadri.Digital.Library.Management.System.controller;

import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller

public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OAuth2User oAuth2User, Model model){
        if (oAuth2User == null) {
            model.addAttribute("error", "User is not authenticated.");
            return "error"; // عرض صفحة خطأ مخصصة
        }

        model.addAttribute("username", oAuth2User.getAttribute("login"));
        model.addAttribute("name", oAuth2User.getAttribute("name"));
        model.addAttribute("role", oAuth2User.getAttribute("name"));
        model.addAttribute("avatar", oAuth2User.getAttribute("avatar_url"));

        return "profile";
    }
}
