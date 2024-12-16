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
import org.springframework.beans.factory.annotation.Autowired;
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
    @Override
    public void createReservation(Long bookId, Long userId) {
        if (!bookService.isBookAvailableForReservation(bookId)) {
            throw new BookNotAvailableException("Book with ID " + bookId + " is not available for reservation.");
        }

        // تحقق إذا كان الحجز موجودًا لنفس المستخدم ونفس الكتاب
        boolean alreadyReserved = reservationRepository.existsByBookIdAndUserId(bookId, userId);
        if (alreadyReserved) {
            throw new DuplicateReservationException("User with ID " + userId + " has already reserved the book with ID " + bookId + ".");
        }
        User user = userService.getUserById(userId);


        Book book = bookService.getBookById(bookId);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus("ACTIVE");
        if (reservationRepository.countByUserId(userId) == 0) {
            user.setReservation("ACTIVE");
            userService.save(user);
        }
        reservationRepository.save(reservation);


        bookService.updateBookReservationStatus(bookId, true);

    }

    @Override
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
