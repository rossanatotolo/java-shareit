package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public Booking toBooking(final BookingDtoInput bookingDtoInput, final User user, final Item item) {
        final Booking booking = new Booking();

        booking.setStart(bookingDtoInput.getStart());
        booking.setEnd(bookingDtoInput.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    public BookingDtoOutput toBookingDtoOutput(final Booking booking, final UserDto userDto,
                                               final ItemDto itemDto) {

        final BookingDtoOutput bookingDtoOutput = new BookingDtoOutput();

        bookingDtoOutput.setId(booking.getId());
        bookingDtoOutput.setBooker(userDto);
        bookingDtoOutput.setItem(itemDto);
        bookingDtoOutput.setStart(booking.getStart());
        bookingDtoOutput.setEnd(booking.getEnd());
        bookingDtoOutput.setStatus(booking.getStatus());

        return bookingDtoOutput;
    }
}
