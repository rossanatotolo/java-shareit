package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceSpringBootTest {

    private final UserService userService;

    @BeforeEach
    public void setUp() {

        final UserDto userDto1 = new UserDto();
        userDto1.setName("Name1");
        userDto1.setEmail("example1@mail.ru");
        userService.userCreate(userDto1);

        final UserDto userDto2 = new UserDto();
        userDto2.setName("Name2");
        userDto2.setEmail("example2@mail.ru");
        userService.userCreate(userDto2);

        final UserDto userDto = new UserDto();
        userDto.setName("Name3");
        userDto.setEmail("example3@mail.ru");
        userService.userCreate(userDto);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("UserService_userCreate")
    void testUserCreate() {

        assertEquals(3, userService.getAllUsers().size());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("UserService_userUpdate")
    void testUserUpdate() {

        final UserDto userDto1 = new UserDto();
        userDto1.setName("NewName1");
        userDto1.setEmail("newExample@mail.ru");
        userService.userUpdate(3, userDto1);

        assertEquals("NewName1", userService.getUserById(3).getName());
        assertEquals("newExample@mail.ru", userService.getUserById(3).getEmail());

        final UserDto userDto2 = new UserDto();
        userDto2.setName("NewName2");
        userService.userUpdate(3, userDto2);

        assertEquals("NewName2", userService.getUserById(3).getName());

        final UserDto userDto3 = new UserDto();
        userDto3.setEmail("new@mail.ru");
        userService.userUpdate(3, userDto3);

        assertEquals("new@mail.ru", userService.getUserById(3).getEmail());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("UserService_getUserById")
    void testGetUserById() {

        assertEquals("Name1", userService.getUserById(1).getName());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("UserService_userDelete")
    void testUserDelete() {

        userService.userDelete(3L);

        assertEquals(2, userService.getAllUsers().size());
    }
}
