package kadri.Digital.Library.Management.System.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.entity.Reservation;
import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.service.BookService;
import kadri.Digital.Library.Management.System.service.ReservationService;
import kadri.Digital.Library.Management.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Digital Library/admin")
public class AdminController {
    @Autowired
    BookService bookService;
    @Autowired
    UserService userService;
    @Autowired
    ReservationService reservationService;
    @GetMapping("/test-auth")
    public String testAuthentication(Authentication authentication) {
        if (authentication != null) {
            System.out.println("Authentication: " + authentication.getAuthorities());
        } else {
            System.out.println("Authentication is null");
        }
        return "test-auth";
    }

    @GetMapping("/books")
    public String manageBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Page<Book> booksPage = bookService.getAllBooks(page, size);
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        return "admin-books";
    }

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }
    @PostMapping("/books/add")
    public String addBook(@Valid @ModelAttribute Book book, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            return "add-book";
        }

        bookService.saveBook(book);
        return "redirect:/Digital Library/admin/books";
    }

    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "edit-book";
    }

    @PostMapping("/books/edit/{id}")
    public String editBook(@PathVariable Long id, @ModelAttribute @Valid Book book, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return "edit-book";
        bookService.updateBook(id, book);
        return "redirect:/Digital Library/admin/books";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/Digital Library/admin/books";
    }

    @GetMapping("/users")
    public String users(Model model ,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size) {

        Page<User> usersPage = userService.getAllUsers(page,size);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());

        return "admin_user";

    }

    @GetMapping("users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpServletRequest request){
        String username = userService.getUser(id);
        userService.delete(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName().equals(username)) {
            // إبطال الجلسة
            request.getSession().invalidate();
            // حذف SecurityContext
            SecurityContextHolder.clearContext();
        }
        return "redirect:/Digital Library/admin/users";
    }

    @GetMapping("/user/{id}/reservations")
    public String listReservation(@PathVariable Long id, Model model){
        List<Reservation> userReservations = reservationService.getReservationsByUser(id);
        String username = userService.getUserById(id).getUsername();

        model.addAttribute("reservations", userReservations);
        model.addAttribute("username", username);
        return "reservations";
    }


    @PostMapping("/user/{userId}/reservations/cancel")
    public String cancelReservation(@PathVariable long userId,@RequestParam Long reservationId, Model model){
        reservationService.cancelReservation(reservationId);
        return "redirect:/Digital Library/admin/user/" + userId + "/reservations";
    }
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
