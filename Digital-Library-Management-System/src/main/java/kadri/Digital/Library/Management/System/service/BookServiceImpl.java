package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookRepository bookRepository;
    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Page<Book> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> searchBooksByTitle(String title,int page, int size) {
        Pageable pageable = PageRequest.of(page , size);
        return bookRepository.findByTitleContainingIgnoreCase(title , pageable);
    }

    @Override
    public Page<Book> searchBooksByAuthor(String author, int page, int size) {
        Pageable pageable  = PageRequest.of(page , size);
        return bookRepository.findByAuthorContainingIgnoreCase(author , pageable);
    }

    @Override
    public Page<Book> searchBooksByIsbn(String isbn, int page, int size) {
        Pageable pageable = PageRequest.of(page , size );
        return bookRepository.findByIsbn(isbn , pageable);
    }


}
