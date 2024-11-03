package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoOutput createBooking(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                          @RequestBody final BookingDtoInput bookingDtoInput) {
        return bookingService.createBooking(userId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput confirmationBooking(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                @PathVariable final Long bookingId,
                                                @RequestParam final Boolean approved) {
        return bookingService.confirmationBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getBookingById(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                           @PathVariable final Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoOutput> getAllBookingsFromUser(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                               @RequestParam(defaultValue = "ALL") final State state) {
        return bookingService.getAllBookingsFromUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOutput> getAllBookingsFromOwner(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                                @RequestParam(defaultValue = "ALL") final State state) {
        return bookingService.getAllBookingsFromOwner(userId, state);
    }
}
