package kadri.Digital.Library.Management.System.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFoundException(BookNotFoundException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 404);

        return "error";
    }
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model, HttpServletRequest request){
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 404);


        return "error";
    }
    @ExceptionHandler(ReservationNotFoundException.class)
    public String handleReservationNotFoundException(ReservationNotFoundException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 404);

        return "error";
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public String handleBookNotAvailableException(BookNotAvailableException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 400);

        return "error";
    }

    @ExceptionHandler(DuplicateReservationException.class)
    public String handleDuplicateReservationException(DuplicateReservationException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", 409);

        return "error";
    }
    @ExceptionHandler(OptimisticLockException.class)
    public String handleOptimisticLockException(OptimisticLockException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error","The record was updated by another user . Please try again.");
        model.addAttribute("status", 409);

        return "error";
    }
//    @ExceptionHandler(Exception.class)
//    public String handleGeneralException(Exception ex,  Model model, HttpServletRequest request) {
//        model.addAttribute("error", ex.getMessage());
//        model.addAttribute("status", 500);
//        String referer = request.getHeader("Referer");
//        model.addAttribute("backLink", referer != null ? referer : "/");
//        return "error";
//    }
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model, HttpServletRequest request) {
        model.addAttribute("status", 403);
        model.addAttribute("error", ex.getMessage());

        return "error";
    }


}
