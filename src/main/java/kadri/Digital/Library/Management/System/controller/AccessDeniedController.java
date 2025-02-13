package kadri.Digital.Library.Management.System.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {
    @GetMapping("/access-denied")
    public String handleAccessDenied(Model model) {
        model.addAttribute("error", "You do not have permission to access this resource." );
        return "error/403";
    }

}
