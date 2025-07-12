package kadri.Digital.Library.Management.System.repository;

import kadri.Digital.Library.Management.System.entity.Book;
import kadri.Digital.Library.Management.System.entity.Reservation;
import kadri.Digital.Library.Management.System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByBook(Book book);

    boolean existsByBookIdAndUserId(Long bookId, Long userId);
    long countByUserIdAndStatus(Long userId, String status);
    boolean existsByBookIdAndUserIdAndStatus(Long bookId, Long userId, String status);



    int countByUserId(Long userId);
}
