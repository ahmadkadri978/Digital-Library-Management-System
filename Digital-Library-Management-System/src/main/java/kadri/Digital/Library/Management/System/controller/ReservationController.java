package kadri.Digital.Library.Management.System.controller;

import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.repository.UserRepository;
import kadri.Digital.Library.Management.System.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    ReservationService reservationService;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/add")
    public String reserveBook(@RequestParam Long bookId, @AuthenticationPrincipal OAuth2User principal){
        Long userId = getUserIdFromPrincipal(principal);
        reservationService.createReservation(bookId, userId);
        return "redirect:/reservations";
    }

    @PostMapping("/cancel")
    public String cancelReservation(@RequestParam Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return "redirect:/reservations";
    }
    @GetMapping
    public String listReservation(Model model, @AuthenticationPrincipal OAuth2User principal){
        Long userId = getUserIdFromPrincipal(principal);
        model.addAttribute("reservations", reservationService.getReservationsByUser(userId));
        return "reservations";
    }
    private Long getUserIdFromPrincipal(OAuth2User principal) {
        String username = principal.getAttribute("login"); // Retrieve username from principal
        Optional<User> user = userRepository.findByUsername(username); // Fetch user by username
        return user.get().getId(); // Return the user's ID
    }
}
