package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    public Book saveBook(Book book);
    public Page<Book> getAllBooks(int page , int size);
    public Page<Book> searchBooksByTitle(String title,int page, int size);
    public Page<Book> searchBooksByAuthor(String author, int page, int size);
    public Page<Book> searchBooksByIsbn(String isbn, int page, int size);
}
