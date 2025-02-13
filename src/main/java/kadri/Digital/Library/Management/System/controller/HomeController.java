package kadri.Digital.Library.Management.System.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Digital Library")
public class HomeController {
    @GetMapping("/login")
    public String LoginController() {
        return "login";
    }

}
