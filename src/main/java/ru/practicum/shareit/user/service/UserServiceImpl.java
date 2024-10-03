package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение списка всех пользователей.");
        return userRepository.getAllUsers().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(final long userId) {
        log.info("Получение пользователя по id.");
        final User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto userCreate(final UserDto userDto) {
        if (userRepository.getAllUsers().contains(userMapper.toUser(userDto))) {
            log.warn("Пользователь с id {} уже добавлен в список.", userDto.getId());
            throw new DuplicatedDataException("Этот пользователь уже существует.");
        }

        checkUserEmail(userDto.getEmail());
        final User user = userMapper.toUser(userDto);
        log.info("Пользователь с id {} добавлен.", user.getId());
        return userMapper.toUserDto(userRepository.userCreate(user));
    }

    @Override
    public UserDto userUpdate(final long userId, final UserDto userDto) {
        checkUserId(userId);
        checkUserEmail(userDto.getEmail());
        final User user = userMapper.toUser(userDto);
        log.info("Пользователь с id {} обновлен.", user.getId());
        return userMapper.toUserDto(userRepository.userUpdate(userId, user));
    }

    @Override
    public void userDelete(final Long userId) {
        userRepository.userDelete(userId);
        log.info("Пользователь с id {} удален.", userId);
    }

    private void checkUserEmail(final String email) {
        for (User user : userRepository.getAllUsers()) {
            if (user.getEmail().equals(email)) {
                log.warn("Пользователь с email {} уже существует.", email);
                throw new DuplicatedDataException("Этот имейл уже существует.");
            }
        }
    }

    private void checkUserId(final Long userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            log.warn("Пользователя с id = {} не существует.", userId);
            throw new NotFoundException("Пользователя с id = {} не существует." + userId);
        }
    }
}
