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
        logger.info("Saving new book: {}", book.getTitle());
        Book saved = bookRepository.save(book);
        logger.debug("Book saved with ID: {}", saved.getId());
        return saved;
    }

    @Override
    @Cacheable(value = "books", key = "#page + '-' + #size")
    public Page<Book> getAllBooks(int page, int size) {
        logger.info("Fetching all books - Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookRepository.findAll(pageable);
        logger.debug("Fetched {} books", books.getNumberOfElements());
        return books;
    }

    @Override
    @Cacheable(value = "booksByTitle", key = "#title + '-' + #page + '-' + #size")
    public Page<Book> searchBooksByTitle(String title, int page, int size) {
        logger.info("Searching books by title : '{}'", title);
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    @Cacheable(value = "booksByAuthor", key = "#author + '-' + #page + '-' + #size")
    public Page<Book> searchBooksByAuthor(String author, int page, int size) {
        logger.info("Searching books by author: '{}'", author);
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
    }

    @Override
    @Cacheable(value = "booksByIsbn", key = "#isbn + '-' + #page + '-' + #size")
    public Page<Book> searchBooksByIsbn(String isbn, int page, int size) {
        logger.info("Searching books by ISBN: '{}'", isbn);
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByIsbn(isbn, pageable);
    }

    @Override
    @Cacheable(value = "book", key = "#id")
    public Book getBookById(Long id) {
        logger.info("Retrieving book with ID: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Book with ID {} not found", id);
                    return new BookNotFoundException("Book with ID " + id + " not found.");
                });
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "book", key = "#id"),
            @CacheEvict(value = "books", allEntries = true)
    })
    public void updateBook(Long id, Book book) {
        logger.info("Updating book with ID: {}", id);
        Book existingBook = getBookById(id);
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        bookRepository.save(existingBook);
        logger.debug("Book with ID {} updated successfully", id);
    }

    @Override
    @CacheEvict(value = "books", allEntries = true)
    public void deleteBook(Long id) {
        logger.info("Deleting book with ID: {}", id);
        if (!bookRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existing book with ID: {}", id);
            throw new BookNotFoundException("Book with ID " + id + " not found.");
        }
        bookRepository.deleteById(id);
        logger.debug("Book with ID {} deleted", id);
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

