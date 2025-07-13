package unittests;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.exception.BookNotFoundException;
import kadri.Digital.Library.Management.System.repository.BookRepository;

import kadri.Digital.Library.Management.System.service.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        book = new Book("Title", "Author", "ISBN", "Description", 2024, true);
        book.setId(1L);
    }

    @Test
    void saveBook_ShouldSaveSuccessfully() {
        when(bookRepository.save(book)).thenReturn(book);
        Book saved = bookService.saveBook(book);
        assertEquals(book, saved);
        verify(bookRepository).save(book);
    }

    @Test
    void getAllBooks_ShouldReturnPaginatedList() {
        List<Book> books = List.of(book);
        Page<Book> page = new PageImpl<>(books);
        when(bookRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<Book> result = bookService.getAllBooks(0, 10);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchBooksByTitle_ShouldReturnMatchingBooks() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findByTitleContainingIgnoreCase("Title", PageRequest.of(0, 5))).thenReturn(page);

        Page<Book> result = bookService.searchBooksByTitle("Title", 0, 5);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getBookById_ShouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Book result = bookService.getBookById(1L);
        assertEquals(book, result);
    }

    @Test
    void getBookById_ShouldThrowException_WhenNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void updateBook_ShouldUpdateSuccessfully() {
        Book updated = new Book("New", "NewAuthor", "ISBN", "NewDesc", 2024, true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.updateBook(1L, updated);
        verify(bookRepository).save(book);
        assertEquals("New", book.getTitle());
    }

    @Test
    void deleteBook_ShouldDeleteSuccessfully() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        bookService.deleteBook(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_ShouldThrowException_WhenNotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
    }
}

