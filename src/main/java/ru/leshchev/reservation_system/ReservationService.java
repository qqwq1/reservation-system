package ru.leshchev.reservation_system;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

//Обработка бизнес логики
@Service
public class ReservationService {
    private final AtomicLong idCounter;
    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
        idCounter = new AtomicLong();
    }

    public Reservation getReservationById(long id) {
        ReservationEntity entity = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        return toDomainReservation(entity);
    }

    public List<Reservation> findAllReservations() {
        return repository.findAll().stream()
                .map(this::toDomainReservation)
                .toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }

        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        var savedEntity = repository.save(entityToSave);
        return toDomainReservation(savedEntity);
    }

    public Reservation updateReservation(long id, Reservation reservationToUpdate) {

        ReservationEntity oldReservationEntity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reservations table doesn't contains id -> " + id)
        );
        if (reservationToUpdate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (oldReservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Status should be \"PENDING\"");
        }
        ReservationEntity reservationToSave = new ReservationEntity(
                oldReservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        var updatedReservation = repository.save(reservationToSave);
        return toDomainReservation(updatedReservation);
    }

    public void deleteReservation(long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Reservations table doesn't contains id -> " + id);
        }
        repository.deleteById(id);
    }

    public Reservation approveReservation(long id) {

        ReservationEntity oldReservation = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reservations table doesn't contains id -> " + id)
        );
        if (oldReservation.getStatus() != ReservationStatus.PENDING) {
            throw new UnsupportedOperationException("Status of the reservation must be PENDING");
        }
        ReservationEntity updatedReservation = new ReservationEntity(
                oldReservation.getId(),
                oldReservation.getUserId(),
                oldReservation.getRoomId(),
                oldReservation.getStartDate(),
                oldReservation.getEndDate(),
                ReservationStatus.APPROVED
        );
        var reservationSave = repository.save(updatedReservation);
        return toDomainReservation(reservationSave);
    }

    private Reservation toDomainReservation(ReservationEntity entity) {
        return new Reservation(
                entity.getId(),
                entity.getUserId(),
                entity.getRoomId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus()
        );
    }
}
