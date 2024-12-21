package kadri.Digital.Library.Management.System.controller;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.service.BookService;
import kadri.Digital.Library.Management.System.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;


@Controller
@RequestMapping("/Digital Library")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;
    @Autowired
    UserService userService;
    @GetMapping
    public String homePage(){
        return "home";
    }
    @GetMapping("/redirect")
    public String redirectAfterLogin(OAuth2AuthenticationToken authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/Digital Library/admin/books";
        }
        return "redirect:/Digital Library/profile";
    }


    // عرض الكتب مع Pagination
    @GetMapping("/books")
    public String getBooks(
            @RequestParam(defaultValue = "0") int page, // رقم الصفحة الافتراضي 0
            @RequestParam(defaultValue = "5") int size, // حجم الصفحة الافتراضي 5
            Model model,
            @AuthenticationPrincipal OAuth2User principal) {

        logger.debug("Fetching books for page: {}, size: {}", page, size);

        Page<Book> booksPage = bookService.getAllBooks(page, size);
        User user = getUserIdFromPrincipal(principal);
        logger.debug("Current user: {}", user.getUsername());
        model.addAttribute("user", user);

        model.addAttribute("books", booksPage.getContent()); // الكتب في الصفحة الحالية
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());

        logger.debug("Books fetched: {}", booksPage.getContent());

        return "books"; // اسم صفحة Thymeleaf لعرض الكتب
    }
    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model){
        Page<Book> booksPage;
        if(title != null && !title.isEmpty()) {
            booksPage = bookService.searchBooksByTitle(title, page, size);
        }  else if (author != null && !author.isEmpty()) {
            booksPage = bookService.searchBooksByAuthor(author, page, size);
        } else if (isbn != null && !isbn.isEmpty()) {
            booksPage = bookService.searchBooksByIsbn(isbn, page, size);
        } else {
            booksPage = bookService.getAllBooks(page, size);
        }

        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        return "search-results";
    }
    private User getUserIdFromPrincipal(OAuth2User principal) {
        String username = principal.getAttribute("login"); // Retrieve username from principal
        Optional<User> user = userService.findByUsername(username); // Fetch user by username
        return user.get();
    }
}

