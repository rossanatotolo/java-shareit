package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class Booking {
    private Long id;
    @NotNull(message = "Дата начала бронирования должна быть указана")
    private LocalDate start; //дата и время начала бронирования;
    @NotNull(message = "Дата конца бронирования должна быть указана")
    private LocalDate end; //дата и время конца бронирования;
    private Item item; // вещь, которую пользователь бронирует;
    private User booker; // пользователь, который осуществляет бронирование;
    private BookingStatus status;
}
