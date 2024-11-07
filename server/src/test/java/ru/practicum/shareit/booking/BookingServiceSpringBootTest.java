package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingServiceSpringBootTest {

    private final BookingService bookingService;

    private final ItemService itemService;

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

        final UserDto userDto3 = new UserDto();
        userDto3.setName("Name3");
        userDto3.setEmail("example3@mail.ru");
        userService.userCreate(userDto3);

        final ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Item1");
        itemDto1.setDescription("desc1");
        itemDto1.setAvailable(true);
        itemService.itemCreate(1L, itemDto1);

        final ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Item2");
        itemDto2.setDescription("desc2");
        itemDto2.setAvailable(true);
        itemService.itemCreate(1L, itemDto2);

        final ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("Item3");
        itemDto3.setDescription("desc3");
        itemDto3.setAvailable(true);
        itemService.itemCreate(2L, itemDto3);

        final BookingDtoInput bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(2L);
        bookingDtoInput.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(3));
        bookingService.createBooking(2L, bookingDtoInput);

        final BookingDtoInput bookingDtoInput2 = new BookingDtoInput();
        bookingDtoInput2.setItemId(3L);
        bookingDtoInput2.setStart(LocalDateTime.now().plusDays(10));
        bookingDtoInput2.setEnd(LocalDateTime.now().plusDays(13));
        bookingService.createBooking(2L, bookingDtoInput2);
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_createBooking")
    void testCreateBooking() {
        final BookingDtoInput bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(1L);
        bookingDtoInput.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(3));

        final BookingDtoOutput bookingDtoOutput = bookingService.createBooking(1L, bookingDtoInput);

        assertEquals("Item1", bookingDtoOutput.getItem().getName());
        assertEquals("Name1", bookingDtoOutput.getBooker().getName());
        assertEquals(BookingStatus.WAITING, bookingDtoOutput.getStatus());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_confirmationBooking")
    void testConfirmationBooking() {

        final BookingDtoOutput bookingDtoOutput = bookingService.confirmationBooking(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, bookingDtoOutput.getStatus());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_getBookingById")
    void testGetBookingById() {

        final BookingDtoOutput bookingDtoOutput1 = bookingService.getBookingById(1L, 1L);

        final BookingDtoOutput bookingDtoOutput2 = bookingService.getBookingById(2L, 1L);

        assertEquals(bookingDtoOutput1, bookingDtoOutput2);
        assertEquals("Item2", bookingDtoOutput1.getItem().getName());
        assertEquals("Item2", bookingDtoOutput2.getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_getAllBookingsFromUser")
    void testGetAllBookingsFromUser() {

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromUser(2L, State.ALL);

        assertEquals(2, bookingDtoOutputs.size());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_getAllBookingsFromOwner")
    void testGetAllBookingsFromOwner() {

        final Collection<BookingDtoOutput> bookingDtoOutputs = bookingService.getAllBookingsFromOwner(1L, State.ALL);

        assertEquals(1, bookingDtoOutputs.size());
    }
}
