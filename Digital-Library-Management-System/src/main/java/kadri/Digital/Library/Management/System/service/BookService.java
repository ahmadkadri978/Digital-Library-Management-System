package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    Book saveBook(Book book);
    Page<Book> getAllBooks(int page , int size);
    Page<Book> searchBooksByTitle(String title,int page, int size);
    Page<Book> searchBooksByAuthor(String author, int page, int size);
    Page<Book> searchBooksByIsbn(String isbn, int page, int size);
    Book getBookById(Long id);

    void updateBook(Long id, Book book);

    void deleteBook(Long id);
}
