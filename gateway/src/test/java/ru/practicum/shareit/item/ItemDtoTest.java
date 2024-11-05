package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
public class ItemDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DirtiesContext
    @DisplayName("ItemDto_serializeJson")
    void serializeJsonTest() throws Exception {

        final ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        String json = objectMapper.writeValueAsString(itemDto);
        assertThat(json).contains("\"name\":\"name\"");
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemDto_deserializeJson")
    void deserializeJsonTest() throws Exception {

        String json = "{\"id\":1,\"name\":\"Test\",\"description\":\"desc\",\"available\":true,\"requestId\":1}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);
        assertThat(itemDto.getName()).isEqualTo("Test");
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemDto_validation")
    void validationTest() {

        final ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description1");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isEmpty();
    }
}
