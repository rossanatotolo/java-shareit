package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoResponseRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "created");

    private User user1;

    private ItemRequestDto itemRequestDto1;

    private ItemRequest itemRequest1;

    private ItemRequest itemRequest2;

    private Item item1;

    private Item item2;

    @BeforeEach
    public void setUp() {

        ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
        UserMapper userMapper = new UserMapper();
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRequestRepository, itemRequestMapper,
                userMapper, itemRepository, itemMapper);

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Description");

        user1 = new User();
        user1.setName("Name1");
        user1.setEmail("ex1@yandex.ru");
        user1.setId(1L);

        itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("description1");
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setRequester(user1);

        itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("description2");
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setRequester(user1);

        item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("desc1");
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1.setRequest(itemRequest1);

        item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("desc2");
        item2.setOwner(user1);
        item2.setAvailable(true);
        item2.setRequest(itemRequest2);

        ItemDtoResponseRequest i1 = new ItemDtoResponseRequest();
        i1.setItemId(1L);
        i1.setOwnerId(1L);
        i1.setItemId(1L);
    }

    @Test
    @Order(1)
    @DisplayName("ItemRequestService_itemRequestCreateNotUser")
    void testItemRequestCreateNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.itemRequestCreate(1L, itemRequestDto1)
        );
    }

    @Test
    @Order(2)
    @DisplayName("ItemRequestService_itemRequestCreate")
    void testItemRequestCreate() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest1);

        final ItemRequestDtoOutput itemRequestDtoOutput =
                itemRequestService.itemRequestCreate(1L, itemRequestDto1);

        assertEquals("description1", itemRequestDtoOutput.getDescription());
        assertEquals("Name1", itemRequestDtoOutput.getRequester().getName());
    }

    @Test
    @Order(3)
    @DisplayName("ItemRequestService_getAllRequestByUser")
    void testGetAllRequestByUser() {

        when(itemRequestRepository.findAllByRequesterId(1L, sort))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findAllByRequest(itemRequest1)).thenReturn(List.of(item1));
        when(itemRepository.findAllByRequest(itemRequest2)).thenReturn(List.of(item2));

        final List<ItemRequestDtoOutput> itemRequestResponceDtoList = itemRequestService.getAllRequestByUser(1L);

        assertEquals(2, itemRequestResponceDtoList.size());
        assertEquals("Name1", itemRequestResponceDtoList.getFirst().getRequester().getName());
    }

    @Test
    @Order(4)
    @DisplayName("ItemRequestService_getAllRequests")
    void testGetAllRequests() {

        when(itemRequestRepository.findAllByRequesterIdNot(2L, sort))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        final List<ItemRequestDtoOutput> itemRequestResponceDtoList = itemRequestService.getAllRequests(2L);

        assertEquals(2, itemRequestResponceDtoList.size());
        assertEquals("Name1", itemRequestResponceDtoList.getFirst().getRequester().getName());
    }

    @Test
    @Order(5)
    @DisplayName("ItemRequestService_getByIdNotRequest")
    void testGetByIdNotRequest() {

        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getRequestById(1L)
        );
    }

    @Test
    @Order(6)
    @DisplayName("ItemRequestService_getRequestById")
    void testGetRequestById() {

        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByRequest(itemRequest1)).thenReturn(List.of(item1));

        final ItemRequestDtoOutput itemRequestDtoOutput = itemRequestService.getRequestById(1L);

        assertEquals("description1", itemRequestDtoOutput.getDescription());
        assertEquals("Name1", itemRequestDtoOutput.getRequester().getName());
        assertEquals(1, itemRequestDtoOutput.getItems().size());
    }
}
