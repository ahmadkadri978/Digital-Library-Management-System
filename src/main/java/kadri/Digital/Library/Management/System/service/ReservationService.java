package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.entity.Reservation;

import java.util.List;

public interface ReservationService {
    void createReservation(Long bookId, Long userId);
    void cancelReservation(Long reservationId);
    List<Reservation> getReservationsByUser(Long userId);
}
