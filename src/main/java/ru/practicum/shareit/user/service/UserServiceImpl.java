package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        log.info("Получение списка всех пользователей.");
        return userMapper.toListDto(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(final long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        log.info("Получение пользователя по id.");
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto userCreate(final UserDto userDto) {
        if (userRepository.findAll().contains(userMapper.toUser(userDto))) {
            log.warn("Пользователь с id {} уже добавлен в список.", userDto.getId());
            throw new DuplicatedDataException("Этот пользователь уже существует.");
        }

        final User user = userRepository.save(userMapper.toUser(userDto));
        log.info("Пользователь с id {} добавлен.", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto userUpdate(final long userId, final UserDto userDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));

        if (Objects.nonNull(userDto.getName())) {
            user.setName(userDto.getName());
        }
        if (Objects.nonNull(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }

        final User userUpdate = userRepository.save(user);
        log.info("Пользователь с id {} обновлен.", user.getId());
        return userMapper.toUserDto(userUpdate);
    }

    @Override
    @Transactional
    public void userDelete(final Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));

        userRepository.delete(user);
        log.info("Пользователь с id {} удален.", userId);
    }
}
