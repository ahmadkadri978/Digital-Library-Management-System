package unittests;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.exception.BookNotFoundException;
import kadri.Digital.Library.Management.System.repository.BookRepository;
import kadri.Digital.Library.Management.System.service.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void testSaveBook() {
        Book book = new Book("Test Title", "Test Author", "Test Description", "1234567890123", 10);

        Mockito.when(bookRepository.save(book)).thenReturn(book);

        Book savedBook = bookService.saveBook(book);

        Assertions.assertEquals("Test Title", savedBook.getTitle());
        Assertions.assertEquals("Test Author", savedBook.getAuthor());
        Assertions.assertEquals("1234567890123", savedBook.getIsbn());
        Assertions.assertEquals(10, savedBook.getCopiesAvailable());
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }
    @Test
    void testGetAllBooks(){
        Pageable pageable = PageRequest.of(0,5);
        List<Book> books = List.of(
                new Book("Title1", "Author1", "Description1", "ISBN001", 5),
                new Book("Title2", "Author2", "Description2", "ISBN002", 3)
        );
        Page<Book> bookPage = new PageImpl<>(books,pageable, books.size());
        Mockito.when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        Page<Book> result=bookService.getAllBooks(0,5);

        Assertions.assertEquals(2,result.getContent().size());
        Mockito.verify(bookRepository,Mockito.times(1)).findAll(pageable);
    }
    @Test
    void testSearchBooksByTitle(){
        Pageable pageable = PageRequest.of(0,5);
        List<Book> books = List.of(new Book("Search Title", "Author1", "Description", "ISBN001", 5));
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        Mockito.when(bookRepository.findByTitleContainingIgnoreCase("Search",pageable)).thenReturn(bookPage);

        Page<Book> result = bookService.searchBooksByTitle("Search",0,5);

        Assertions.assertEquals(1,result.getTotalElements());
        Assertions.assertEquals("Search Title", result.getContent().get(0).getTitle());
        Mockito.verify(bookRepository, Mockito.times(1)).findByTitleContainingIgnoreCase("Search", pageable);

    }
    @Test
    void testGetBookById(){
        Book book = new Book("Title", "Author", "Description", "ISBN001", 5);
        book.setId(1L);
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Title", result.getTitle());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }
    @Test
    void testGetBookByIdNotFound() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BookNotFoundException.class,()->bookService.getBookById(1L));
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }
    @Test
    void testUpdateBook(){
        Book existingBook = new Book("Old Title", "Old Author", "Old Description", "ISBN001", 5);
        existingBook.setId(1L);

        Book updatedBook = new Book("New Title", "New Author", "New Description", "ISBN001", 5);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        Mockito.when(bookRepository.save(existingBook)).thenReturn(existingBook);

        bookService.updateBook(1L,updatedBook);

        Assertions.assertEquals("New Title", existingBook.getTitle());
        Assertions.assertEquals("New Author", existingBook.getAuthor());
        Assertions.assertEquals("New Description", existingBook.getDescription());
        Mockito.verify(bookRepository, Mockito.times(1)).save(existingBook);

    }
    @Test
    void testDeleteBook(){
        Mockito.when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(1L);
    }
    @Test
    void testDeleteBookNotFound() {
        Mockito.when(bookRepository.existsById(1L)).thenReturn(false);

        Assertions.assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
        Mockito.verify(bookRepository, Mockito.never()).deleteById(Mockito.any());
    }
    @Test
    void testisBookAvailableForReservation(){
        Book book = new Book("Title", "Author", "Description", "ISBN001", 5);
        book.setId(1L);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Boolean result = bookService.isBookAvailableForReservation(1L);

        Assertions.assertTrue(result);

    }
    @Test
    void bookIsNotAvailableForReservation(){
        Book book = new Book("Title", "Author", "Description", "ISBN001", 0);
        book.setId(1L);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Boolean result = bookService.isBookAvailableForReservation(1L);

        Assertions.assertFalse(result);
    }
    @Test
    void testUpdateBookReservationStatus() {
        Book book = new Book("Title", "Author", "Description", "ISBN001", 5);
        book.setId(1L);
        Book book2 = new Book("Title2", "Author2", "Description2", "ISBN001", 5);
        book2.setId(2L);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));

        bookService.updateBookReservationStatus(1L,true);

        Assertions.assertEquals(4,book.getCopiesAvailable());

        bookService.updateBookReservationStatus(2L,false);

        Assertions.assertEquals(6,book2.getCopiesAvailable());
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book2);
    }

}
