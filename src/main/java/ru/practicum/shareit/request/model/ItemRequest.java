package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class ItemRequest {
    private Long id;
    @NotBlank(message = "Описание должно быть указано")
    private String description; // — текст запроса, содержащий описание требуемой вещи;
    private User requester; // — пользователь, создавший запрос;
    @NotNull
    private LocalDate created; // — дата и время создания запроса.
}
