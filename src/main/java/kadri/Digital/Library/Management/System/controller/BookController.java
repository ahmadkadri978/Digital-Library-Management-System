package kadri.Digital.Library.Management.System.controller;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.exception.UserNotFoundException;
import kadri.Digital.Library.Management.System.service.BookService;
import kadri.Digital.Library.Management.System.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;


@Controller
@RequestMapping("/Digital Library")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String homePage() {
        logger.info("Accessing home page");
        return "home";
    }

    @GetMapping("/redirect")
    public String redirectAfterLogin(OAuth2AuthenticationToken authentication) {
        logger.info("Redirecting user after login based on role");
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            logger.debug("User has ROLE_ADMIN - redirecting to admin books page");
            return "redirect:/Digital Library/admin/books";
        }
        logger.debug("User is not admin - redirecting to books page");
        return "redirect:/Digital Library/books";
    }

    @GetMapping("/books")
    public String getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            @AuthenticationPrincipal OAuth2User principal) {

        logger.info("GET /books - page: {}, size: {}", page, size);
        Page<Book> booksPage = bookService.getAllBooks(page, size);

        User user = getUserIdFromPrincipal(principal);
        logger.debug("Current user: {}", user.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());

        logger.debug("Books displayed: {}", booksPage.getContent().size());
        return "books";
    }

    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        logger.info("Searching books with filters - title: {}, author: {}, isbn: {}", title, author, isbn);

        Page<Book> booksPage;

        if (title != null && !title.isEmpty()) {
            booksPage = bookService.searchBooksByTitle(title, page, size);
            logger.debug("Books filtered by title");
        } else if (author != null && !author.isEmpty()) {
            booksPage = bookService.searchBooksByAuthor(author, page, size);
            logger.debug("Books filtered by author");
        } else if (isbn != null && !isbn.isEmpty()) {
            booksPage = bookService.searchBooksByIsbn(isbn, page, size);
            logger.debug("Books filtered by ISBN");
        } else {
            booksPage = bookService.getAllBooks(page, size);
            logger.debug("No filter applied - fetching all books");
        }

        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());

        logger.debug("Search results count: {}", booksPage.getContent().size());
        return "search-results";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
        logger.info("Accessing user profile");

        if (oAuth2User == null) {
            logger.warn("Profile access failed - user not authenticated");
            model.addAttribute("error", "User is not authenticated.");
            return "error";
        }

        model.addAttribute("username", oAuth2User.getAttribute("login"));
        model.addAttribute("name", oAuth2User.getAttribute("name"));
        model.addAttribute("role", oAuth2User.getAttribute("name")); // Maybe replace with actual role if needed
        model.addAttribute("avatar", oAuth2User.getAttribute("avatar_url"));

        logger.debug("Profile data loaded for user: {}", Optional.ofNullable(oAuth2User.getAttribute("login")));
        return "profile";
    }

    private User getUserIdFromPrincipal(OAuth2User principal) {
        String username = principal.getAttribute("login");
        logger.debug("Extracting user from principal with username: {}", username);
        Optional<User> user = userService.findByUsername(username);
        return user.orElseThrow(() -> {
            logger.error("User not found with username: {}", username);
            return new UserNotFoundException("User not found with username: " + username);
        });
    }
}

