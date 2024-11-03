package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemServiceSpringBootTest {

    private final ItemService itemService;

    private final UserService userService;

    private final BookingService bookingService;

    @BeforeEach
    public void setUp() {

        final UserDto userDto1 = new UserDto();
        userDto1.setName("Name1");
        userDto1.setEmail("ex1@mail.ru");
        userService.userCreate(userDto1);

        final UserDto userDto2 = new UserDto();
        userDto2.setName("Name2");
        userDto2.setEmail("ex2@mail.ru");
        userService.userCreate(userDto2);

        final UserDto userDto3 = new UserDto();
        userDto3.setName("Name3");
        userDto3.setEmail("ex3@mail.ru");
        userService.userCreate(userDto3);

        final ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Item1");
        itemDto1.setDescription("desc1");
        itemDto1.setAvailable(true);
        itemService.itemCreate(1, itemDto1);

        final ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Item2");
        itemDto2.setDescription("desc2");
        itemDto2.setAvailable(true);
        itemService.itemCreate(1, itemDto2);

        final ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("Item3");
        itemDto3.setDescription("desc3");
        itemDto3.setAvailable(true);
        itemService.itemCreate(2, itemDto3);

        final BookingDtoInput bookingDtoInput1 = new BookingDtoInput();
        bookingDtoInput1.setItemId(1L);
        bookingDtoInput1.setStart(LocalDateTime.now().minusDays(6));
        bookingDtoInput1.setEnd(LocalDateTime.now().minusDays(2));
        bookingService.createBooking(2L, bookingDtoInput1);
        bookingService.confirmationBooking(1L, 1L, true);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("ItemService_itemUpdate")
    void testItemUpdate() {

        final ItemDto itemDto = new ItemDto();
        itemDto.setName("TestName");
        itemDto.setDescription("TestDesc");
        itemDto.setAvailable(true);
        itemService.itemCreate(1, itemDto);

        itemDto.setName("TestName1");
        itemDto.setDescription("TestDesc1");
        itemService.itemUpdate(1, 4, itemDto);

        assertEquals("TestName1", itemDto.getName());
        assertEquals("TestDesc1", itemDto.getDescription());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("ItemService_getAllItems")
    void testGetAllItems() {

        assertEquals(2, itemService.getAllItems(1).size());
        assertEquals(1, itemService.getAllItems(2).size());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("ItemService_itemSearch")
    void testItemSearch() {

        assertEquals(1, itemService.itemSearch("Item1").size());
        assertEquals(3, itemService.itemSearch("I").size());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("ItemService_addComments")
    void testAddComments() {

        final CommentDto commentDto1 = new CommentDto();
        commentDto1.setText("Text-test");

        final CommentDto commentDto2 = itemService.addComments(2, 1, commentDto1);

        assertEquals("Text-test", commentDto2.getText());
        assertEquals("Name2", commentDto2.getAuthorName());
    }
}