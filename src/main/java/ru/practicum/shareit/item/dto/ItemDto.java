package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя должно быть указано")
    private String name;
    @NotBlank(message = "Описание должно быть указано")
    private String description;
    @NotNull(message = "Доступность вещи должна быть указана")
    private Boolean available; //статус о том, доступна или нет вещь для аренды;
}
