package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.entity.Reservation;
import kadri.Digital.Library.Management.System.entity.User;
import kadri.Digital.Library.Management.System.repository.ReservationRepository;
import kadri.Digital.Library.Management.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ReservationServiceImpl implements ReservationService{
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    BookService bookService;
    @Autowired
    UserRepository userRepository;
    @Override
    public void createReservation(Long bookId, Long userId) {
        if (!bookService.isBookAvailableForReservation(bookId)) {
            throw new IllegalStateException("Book is not available for reservation.");
        }

        // تحقق إذا كان الحجز موجودًا لنفس المستخدم ونفس الكتاب
        boolean alreadyReserved = reservationRepository.existsByBookIdAndUserId(bookId, userId);
        if (alreadyReserved) {
            throw new IllegalStateException("You have already reserved this book.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookService.findBookById(bookId);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus("ACTIVE");
        reservationRepository.save(reservation);
        bookService.updateBookReservationStatus(reservation.getBook().getId(), true);

    }

    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus("CANCELED");
        reservationRepository.save(reservation);

        bookService.updateBookReservationStatus(reservation.getBook().getId(), false);

    }

    @Override
    public List<Reservation> getReservationsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reservationRepository.findByUser(user);
    }
}
