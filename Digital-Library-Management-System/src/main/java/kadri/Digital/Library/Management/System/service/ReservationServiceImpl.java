package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.entity.Reservation;
import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.exception.BookNotAvailableException;
import kadri.Digital.Library.Management.System.exception.DuplicateReservationException;
import kadri.Digital.Library.Management.System.exception.ReservationNotFoundException;
import kadri.Digital.Library.Management.System.exception.UserNotFoundException;
import kadri.Digital.Library.Management.System.module.NotificationMessage;
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
public class ReservationServiceImpl implements ReservationService{
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    BookService bookService;
    @Autowired
    UserService userService;
    @Autowired
    private NotificationProducer notificationProducer;
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);
    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public void createReservation(Long bookId, Long userId) {
        if (!bookService.isBookAvailableForReservation(bookId)) {
            logger.debug("Book is not available for reservation. ID: " + bookId);
            throw new BookNotAvailableException("Book with ID " + bookId + " is not available for reservation.");
        }

        // تحقق إذا كان الحجز موجودًا لنفس المستخدم ونفس الكتاب
        boolean alreadyReserved = reservationRepository.existsByBookIdAndUserId(bookId, userId);
        if (alreadyReserved) {
            logger.debug("Duplicate reservation detected for user ID: " + userId + " and book ID: " + bookId);
            throw new DuplicateReservationException("User with ID " + userId + " has already reserved the book with ID " + bookId + ".");
        }
        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus("ACTIVE");

        logger.debug("Saving reservation for user ID: " + userId + " and book ID: " + bookId);
        reservationRepository.save(reservation);

        logger.debug("Updating book reservation status for book ID: " + bookId);
        bookService.updateBookReservationStatus(bookId, true);

        if (reservationRepository.countByUserId(userId) == 0) {
            logger.debug("First reservation for user ID: " + userId);
            user.setReservation("ACTIVE");
            userService.save(user);
        }

        NotificationMessage notificationMessage = new NotificationMessage(
                "New BookReservation",
                "The Book \"" + book.getTitle() + "\" booked by \"" + user.getUsername()

        );
        notificationProducer.sentNotification(notificationMessage);

    }

    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID " + reservationId + " not found."));

        reservation.setStatus("CANCELED");
        reservationRepository.save(reservation);

        User user = reservation.getUser();
        if (reservationRepository.countByUserId(user.getId()) == 0) {
            user.setReservation("INACTIVE");
            userService.save(user);
        }

        bookService.updateBookReservationStatus(reservation.getBook().getId(), false);

    }

    @Override
    @Cacheable(value = "reservations", key = "#userId")
    public List<Reservation> getReservationsByUser(Long userId) {
        User user = userService.getUserById(userId);

        return reservationRepository.findByUser(user);
    }
}
