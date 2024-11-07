package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDtoOutput bookingDtoOutput;

    private BookingDtoInput bookingDtoInput;

    private static final String HEADER = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {

        bookingDtoOutput = new BookingDtoOutput();
        bookingDtoOutput.setId(1L);
        bookingDtoOutput.setStatus(BookingStatus.WAITING);

        bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(1L);
        bookingDtoInput.setStart(LocalDateTime.now());
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("BookingController_createBooking")
    public void testCreateBooking() throws Exception {

        when(bookingService.createBooking(anyLong(), any(BookingDtoInput.class)))
                .thenReturn(bookingDtoOutput);

        final String bookingRequestJson = objectMapper.writeValueAsString(bookingDtoInput);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequestJson)
                        .header(HEADER, 1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("BookingController_confirmationBooking")
    public void testConfirmationBooking() throws Exception {

        bookingDtoOutput.setStatus(BookingStatus.APPROVED);

        when(bookingService.confirmationBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoOutput);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("BookingController_getBookingById")
    public void testGetBookingById() throws Exception {

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDtoOutput);

        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("BookingController_getAllBookingsFromUser")
    public void testGetAllBookingsFromUser() throws Exception {

        when(bookingService.getAllBookingsFromUser(anyLong(), any(State.class)))
                .thenReturn(Collections.singletonList(bookingDtoOutput));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("BookingController_getAllBookingsFromOwner")
    public void testGetAllBookingsFromOwner() throws Exception {

        when(bookingService.getAllBookingsFromOwner(anyLong(), any(State.class)))
                .thenReturn(Collections.singletonList(bookingDtoOutput));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}