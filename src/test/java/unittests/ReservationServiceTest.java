package unittests;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.entity.Reservation;
import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.repository.ReservationRepository;
import kadri.Digital.Library.Management.System.service.BookService;
import kadri.Digital.Library.Management.System.service.ReservationServiceImpl;
import kadri.Digital.Library.Management.System.service.UserService;
import kadri.Digital.Library.Management.System.exception.BookNotAvailableException;
import kadri.Digital.Library.Management.System.exception.DuplicateReservationException;
import kadri.Digital.Library.Management.System.exception.ReservationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    private Book book;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        book = new Book("Title", "Author", "ISBN", "Description", 2024, true);
        book.setId(1L);
        book.setCopiesAvailable(1);

        user = new User();
        user.setId(1L);
        user.setUsername("kadri");
    }

    @Test
    void createReservation_shouldCreateSuccessfully() {
        when(bookService.getBookById(1L)).thenReturn(book);
        when(userService.getUserById(1L)).thenReturn(user);
        when(reservationRepository.existsByBookIdAndUserIdAndStatus(1L, 1L, "ACTIVE")).thenReturn(false);
        when(reservationRepository.countByUserIdAndStatus(1L, "ACTIVE")).thenReturn(1L);

        reservationService.createReservation(1L, 1L);

        assertEquals(0, book.getCopiesAvailable());
        assertTrue(book.getReserved());
        verify(reservationRepository).save(any(Reservation.class));
        verify(bookService).saveBook(book);
        verify(userService).save(user);
    }

    @Test
    void createReservation_shouldThrow_WhenBookUnavailable() {
        book.setCopiesAvailable(0);
        when(bookService.getBookById(1L)).thenReturn(book);
        when(userService.getUserById(1L)).thenReturn(user);

        assertThrows(BookNotAvailableException.class,
                () -> reservationService.createReservation(1L, 1L));
    }

    @Test
    void createReservation_shouldThrow_WhenDuplicateReservationExists() {
        when(bookService.getBookById(1L)).thenReturn(book);
        when(userService.getUserById(1L)).thenReturn(user);
        when(reservationRepository.existsByBookIdAndUserIdAndStatus(1L, 1L, "ACTIVE")).thenReturn(true);

        assertThrows(DuplicateReservationException.class,
                () -> reservationService.createReservation(1L, 1L));
    }

    @Test
    void cancelReservation_shouldCancelSuccessfully() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus("ACTIVE");
        reservation.setBook(book);
        reservation.setUser(user);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.countByUserIdAndStatus(1L, "ACTIVE")).thenReturn(0L);

        reservationService.cancelReservation(1L);

        assertEquals("CANCELED", reservation.getStatus());
        assertEquals(2, book.getCopiesAvailable());
        verify(reservationRepository).save(reservation);
        verify(bookService).saveBook(book);
        verify(userService).save(user);
    }

    @Test
    void cancelReservation_shouldThrow_WhenReservationNotFound() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> reservationService.cancelReservation(999L));
    }

    @Test
    void getReservationsByUser_shouldReturnList() {
        Reservation reservation = new Reservation();
        reservation.setUser(user);

        when(userService.getUserById(1L)).thenReturn(user);
        when(reservationRepository.findByUser(user)).thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getReservationsByUser(1L);
        assertEquals(1, result.size());
    }
}

