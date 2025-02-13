package kadri.Digital.Library.Management.System.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFoundException(BookNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/book-not-found";
    }
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model){
        model.addAttribute("error", ex.getMessage());
        return "error/user-not-found";
    }
    @ExceptionHandler(ReservationNotFoundException.class)
    public String handleReservationNotFoundException(ReservationNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/reservation-not-found"; // صفحة خطأ مخصصة
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public String handleBookNotAvailableException(BookNotAvailableException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/book-not-available"; // صفحة خطأ مخصصة
    }

    @ExceptionHandler(DuplicateReservationException.class)
    public String handleDuplicateReservationException(DuplicateReservationException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/duplicate-reservation"; // صفحة خطأ مخصصة
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
//        model.addAttribute("error","You do not have permission to access this resource.");
//        System.out.println(model.getAttribute("error"));
//        return "error/403";
//    }
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex,  Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/general-error";
    }

}
