package unittests;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.entity.Reservation;
import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.exception.BookNotAvailableException;
import kadri.Digital.Library.Management.System.exception.DuplicateReservationException;
import kadri.Digital.Library.Management.System.exception.ReservationNotFoundException;
import kadri.Digital.Library.Management.System.repository.BookRepository;
import kadri.Digital.Library.Management.System.repository.ReservationRepository;
import kadri.Digital.Library.Management.System.service.BookService;
import kadri.Digital.Library.Management.System.service.BookServiceImpl;
import kadri.Digital.Library.Management.System.service.ReservationServiceImpl;
import kadri.Digital.Library.Management.System.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void testCreateReservationSuccess() {
        Long bookId = 1L;
        Long userId = 1L;

        Book book = new Book("Book Title", "Author", "Description", "ISBN", 3);
        book.setId(bookId);

        User user = new User("username", "name", "avatar", "USER");
        user.setId(userId);

        when(bookService.isBookAvailableForReservation(bookId)).thenReturn(true);
        when(reservationRepository.existsByBookIdAndUserId(bookId,userId)).thenReturn(false);
        when(userService.getUserById(userId)).thenReturn(user);
        when(bookService.getBookById(bookId)).thenReturn(book);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus("ACTIVE");

        when(reservationRepository.countByUserId(userId)).thenReturn(0);
        user.setReservation("ACTIVE");
        doNothing().when(userService).save(any(User.class));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        reservationService.createReservation(bookId, userId);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(bookService,times(1)).updateBookReservationStatus(bookId,true);
        verify(reservationRepository,times(1)).countByUserId(userId);
        verify(userService,times(1)).save(user);

    }
    @Test
    void testCreateReservationBookNotAvailable() {
        Long bookId = 1l;
        Long userId = 1L;

        when(bookService.isBookAvailableForReservation(bookId)).thenReturn(false);

        assertThrows(BookNotAvailableException.class, () -> reservationService.createReservation(bookId,userId));
        verify(reservationRepository,never()).save(any());
    }

    @Test
    void testCreateReservationDuplicateReservation() {
        Long bookId = 1L;
        Long userId = 1L;

        when(bookService.isBookAvailableForReservation(bookId)).thenReturn(true);
        when(reservationRepository.existsByBookIdAndUserId(bookId, userId)).thenReturn(true);

        assertThrows(DuplicateReservationException.class, () -> reservationService.createReservation(bookId, userId));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCancelReservation_Success() {
        Long reservationId = 1L;
        Long userId = 1L;

        User user = new User("username", "name", "avatar", "USER");
        user.setId(userId);

        Book book = new Book("Book Title", "Author", "Description", "ISBN", 3);

        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus("ACTIVE");

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        reservation.setStatus("CANCELED");
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        when(reservationRepository.countByUserId(userId)).thenReturn(0);
        user.setReservation("INACTIVE");
        doNothing().when(userService).save(user);

        doAnswer(invocation -> {
            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
            return null;
        }).when(bookService).updateBookReservationStatus(reservation.getBook().getId(), false);

        reservationService.cancelReservation(reservationId);
        verify(userService, times(1)).save(user);
        verify(reservationRepository, times(1)).save(reservation);
        verify(bookService, times(1)).updateBookReservationStatus(reservation.getBook().getId(),false);

        assertEquals("CANCELED" ,reservation.getStatus());
        assertEquals("INACTIVE", user.getReservation());
        assertEquals(4, book.getCopiesAvailable());
    }

    @Test
    void testCancelReservation_NotFound() {
        Long reservationId = 1L;

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> reservationService.cancelReservation(reservationId));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testGetReservationsByUser() {
        Long userId = 1L;

        User user = new User("username", "name", "avatar", "USER");
        user.setId(userId);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStatus("ACTIVE");

        when(userService.getUserById(userId)).thenReturn(user);
        when(reservationRepository.findByUser(user)).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByUser(userId);

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findByUser(user);
    }

}
