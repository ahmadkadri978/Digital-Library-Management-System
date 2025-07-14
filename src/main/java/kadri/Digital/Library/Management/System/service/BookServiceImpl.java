package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.exception.BookNotFoundException;
import kadri.Digital.Library.Management.System.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    @Autowired
    BookRepository bookRepository;

    @Override
    @CacheEvict(value = "books", allEntries = true)
    public Book saveBook(Book book) {
        logger.debug("Saving book: {}", book);
        return bookRepository.save(book);
    }

    @Override
    @Cacheable(value = "books", key = "#page + '-' + #size")
    public Page<Book> getAllBooks(int page, int size) {
        logger.debug("Fetching all books for page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookRepository.findAll(pageable);
        logger.debug("Books fetched: {}", books.getContent());
        return books;
    }

    @Override
    @Cacheable(value = "booksByTitle", key = "#title + '-' + #page + '-' + #size")
    public Page<Book> searchBooksByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    @Cacheable(value = "booksByAuthor", key = "#author + '-' + #page + '-' + #size")
    public Page<Book> searchBooksByAuthor(String author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
    }

    @Override
    @Cacheable(value = "booksByIsbn", key = "#isbn + '-' + #page + '-' + #size")
    public Page<Book> searchBooksByIsbn(String isbn, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByIsbn(isbn, pageable);
    }

    @Override
    @Cacheable(value = "book", key = "#id")
    public Book getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID" + id + "not found."));
        return book;

    }

    @Override
@Caching(evict = {
    @CacheEvict(value = "book", key = "#id"),
    @CacheEvict(value = "books", allEntries = true)
})
public void updateBook(Long id, Book book) {
    Book existingBook = getBookById(id);
    existingBook.setTitle(book.getTitle());
    existingBook.setAuthor(book.getAuthor());
    existingBook.setDescription(book.getDescription());
    bookRepository.save(existingBook);
}

    @Override
    @CacheEvict(value = "books", allEntries = true)
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book with ID" + id + "not found.");
        }
        bookRepository.deleteById(id);
    }

//    @Override
//    public boolean isBookAvailableForReservation(Long bookId) {
//        Book book = getBookById(bookId);
//        return book.getCopiesAvailable() > 0 ;
//    }

//    @Override
//    @CacheEvict(value = "books", allEntries = true)
//    public void updateBookReservationStatus(Long bookId, boolean reserved) {
//        Book book = getBookById(bookId);
//        if (reserved) {
//            book.setCopiesAvailable(book.getCopiesAvailable() - 1);
//        } else {
//            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
//        }
//        bookRepository.save(book);
//
//    }
}

