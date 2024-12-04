package kadri.Digital.Library.Management.System.controller;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;
    @GetMapping("/")
    public String homePage(){
        return "home";
    }

    // عرض الكتب مع Pagination
    @GetMapping("/books")
    public String getBooks(
            @RequestParam(defaultValue = "0") int page, // رقم الصفحة الافتراضي 0
            @RequestParam(defaultValue = "5") int size, // حجم الصفحة الافتراضي 5
            Model model) {

        Page<Book> booksPage = bookService.getAllBooks(page, size);

        model.addAttribute("books", booksPage.getContent()); // الكتب في الصفحة الحالية
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());

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
}

