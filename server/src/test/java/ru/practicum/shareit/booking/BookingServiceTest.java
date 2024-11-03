package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.validation.BookingTimeValidator;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    private BookingService bookingService;

    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;
    private final BookingTimeValidator validator = new BookingTimeValidator();

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    private User user1;

    private Item item1;

    private Item item2;

    private BookingDtoInput bookingDtoInput1;

    private Booking booking;

    private Booking booking1;

    @BeforeEach
    public void setUp() {

        bookingMapper = new BookingMapper();

        UserMapper userMapper = new UserMapper();

        ItemMapper itemMapper = new ItemMapper();

        bookingService = new BookingServiceImpl(bookingMapper, bookingRepository,
                userRepository, userMapper, itemRepository, itemMapper);

        user1 = new User();
        user1.setName("Name1");
        user1.setEmail("ex1t@yandex.ru");
        user1.setId(1L);

        item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("desc1");
        item1.setOwner(user1);
        item1.setAvailable(true);

        item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("desc2");
        item2.setOwner(user1);
        item2.setAvailable(false);

        bookingDtoInput1 = new BookingDtoInput();
        bookingDtoInput1.setItemId(1L);
        bookingDtoInput1.setStart(LocalDateTime.now().plusDays(2));
        bookingDtoInput1.setEnd(LocalDateTime.now().plusDays(5));

        booking = new Booking();
        booking.setItem(item1);
        booking.setBooker(user1);
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(5));

        booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setBooker(user1);
        booking1.setId(2L);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStart(LocalDateTime.now().minusDays(10));
        booking1.setEnd(LocalDateTime.now().minusDays(5));
    }

    @Test
    @Order(1)
    @DisplayName("BookingService_createNotUser")
    void testCreateNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDtoInput1)
        );
    }

    @Test
    @Order(2)
    @DisplayName("BookingService_createNotItem")
    void testCreateNotItem() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDtoInput1)
        );
    }

    @Test
    @Order(3)
    @DisplayName("BookingService_createNotAvailable")
    void testCreateNotAvailable() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item2));

        assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1L, bookingDtoInput1)
        );
    }

    @Test
    @Order(4)
    @DisplayName("BookingService_createNotGoodTime")
    void testCreateNotGoodTime() {

        final BookingDtoInput bookingDtoInput2;
        bookingDtoInput2 = new BookingDtoInput();
        bookingDtoInput2.setItemId(1L);
        bookingDtoInput2.setStart(LocalDateTime.now().plusDays(7));
        bookingDtoInput2.setEnd(LocalDateTime.now().plusDays(5));

        Booking b = bookingMapper.toBooking(bookingDtoInput2, user1, item1);
        assertFalse(validator.isValid(b, null));
    }

    @Test
    @Order(5)
    @DisplayName("BookingService_createEqualsTime")
    void testCreateEqualsTime() {

        final LocalDateTime time = LocalDateTime.now().plusDays(5);

        final BookingDtoInput bookingDtoInput2;
        bookingDtoInput2 = new BookingDtoInput();
        bookingDtoInput2.setItemId(1L);
        bookingDtoInput2.setStart(time);
        bookingDtoInput2.setEnd(time);

        Booking b = bookingMapper.toBooking(bookingDtoInput2, user1, item1);
        assertFalse(validator.isValid(b, null));
    }

    @Test
    @Order(6)
    @DisplayName("BookingService_create")
    void testCreate() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(bookingRepository.save(bookingMapper.toBooking(bookingDtoInput1, user1, item1))).thenReturn(booking);

        final BookingDtoOutput bookingDtoOutput = bookingService.createBooking(1L, bookingDtoInput1);

        assertEquals("Name1", bookingDtoOutput.getBooker().getName());
        assertEquals("Item1", bookingDtoOutput.getItem().getName());
        assertEquals(BookingStatus.WAITING, bookingDtoOutput.getStatus());
    }

    @Test
    @Order(7)
    @DisplayName("BookingService_confirmationNotBooking")
    void testConfirmationNotBooking() {

        assertThrows(
                ValidationException.class,
                () -> bookingService.confirmationBooking(1L, 1L, true)
        );
    }

    @Test
    @Order(8)
    @DisplayName("BookingService_confirmationStatusNotWaiting")
    void testConfirmationStatusNotWaiting() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(bookingRepository.findByIdAndOwnerId(2L, 1L)).thenReturn(Optional.of(booking2));

        assertThrows(
                ValidationException.class,
                () -> bookingService.confirmationBooking(1L, 2L, true)
        );
    }

    @Test
    @Order(9)
    @DisplayName("BookingService_confirmationTrue")
    void testConfirmationTrue() {

        when(bookingRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        final BookingDtoOutput bookingDtoOutput = bookingService.confirmationBooking(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, bookingDtoOutput.getStatus());
        assertEquals("Name1", bookingDtoOutput.getBooker().getName());
        assertEquals("Item1", bookingDtoOutput.getItem().getName());
    }

    @Test
    @Order(10)
    @DisplayName("BookingService_confirmationFalse")
    void testConfirmationFalse() {

        when(bookingRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        final BookingDtoOutput bookingDtoOutput = bookingService.confirmationBooking(1L, 1L, false);

        assertEquals(BookingStatus.REJECTED, bookingDtoOutput.getStatus());
        assertEquals("Name1", bookingDtoOutput.getBooker().getName());
        assertEquals("Item1", bookingDtoOutput.getItem().getName());
    }

    @Test
    @Order(11)
    @DisplayName("BookingService_getByIdNotBooking")
    void testGetByIdNotBooking() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L)
        );
    }

    @Test
    @Order(12)
    @DisplayName("BookingService_getByIdUserNotOwner")
    void testGetByIdUserNotOwner() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(
                ValidationException.class,
                () -> bookingService.getBookingById(2L, 1L)
        );
    }

    @Test
    @Order(13)
    @DisplayName("BookingService_getBookingById")
    void testGetBookingById() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        final BookingDtoOutput bookingDtoOutput = bookingService.getBookingById(1L, 1L);

        assertEquals("Name1", bookingDtoOutput.getBooker().getName());
        assertEquals("Item1", bookingDtoOutput.getItem().getName());
    }

    @Test
    @Order(14)
    @DisplayName("BookingService_getAllByUserIdNotUser")
    void testGetAllByUserIdNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookingsFromUser(1L, State.ALL)
        );
    }

    @Test
    @Order(15)
    @DisplayName("BookingService_getAllByUserIdAll")
    void testGetAllByUserIdAll() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerId(1L, sort)).thenReturn(List.of(booking, booking1));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromUser(1L, State.ALL);

        assertEquals(2, bookingDtoOutputs.size());
    }

    @Test
    @Order(16)
    @DisplayName("BookingService_getAllByUserIdCurrent")
    void testGetAllByUserIdCurrent() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking2));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromUser(1L, State.CURRENT);

        assertEquals(1, bookingDtoOutputs.size());
        assertTrue(bookingDtoOutputs.iterator().next().getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookingDtoOutputs.iterator().next().getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @Order(17)
    @DisplayName("BookingService_getAllByUserIdPast")
    void testGetAllByUserIdPast() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking1));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromUser(1L, State.PAST);

        assertEquals(1, bookingDtoOutputs.size());
        assertTrue(bookingDtoOutputs.iterator().next().getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @Order(18)
    @DisplayName("BookingService_getAllByUserIdFuture")
    void testGetAllByUserIdFuture() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromUser(1L, State.FUTURE);

        assertEquals(1, bookingDtoOutputs.size());
        assertTrue(bookingDtoOutputs.iterator().next().getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    @Order(19)
    @DisplayName("BookingService_getAllByUserIdWaiting")
    void testGetAllByUserIdWaiting() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndStatusIs(1, BookingStatus.WAITING, sort))
                .thenReturn(List.of(booking));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromUser(1L, State.WAITING);

        assertEquals(1, bookingDtoOutputs.size());
        assertEquals(BookingStatus.WAITING, bookingDtoOutputs.iterator().next().getStatus());
    }

    @Test
    @Order(20)
    @DisplayName("BookingService_getAllByUserIdRejected")
    void testGetAllByUserIdRejected() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.REJECTED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndStatusIs(1, BookingStatus.REJECTED, sort))
                .thenReturn(List.of(booking2));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromUser(1L, State.REJECTED);

        assertEquals(1, bookingDtoOutputs.size());
        assertEquals(BookingStatus.REJECTED, bookingDtoOutputs.iterator().next().getStatus());
    }

    @Test
    @Order(21)
    @DisplayName("BookingService_getAllByOwnerIdNotUser")
    void testGetAllByOwnerIdNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookingsFromOwner(1L, State.ALL)
        );
    }

    @Test
    @Order(22)
    @DisplayName("BookingService_getAllByOwnerIdNotItem")
    void testGetAllByOwnerIdNotItem() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsFromOwner(1L, State.ALL)
        );
    }

    @Test
    @Order(23)
    @DisplayName("BookingService_getAllByOwnerIdAll")
    void testGetAllByOwnerIdAll() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerId(1, sort)).thenReturn(List.of(booking, booking1));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromOwner(1L, State.ALL);

        assertEquals(2, bookingDtoOutputs.size());
    }

    @Test
    @Order(24)
    @DisplayName("BookingService_getAllByOwnerIdCurrent")
    void testGetAllByOwnerIdCurrent() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking2));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromOwner(1L, State.CURRENT);

        assertEquals(1, bookingDtoOutputs.size());
        assertTrue(bookingDtoOutputs.iterator().next().getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookingDtoOutputs.iterator().next().getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @Order(25)
    @DisplayName("BookingService_getAllByOwnerIdPast")
    void testGetAllByOwnerIdPast() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking1));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromOwner(1L, State.PAST);

        assertEquals(1, bookingDtoOutputs.size());
        assertTrue(bookingDtoOutputs.iterator().next().getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @Order(26)
    @DisplayName("BookingService_getAllByOwnerIdFuture")
    void testGetAllByOwnerIdFuture() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromOwner(1L, State.FUTURE);

        assertEquals(1, bookingDtoOutputs.size());
        assertTrue(bookingDtoOutputs.iterator().next().getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    @Order(27)
    @DisplayName("BookingService_getAllByOwnerIdWaiting")
    void testGetAllByOwnerIdWaiting() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(1, BookingStatus.WAITING, sort))
                .thenReturn(List.of(booking));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromOwner(1L, State.WAITING);

        assertEquals(1, bookingDtoOutputs.size());
        assertEquals(BookingStatus.WAITING, bookingDtoOutputs.iterator().next().getStatus());
    }

    @Test
    @Order(28)
    @DisplayName("BookingService_getAllByOwnerIdRejected")
    void testGetAllByOwnerIdRejected() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.REJECTED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(1L, BookingStatus.REJECTED, sort))
                .thenReturn(List.of(booking2));

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromOwner(1L, State.REJECTED);

        assertEquals(1, bookingDtoOutputs.size());
        assertEquals(BookingStatus.REJECTED, bookingDtoOutputs.iterator().next().getStatus());
    }
}
