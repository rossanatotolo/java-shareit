package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class User {
    private Long id;
    @NotBlank(message = "Имя пользователя должно быть указано")
    private String name;
    @NotNull(message = "Имейл должен быть указан")
    @Email(message = "Имейл должен содержать символ «@». Формат имейла: example@mail.com")
    private String email;
}
