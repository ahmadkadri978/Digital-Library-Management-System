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
public class ReservationServiceImpl implements ReservationService{
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    BookService bookService;
    @Autowired
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);
    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public void createReservation(Long bookId, Long userId) {
        // تحقق من توفر نسخ
        Book book = bookService.getBookById(bookId);
        if (book.getCopiesAvailable() <= 0) {
            logger.debug("No copies available for book ID: " + bookId);
            throw new BookNotAvailableException("No copies available for book with ID " + bookId + ".");
        }

        // تحقق من وجود حجز ACTIVE
        boolean alreadyActiveReservation = reservationRepository.existsByBookIdAndUserIdAndStatus(bookId, userId, "ACTIVE");
        if (alreadyActiveReservation) {
            logger.debug("Duplicate ACTIVE reservation for user ID: " + userId + " and book ID: " + bookId);
            throw new DuplicateReservationException("User with ID " + userId + " already has an ACTIVE reservation for book ID " + bookId + ".");
        }

        User user = userService.getUserById(userId);

        // إنشاء الحجز
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus("ACTIVE");

        reservationRepository.save(reservation);

        // خصم نسخة من الكتاب
        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        book.setReserved(true);
        bookService.saveBook(book);

        // تحديث حالة المستخدم
        long activeCount = reservationRepository.countByUserIdAndStatus(userId, "ACTIVE");
        if (activeCount == 1) {
            user.setReservation("ACTIVE");
            userService.save(user);
        }
    }

    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID " + reservationId + " not found."));

        reservation.setStatus("CANCELED");
        reservationRepository.save(reservation);

        User user = reservation.getUser();
        Book book = reservation.getBook();

        // إعادة نسخة إلى الكتاب
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);

        // إذا أصبحت كل النسخ متاحة، نلغي الحجز
        if (book.getCopiesAvailable() > 0) {
            book.setReserved(false);
        }

        bookService.saveBook(book);

        // تحديث حالة المستخدم
        long activeCount = reservationRepository.countByUserIdAndStatus(user.getId(), "ACTIVE");
        if (activeCount == 0) {
            logger.debug("No active reservations left for user ID: " + user.getId());
            user.setReservation("INACTIVE");
            userService.save(user);
        }
    }



    @Override
    @Cacheable(value = "reservations", key = "#userId")
    public List<Reservation> getReservationsByUser(Long userId) {
        User user = userService.getUserById(userId);

        return reservationRepository.findByUser(user);
    }
}
