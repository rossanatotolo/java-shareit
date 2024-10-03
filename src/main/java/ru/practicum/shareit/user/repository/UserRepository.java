package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();

    Optional<User> getUserById(final long userId);

    User userCreate(final User user);

    User userUpdate(final long userId, final User user);

    void userDelete(final Long userId);
}
