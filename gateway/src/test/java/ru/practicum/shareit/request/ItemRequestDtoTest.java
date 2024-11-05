package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    private ObjectMapper objectMapper;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestDto_serializeJson")
    public void serializeJsonTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Desc");

        String jsonContent = objectMapper.writeValueAsString(itemRequestDto);

        assertThat(jsonContent).contains("\"description\":\"Desc\"");
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestDto_validation")
    public void validationTest() {
        ItemRequestDto validRequest = new ItemRequestDto();
        validRequest.setDescription("De sc");

        ItemRequestDto invalidRequest = new ItemRequestDto();
        invalidRequest.setDescription("");

        var validConstraints = validator.validate(validRequest);
        var invalidConstraints = validator.validate(invalidRequest);

        assertThat(validConstraints).isEmpty();
        assertThat(invalidConstraints).isNotEmpty();
    }
}