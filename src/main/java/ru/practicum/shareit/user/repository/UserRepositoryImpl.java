package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(final long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User userCreate(final User user) {
        user.setId(getIdNext());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User userUpdate(final long userId, final User user) {
        final User userOld = users.get(userId);
        if (Objects.nonNull(user.getName())) {
            userOld.setName(user.getName());
        }
        if (Objects.nonNull(user.getEmail())) {
            userOld.setEmail(user.getEmail());
        }
        users.put(userId, userOld);
        return userOld;
    }

    @Override
    public void userDelete(final Long userId) {
        users.remove(userId);
    }

    private long getIdNext() {
        return ++currentId;
    }
}
