package ru.leshchev.reservation_system;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

//Обработка бизнес логики
@Service
public class ReservationService {
    private final Map<Long, Reservation> reservationMap;
    private final AtomicLong idCounter;

    public ReservationService(Map<Long, Reservation> reservationMap) {
        this.reservationMap = reservationMap;
        idCounter = new AtomicLong();
    }

    public Reservation getReservationById(long id) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found Reservation by id -> " + id);
        }
        return reservationMap.get(id);
    }

    public List<Reservation> getAllReservations() {
        return List.copyOf(reservationMap.values());
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException("Status   should be empty");
        }

        Reservation newReservation = new Reservation(
                idCounter.incrementAndGet(),
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        reservationMap.put(newReservation.id(), newReservation);
        return newReservation;
    }

    public Reservation updateReservation(long id, Reservation reservationToUpdate) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Reservations map doesn't contains id -> " + id);
        }
        Reservation oldReservation = reservationMap.get(id);
        if (reservationToUpdate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (oldReservation.status() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Status should be \"PENDING\"");
        }
        Reservation updatedReservation = new Reservation(
                oldReservation.id(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        reservationMap.put(id, updatedReservation);
        return updatedReservation;
    }

    public void deleteReservation(long id) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Reservations map doesn't contains id -> " + id);
        }
        reservationMap.remove(id);
    }

    //TODO: Добавить в метод проверку на доступность комнаты + еще раз просмотреть логику
    public Reservation approveReservation(long id) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Reservations map doesn't contains id -> " + id);
        }
        Reservation oldReservation = reservationMap.get(id);
        if (oldReservation.status() != ReservationStatus.PENDING) {
            throw new UnsupportedOperationException("Status of the reservation must be PENDING");
        }
        Reservation updatedReservation = new Reservation(
                oldReservation.id(),
                oldReservation.userId(),
                oldReservation.roomId(),
                oldReservation.startDate(),
                oldReservation.endDate(),
                ReservationStatus.APPROVED
        );
        reservationMap.put(id,updatedReservation);
        return updatedReservation;
    }
}
