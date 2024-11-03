package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponseRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemRequestServiceSpringBootTest {

    private final ItemRequestService itemRequestService;

    private final ItemService itemService;

    private final UserService userService;

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

        final ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Description1");
        itemRequestService.itemRequestCreate(1L, itemRequestDto1);

        final ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("Description2");
        itemRequestService.itemRequestCreate(1L, itemRequestDto2);

        final ItemRequestDto itemRequestDto3 = new ItemRequestDto();
        itemRequestDto3.setDescription("Description3");
        itemRequestService.itemRequestCreate(2L, itemRequestDto3);

        final ItemDto itemDto = new ItemDto();
        itemDto.setName("Name");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(3L);
        itemService.itemCreate(1L, itemDto);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("ItemRequestService_itemRequestCreate")
    void testItemRequestCreate() {

        final ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("test");

        final ItemRequestDtoOutput i = itemRequestService.itemRequestCreate(1L, itemRequestDto);

        assertEquals("Name1", i.getRequester().getName());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("ItemRequestService_getAllRequestByUser")
    void testGetAllRequestByUser() {

        final List<ItemRequestDtoOutput> i = itemRequestService.getAllRequestByUser(1L);

        assertEquals(2, i.size());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("ItemRequestService_getAllRequests")
    void testGetAllRequests() {

        final ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("test");
        itemRequestService.itemRequestCreate(2L, itemRequestDto);

        final List<ItemRequestDtoOutput> i = itemRequestService.getAllRequests(1L);

        assertEquals(2, i.size());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("ItemRequestService_getRequestById")
    void testGetRequestById() {

        final ItemRequestDtoOutput i = itemRequestService.getRequestById(3L);

        final List<ItemDtoResponseRequest> items = i.getItems();

        assertEquals("Description3", i.getDescription());
        assertEquals(1, items.size());
        assertEquals("Name", items.get(0).getName());
    }
}
