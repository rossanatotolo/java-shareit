package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
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
                                                               @RequestParam(defaultValue = "ALL") final String state) {
        return bookingService.getAllBookingsFromUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOutput> getAllBookingsFromOwner(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                                @RequestParam(defaultValue = "ALL") final String state) {
        return bookingService.getAllBookingsFromOwner(userId, state);
    }
}
