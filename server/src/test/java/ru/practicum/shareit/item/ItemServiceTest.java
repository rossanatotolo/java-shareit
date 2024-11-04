package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    private ItemService itemService;

    private ItemMapper itemMapper;

    private CommentMapper commentMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user1;

    private ItemDto itemDto1;

    private Item item1;

    private Item item2;

    private CommentDto commentDto1;

    private Comment comment;

    private ItemDtoOutput itemDtoOutput;

    @BeforeEach
    public void setUp() {

        itemMapper = new ItemMapper();
        commentMapper = new CommentMapper();
        itemService = new ItemServiceImpl(itemRepository, userRepository, itemMapper, commentRepository,
                commentMapper, bookingRepository, itemRequestRepository);

        user1 = new User();
        user1.setName("Name1");
        user1.setEmail("ex1@yandex.ru");
        user1.setId(1L);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("test desc1");
        itemRequest1.setRequester(user1);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setId(1L);

        itemDto1 = new ItemDto();
        itemDto1.setName("Item1");
        itemDto1.setDescription("desc1");
        itemDto1.setAvailable(true);

        item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("desc1");
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1.setId(1L);

        item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("desc2");
        item2.setOwner(user1);
        item2.setAvailable(true);
        item2.setRequest(itemRequest1);

        commentDto1 = new CommentDto();
        commentDto1.setText("test-text");
        commentDto1.setAuthorName("Name1");
        commentDto1.setCreated(LocalDateTime.now());
        commentDto1.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setItem(item1);
        comment.setText("test-text");
        comment.setAuthor(user1);
        comment.setCreated(LocalDateTime.now());

        itemDtoOutput = new ItemDtoOutput();
        itemDtoOutput.setId(2L);
        itemDtoOutput.setName("Test Item");
        itemDtoOutput.setDescription("Test Description");
        itemDtoOutput.setAvailable(true);
    }

    @Test
    @Order(1)
    @DisplayName("ItemService_createNotOwner")
    void testCreateNotOwner() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.itemCreate(1, itemDto1)
        );
    }

    @Test
    @Order(2)
    @DisplayName("ItemService_createWithOutRequest")
    void testCreateWithOutRequest() {

        item1 = itemMapper.toItem(user1, itemDto1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.save(itemMapper.toItem(user1, itemDto1))).thenReturn(item1);
        itemDto1 = itemMapper.toItemDto(item1);
        final ItemDto itemDto = itemService.itemCreate(1L, itemDto1);

        assertEquals("Item1", itemDto.getName());
        assertNotNull(itemDto);
        verify(userRepository).findById(1L);
        verify(itemRepository).save(item1);
    }

    @Test
    @Order(3)
    @DisplayName("ItemService_createWithRequest")
    void testCreateWithRequest() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.save(itemMapper.toItem(user1, itemDto1))).thenReturn(item2);

        final ItemDto itemDto = itemService.itemCreate(1L, itemDto1);

        assertEquals("Item2", itemDto.getName());
        assertEquals(1, itemDto.getRequestId());
    }

    @Test
    @Order(4)
    @DisplayName("ItemService_itemCreateUserNotFound")
    void testItemCreateUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.itemCreate(1L, itemDto1);
        });

        assertEquals("Пользователя с id = {} нет." + 1L, exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("ItemService_updateNotItem")
    void testUpdateNotItem() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.itemUpdate(1L, 1L, itemDto1)
        );
    }

    @Test
    @Order(6)
    @DisplayName("ItemService_itemUpdateSuccess")
    void testItemUpdateSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        when(itemRepository.save(item2)).thenReturn(item2);

        ItemDto updatedItemDto = itemService.itemUpdate(1L, 2L, itemDto1);

        assertNotNull(updatedItemDto);
        assertEquals("Item1", updatedItemDto.getName());
        assertEquals("desc1", updatedItemDto.getDescription());
        assertTrue(updatedItemDto.getAvailable());
        verify(itemRepository).save(item2);

    }

    @Test
    @Order(7)
    @DisplayName("ItemService_ItemUpdateNotFound")
    void testItemUpdateNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.itemUpdate(1L, 1L, itemDto1);
        });

        assertEquals("Вещь с id: 1 не найдена.", exception.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("ItemService_updateName")
    void testUpdateName() {

        itemDto1.setName("nameeeNew");
        Item item10 = itemMapper.toItem(user1, itemDto1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item10));
        when(itemRepository.save(item10)).thenReturn(item10);

        ItemDto updatedItemDto = itemService.itemUpdate(1L, 1L, itemDto1);

        assertEquals("nameeeNew", updatedItemDto.getName());
    }

    @Test
    @Order(9)
    @DisplayName("ItemService_updateDescription")
    void testUpdateDescription() {

        itemDto1.setDescription("teeeest");
        Item item10 = itemMapper.toItem(user1, itemDto1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item10));
        when(itemRepository.save(item10)).thenReturn(item10);

        ItemDto updatedItemDto = itemService.itemUpdate(1L, 1L, itemDto1);

        assertEquals("teeeest", updatedItemDto.getDescription());
        assertNotNull(updatedItemDto);
        verify(itemRepository, times(1)).save(item10);

    }

    @Test
    @Order(10)
    @DisplayName("ItemService_updateDescriptionIsNull")
    void testUpdateDescriptionIsNull() {

        itemDto1.setDescription(null);
        Item item10 = itemMapper.toItem(user1, itemDto1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item10));
        when(itemRepository.save(item10)).thenReturn(item10);

        ItemDto updatedItemDto = itemService.itemUpdate(1L, 1L, itemDto1);

        assertEquals(null, updatedItemDto.getDescription());
        assertNull(updatedItemDto.getDescription());
    }

    @Test
    @Order(11)
    @DisplayName("ItemService_updateDescriptionIsBlank")
    void testUpdateDescriptionIsBlank() {

        itemDto1.setDescription(" ");
        Item item10 = itemMapper.toItem(user1, itemDto1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item10));
        when(itemRepository.save(item10)).thenReturn(item10);

        ItemDto updatedItemDto = itemService.itemUpdate(1L, 1L, itemDto1);

        assertEquals(" ", updatedItemDto.getDescription());
    }

    @Test
    @Order(12)
    @DisplayName("ItemService_updateNameIsNull")
    void testUpdateNameIsNull() {

        itemDto1.setName(null);
        Item item10 = itemMapper.toItem(user1, itemDto1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item10));
        when(itemRepository.save(item10)).thenReturn(item10);

        ItemDto updatedItemDto = itemService.itemUpdate(1L, 1L, itemDto1);

        assertEquals(null, updatedItemDto.getName());
        assertNull(updatedItemDto.getName());
    }

    @Test
    @Order(13)
    @DisplayName("ItemService_updateNameIsBlank")
    void testUpdateNameIsBlank() {

        itemDto1.setName(" ");
        Item item10 = itemMapper.toItem(user1, itemDto1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item10));
        when(itemRepository.save(item10)).thenReturn(item10);

        ItemDto updatedItemDto = itemService.itemUpdate(1L, 1L, itemDto1);

        assertEquals(" ", updatedItemDto.getName());
    }

    @Test
    @Order(14)
    @DisplayName("ItemService_updateAvailableIsNull")
    void testUpdateAvailableIsNull() {

        itemDto1.setAvailable(null);
        Item item10 = itemMapper.toItem(user1, itemDto1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item10));
        when(itemRepository.save(item10)).thenReturn(item10);

        ItemDto updatedItemDto = itemService.itemUpdate(1L, 1L, itemDto1);

        assertEquals(null, updatedItemDto.getAvailable());
        assertNull(updatedItemDto.getAvailable());
    }

    @Test
    @Order(15)
    @DisplayName("ItemService_updateAvailable")
    void testUpdateAvailable() {

        itemDto1.setAvailable(false);
        Item item10 = itemMapper.toItem(user1, itemDto1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item10));
        when(itemRepository.save(item10)).thenReturn(item10);

        ItemDto updatedItemDto = itemService.itemUpdate(1L, 1L, itemDto1);

        assertEquals(false, updatedItemDto.getAvailable());
    }

    @Test
    @Order(16)
    @DisplayName("ItemService_getItemByIdSuccess")
    void testGetItemByIdSuccess() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.singletonList(comment));
        commentDto1 = commentMapper.toCommentDto(comment);
        itemDtoOutput = itemMapper.toItemDtoOutput(item1, Collections.singletonList(commentDto1));

        Booking lastBooking = new Booking();
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        Booking futureBooking = new Booking();
        futureBooking.setStart(LocalDateTime.now().plusDays(1));

        when(bookingRepository.findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(eq(1L), any(), anyList()))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(eq(1L), any(), anyList()))
                .thenReturn(Optional.of(futureBooking));

        ItemDtoOutput result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals("Item1", result.getName());
        assertEquals("desc1", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(1, result.getComments().size());
        assertEquals("test-text", result.getComments().get(0).getText());
    }

    @Test
    @Order(17)
    @DisplayName("ItemService_getByIdNotItem")
    void testGetByIdNotItem() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(2, 5)
        );
    }

    @Test
    @Order(18)
    @DisplayName("ItemService_getByIdNoOwner")
    void testGetByIdNoOwner() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1)).thenReturn(List.of(comment));

        itemService.getItemById(5, 1);

        verify(bookingRepository, never()).findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(1,
                LocalDateTime.now(), List.of(BookingStatus.APPROVED));
        verify(bookingRepository, never()).findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(1,
                LocalDateTime.now(), List.of(BookingStatus.APPROVED));
    }

    @Test
    @Order(19)
    @DisplayName("ItemService_deleteNotItem")
    void testDeleteNotItem() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.itemDelete(5L)
        );
    }

    @Test
    @Order(20)
    @DisplayName("ItemService_deleteNotFound")
    void testItemDeleteNotFound() {

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.itemDelete(1L);
        });

        assertEquals("Вещь с id: 1 не найдена.", exception.getMessage());
    }


    @Test
    @Order(21)
    @DisplayName("ItemService_itemDelete")
    void testItemDelete() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        itemService.itemDelete(1L);

        verify(itemRepository).delete(any(Item.class));
    }

    @Test
    @Order(22)
    @DisplayName("ItemService_getAllItems")
    void testGetAllItems() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1));

        final List<ItemDto> items = itemService.getAllItems(1L);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Item1", items.get(0).getName());
        verify(itemRepository).findAllByOwnerId(1L);
    }

    @Test
    @Order(23)
    @DisplayName("ItemService_getAllEmptyItems")
    void testGetAllEmptyItems() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of());

        final List<ItemDto> list = itemService.getAllItems(1);
        assertNotNull(list);
        assertTrue(list.isEmpty());
        verify(itemRepository).findAllByOwnerId(1L);
    }

    @Test
    @Order(24)
    @DisplayName("ItemService_getAllItemsUserNotFound")
    void testGetAllItemsUserNotFound() {

        assertThrows(NotFoundException.class, () -> {
            itemService.getAllItems(10L);
        });
    }

    @Test
    @Order(25)
    @DisplayName("ItemService_searchTextEmpty")
    void testSearchTextEmpty() {

        final List<ItemDto> list = itemService.itemSearch("");

        assertTrue(list.isEmpty());
        assertNotNull(list);
    }

    @Test
    @Order(26)
    @DisplayName("ItemService_searchText")
    void testSearchText() {

        when(itemRepository.itemSearch("em")).thenReturn(List.of(item1));

        final List<ItemDto> list = itemService.itemSearch("em");

        assertEquals("Item1", list.getFirst().getName());
    }

    @Test
    @Order(27)
    @DisplayName("ItemService_itemSearchSuccess")
    void testItemSearchSuccess() {

        List<Item> items = new ArrayList<>();
        items.add(item1);

        when(itemRepository.itemSearch("item1")).thenReturn(items);

        itemDto1 = itemMapper.toItemDto(item1);
        List<ItemDto> result = itemService.itemSearch("Item1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Item1", result.get(0).getName());
    }

    @Test
    @Order(28)
    @DisplayName("ItemService_itemSearchNoResults")
    void testItemSearchNoResults() {

        when(itemRepository.itemSearch("no result")).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.itemSearch("no result");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(29)
    @DisplayName("ItemService_addCommentNotUser")
    void testAddCommentNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.addComments(5, 1, commentDto1)
        );
    }

    @Test
    @Order(30)
    @DisplayName("ItemService_addCommentNotItem")
    void testAddCommentNotItem() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        assertThrows(
                NotFoundException.class,
                () -> itemService.addComments(1, 5, commentDto1)
        );
    }

    @Test
    @Order(31)
    @DisplayName("ItemService_noAddCommentWithOutBooking")
    void testNoAddCommentWithOutBooking() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        assertThrows(
                ValidationException.class,
                () -> itemService.addComments(1, 1, commentDto1)
        );
    }

    @Test
    @Order(32)
    @DisplayName("ItemService_addComments")
    void testAddComments() {

        final Booking booking = new Booking();
        booking.setItem(item1);
        booking.setBooker(user1);
        booking.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(7));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyLong(),
                anyLong(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        final CommentDto commentDto = itemService.addComments(1, 1, commentDto1);

        assertEquals("test-text", commentDto.getText());
    }

    @Test
    @Order(33)
    @DisplayName("ItemService_updateItemIfNotValidOwner")
    void testUpdateItemIfNotValidOwner() {
        final User user10 = new User();
        user10.setId(10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(user10));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.itemUpdate(user10.getId(), 1L, itemDto1)
        );

        assertEquals("Редактировать вещь может только владелец.", exception.getMessage());
    }

    @Test
    @Order(34)
    @DisplayName("ItemService_createWithRequestEmpty")
    void testCreateWithRequestEmpty() {

        final ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Item2");
        itemDto2.setDescription("desc2");
        itemDto2.setAvailable(true);
        itemDto2.setRequestId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.save(itemMapper.toItem(user1, itemDto2))).thenReturn(item2);

        assertThrows(
                NotFoundException.class,
                () -> itemService.itemCreate(1, itemDto2)
        );
    }
}


