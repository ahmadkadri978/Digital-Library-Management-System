package kadri.Digital.Library.Management.System.controller;

import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.exception.BookNotAvailableException;
import kadri.Digital.Library.Management.System.exception.DuplicateReservationException;
import kadri.Digital.Library.Management.System.exception.UserNotFoundException;
import kadri.Digital.Library.Management.System.service.ReservationService;
import kadri.Digital.Library.Management.System.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/Digital Library/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @PostMapping("/add")
    public String reserveBook(@RequestParam Long bookId,
                              @AuthenticationPrincipal OAuth2User principal,
                              RedirectAttributes redirectAttributes) {
        User user = getUserIdFromPrincipal(principal);
        try {
            reservationService.createReservation(bookId, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Reservation added successfully!");
            logger.info("Reservation created successfully for bookId={} by userId={}", bookId, user.getId());
        } catch (DuplicateReservationException | BookNotAvailableException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            logger.warn("Reservation failed for bookId={} by userId={} - {}", bookId, user.getId(), e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong. Please try again.");
            logger.error("Unexpected error during reservation: {}", e.getMessage(), e);
        }
        return "redirect:/Digital Library/reservations";
    }

    @PostMapping("/cancel")
    public String cancelReservation(@RequestParam Long reservationId) {
        reservationService.cancelReservation(reservationId);
        logger.info("Reservation with ID={} cancelled successfully", reservationId);
        return "redirect:/Digital Library/reservations";
    }

    @GetMapping
    public String listReservation(Model model, @AuthenticationPrincipal OAuth2User principal, RedirectAttributes redirectAttributes) {
        User user = getUserIdFromPrincipal(principal);
        model.addAttribute("reservations", reservationService.getReservationsByUser(user.getId()));
        logger.debug("Fetched reservations for userId={}", user.getId());
        return "reservations";
    }

    private User getUserIdFromPrincipal(OAuth2User principal) {
        String username = principal.getAttribute("login");
        return userService.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found for username={}", username);
                    return new UserNotFoundException("User with username " + username + " not found.");
                });
    }
}

