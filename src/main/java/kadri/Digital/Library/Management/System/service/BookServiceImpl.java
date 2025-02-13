package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.exception.BookNotFoundException;
import kadri.Digital.Library.Management.System.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Book getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("Book with ID" + id + "not found."));
        return book;

    }

    @Override
    public void updateBook(Long id, Book book) {
        Book existingBook = getBookById(id);
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {
        if(!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book with ID" + id + "not found.");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public boolean isBookAvailableForReservation(Long bookId) {
        Book book = getBookById(bookId);
        return book.getCopiesAvailable() > 0 ;
    }

    @Override
    public void updateBookReservationStatus(Long bookId, boolean reserved) {
        Book book = getBookById(bookId);
        if (reserved) {
            book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        } else {
            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        }
        bookRepository.save(book);

    }

}
