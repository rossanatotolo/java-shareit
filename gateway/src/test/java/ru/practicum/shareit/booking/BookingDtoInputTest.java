package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
public class BookingDtoInputTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DirtiesContext
    @DisplayName("BookingDtoInput_serializeJson")
    void serializeJsonTest() throws Exception {

        final BookingDtoInput bookingDtoInput = new BookingDtoInput();
        bookingDtoInput.setItemId(1L);
        bookingDtoInput.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(3));

        String json = objectMapper.writeValueAsString(bookingDtoInput);
        assertThat(json).contains("\"itemId\":1");
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingDtoInput_deserializeJson")
    void deserializeJsonTest() throws Exception {

        String json = "{\"itemId\":1,\"start\":\"2020-02-18T10:00:00\",\"end\":\"2020-02-19T10:00:00\"}";

        BookingDtoInput bookingDtoInput = objectMapper.readValue(json, BookingDtoInput.class);
        assertThat(bookingDtoInput.getItemId()).isEqualTo(1);
    }
}
