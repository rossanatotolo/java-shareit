package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDtoInput {
    @NotBlank(message = "Дата начала бронирования должна быть указана")
    @Future(message = "Дата и время начала бронирования не может быть в прошлом")
    private LocalDateTime start;
    @NotBlank(message = "Дата конца бронирования должна быть указана")
    @Future(message = "Дата и время конца бронирования не может быть в прошлом")
    private LocalDateTime end;
    @NotBlank(message = "Идентификатор вещи не может быть пустым")
    private Long itemId;
}
