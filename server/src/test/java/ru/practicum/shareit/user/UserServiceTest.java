package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;

    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    private UserDto userDto1;

    private User user1;

    @BeforeEach
    public void setUp() {

        userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper);

        userDto1 = new UserDto();
        userDto1.setName("Name1");
        userDto1.setEmail("example1@mail.ru");

        user1 = new User();
        user1.setName("Name1");
        user1.setEmail("example1@yandex.ru");
        user1.setId(1L);
    }

    @Test
    @Order(1)
    @DisplayName("UserService_userCreate")
    void testUserCreate() {

        when(userRepository.save(userMapper.toUser(userDto1))).thenReturn(user1);

        final UserDto userDto = userService.userCreate(userDto1);

        assertEquals("Name1", userDto.getName());
        assertEquals(1, userDto.getId());
    }

    @Test
    @Order(2)
    @DisplayName("UserService_getUserById")
    void testGetUserById() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        assertEquals("Name1", userService.getUserById(1).getName());
    }

    @Test
    @Order(3)
    @DisplayName("UserService_getByIdNotUser")
    void testGetByIdNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(3)
        );
    }

    @Test
    @Order(4)
    @DisplayName("UserService_updateNotUser")
    void testUpdateNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> userService.userUpdate(1, userDto1)
        );
    }

    @Test
    @Order(5)
    @DisplayName("UserService_updateSetName")
    void testUpdateSetName() {

        final UserDto userDto2 = new UserDto();
        userDto2.setName("newName");

        final User user2 = new User();
        user2.setName("newName");
        user2.setEmail("new@yandex.ru");
        user2.setId(1L);

        when(userRepository.save(userMapper.toUser(userDto1))).thenReturn(user1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.save(user2)).thenReturn(user2);

        userService.userCreate(userDto1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        final UserDto userDto = userService.userUpdate(1, userDto2);

        assertEquals("newName", userDto.getName());
    }

    @Test
    @Order(6)
    @DisplayName("UserService_updateSetEmail")
    void testUpdateSetEmail() {

        final UserDto userDto3 = new UserDto();
        userDto3.setEmail("newEmail@mail.ru");

        final User user3 = new User();
        user3.setName("Mia");
        user3.setEmail("newEmail@mail.ru");
        user3.setId(1L);

        when(userRepository.save(userMapper.toUser(userDto1))).thenReturn(user1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.save(user3)).thenReturn(user3);

        userService.userCreate(userDto1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user3));
        final UserDto userDto = userService.userUpdate(1, userDto3);

        assertEquals("newEmail@mail.ru", userDto.getEmail());
    }

    @Test
    @Order(7)
    @DisplayName("UserService_getAllUsers")
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1));

        final List<UserDto> users = userService.getAllUsers();

        assertEquals(1, users.size());
    }

    @Test
    @Order(8)
    @DisplayName("UserService_deleteNotUser")
    void testDeleteNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> userService.userDelete(3L)
        );
    }

    @Test
    @Order(9)
    @DisplayName("UserService_userDelete")
    void testUserDelete() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        userService.userDelete(1L);

        verify(userRepository).delete(any(User.class));
    }
}
