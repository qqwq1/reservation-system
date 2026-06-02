package ru.leshchev.reservation_system;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

//Обработка бизнес логики
@Service
public class ReservationService {
    private final Map<Long, Reservation> reservationMap = Map.of(
            1L, new Reservation(
                    0L,
                    0L,
                    10L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(2),
                    ReservationStatus.APPROVED),
            2L, new Reservation(
                    123L,
                    15L,
                    130L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(3),
                    ReservationStatus.PENDING),
            3L, new Reservation(
                    1111L,
                    45L,
                    310L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(1),
                    ReservationStatus.APPROVED)
    );

    public Reservation getReservationById(long id) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found Reservation by id -> " + id);
        }
        return reservationMap.get(id);
    }

    public List<Reservation> getAllReservations() {
        return List.copyOf(reservationMap.values());
    }
}
