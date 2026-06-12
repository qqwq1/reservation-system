package ru.leshchev.reservation_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


//Обработка Http запросов
@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") long id
    ) {
        log.info("Get.../id called getReservationById()");
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(reservationService.getReservationById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }


    }

    @GetMapping()
    public ResponseEntity<List<Reservation>> getAllReservations() {
        log.info("Get.../ called getAllReservations()");
        return ResponseEntity.ok()
                .body(reservationService.findAllReservations());
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestBody Reservation reservationToCreate
    ) {
        log.info("Post.../ called createReservation()");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationToCreate));
        //return reservationService.createReservation(reservationToCreate);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") long id,
            @RequestBody Reservation reservationToUpdate
    ) {
        log.info("Put.../id called updateReservation()");
        reservationService.updateReservation(id, reservationToUpdate);
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationToUpdate));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable("id") long id
    ) {
        log.info("Post.../id/approve called approveReservation()");
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(reservationService.approveReservation(id));
        } catch (UnsupportedOperationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Reservation> deleteReservation(
            @PathVariable("id") long id
    ) {
        log.info("Delete.../id called deleteReservation()");
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok()
                    .build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }

    }
}
