package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(final long userId);

    UserDto userCreate(final UserDto userDto);

    UserDto userUpdate(final long userId, final UserDto userDto);

    void userDelete(final Long userId);
}
