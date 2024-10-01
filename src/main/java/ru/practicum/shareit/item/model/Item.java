package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class Item {
    private Long id;
    @NotBlank(message = "Имя должно быть указано")
    private String name;
    @NotBlank(message = "Описание должно быть указано")
    private String description;
    @NotNull(message = "Доступность вещи должна быть указана")
    private Boolean available; //статус о том, доступна или нет вещь для аренды;
    private User owner; //владелец вещи;
    private ItemRequest request; //хранит ссылку на нее, если вещь была создана по запросу
}
