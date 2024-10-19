package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.Collection;

public interface BookingService {
    BookingDtoOutput createBooking(final Long userId, BookingDtoInput bookingDtoInput);

    BookingDtoOutput confirmationBooking(final Long userId, final Long bookingId, final Boolean approved);

    BookingDtoOutput getBookingById(final Long userId, final Long bookingId);

    Collection<BookingDtoOutput> getAllBookingsFromUser(final Long userId, final String state);

    Collection<BookingDtoOutput> getAllBookingsFromOwner(final Long userId, final String state);
}
