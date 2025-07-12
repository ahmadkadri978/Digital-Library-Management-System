package kadri.Digital.Library.Management.System.exception;

import jakarta.persistence.OptimisticLockException;
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
        return "error/reservation-not-found";
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public String handleBookNotAvailableException(BookNotAvailableException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/book-not-available";
    }

    @ExceptionHandler(DuplicateReservationException.class)
    public String handleDuplicateReservationException(DuplicateReservationException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/duplicate-reservation";
    }
    @ExceptionHandler(OptimisticLockException.class)
    public String handleOptimisticLockException(OptimisticLockException ex, Model model) {
        model.addAttribute("error","The record was updated by another user . Please try again.");
        return "error/optimistic-lock";
    }
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex,  Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/general-error";
    }


}
