package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.entity.Reservation;
import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.exception.BookNotAvailableException;
import kadri.Digital.Library.Management.System.exception.DuplicateReservationException;
import kadri.Digital.Library.Management.System.exception.ReservationNotFoundException;
import kadri.Digital.Library.Management.System.exception.UserNotFoundException;
import kadri.Digital.Library.Management.System.repository.ReservationRepository;
import kadri.Digital.Library.Management.System.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public void createReservation(Long bookId, Long userId) {
        logger.debug("Creating reservation for user ID: {}, book ID: {}", userId, bookId);

        Book book = bookService.getBookById(bookId);
        User user = userService.getUserById(userId);

        if (book.getCopiesAvailable() <= 0) {
            logger.warn("No copies available for book ID: {}", bookId);
            throw new BookNotAvailableException("No copies available for book with ID " + bookId + ".");
        }

        boolean alreadyActiveReservation = reservationRepository.existsByBookIdAndUserIdAndStatus(bookId, userId, "ACTIVE");
        if (alreadyActiveReservation) {
            logger.warn("Duplicate ACTIVE reservation for user: {}, book ID: {}", user.getUsername(), bookId);
            throw new DuplicateReservationException("User : " + user.getUsername() + " already has an ACTIVE reservation for book ID " + bookId + ".");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus("ACTIVE");

        reservationRepository.save(reservation);
        logger.info("Reservation created successfully for user: {}, book: {}", user.getUsername(), book.getTitle());

        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        book.setReserved(true);
        bookService.saveBook(book);
        logger.debug("Updated book availability for '{}'. Remaining copies: {}", book.getTitle(), book.getCopiesAvailable());

        long activeCount = reservationRepository.countByUserIdAndStatus(userId, "ACTIVE");
        if (activeCount == 1) {
            user.setReservation("ACTIVE");
            userService.save(user);
            logger.debug("User {} status set to ACTIVE", user.getUsername());
        }
    }

    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public void cancelReservation(Long reservationId) {
        logger.debug("Canceling reservation with ID : {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID " + reservationId + " not found."));

        reservation.setStatus("CANCELED");
        reservationRepository.save(reservation);
        logger.info("Reservation ID {} canceled", reservationId);

        User user = reservation.getUser();
        Book book = reservation.getBook();

        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        if (book.getCopiesAvailable() > 0) {
            book.setReserved(false);
        }
        bookService.saveBook(book);
        logger.debug("Updated book availability after cancellation. Copies available: {}", book.getCopiesAvailable());

        long activeCount = reservationRepository.countByUserIdAndStatus(user.getId(), "ACTIVE");
        if (activeCount == 0) {
            user.setReservation("INACTIVE");
            userService.save(user);
            logger.debug("User {} status set to INACTIVE", user.getUsername());
        }
    }

    @Override
    @Cacheable(value = "reservations", key = "#userId")
    public List<Reservation> getReservationsByUser(Long userId) {
        logger.debug("Fetching reservations for user ID: {}", userId);

        User user = userService.getUserById(userId);
        List<Reservation> reservations = reservationRepository.findByUser(user);

        logger.debug("Found {} reservations for user ID: {}", reservations.size(), userId);
        return reservations;
    }
}

