package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.validation.ValidBookingTime;

import java.time.LocalDateTime;

@Data
@ValidBookingTime
public class BookingDtoInput {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
