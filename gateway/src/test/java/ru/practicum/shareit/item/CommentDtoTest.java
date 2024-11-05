package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.CommentDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

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
    @DisplayName("CommentDtoTest_serializeJson")
    public void serializeJsonTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("test-text");

        String jsonContent = objectMapper.writeValueAsString(commentDto);

        assertThat(jsonContent).contains("\"text\":\"test-text\"");
    }

    @Test
    @DirtiesContext
    @DisplayName("CommentDtoTest_validation")
    public void validationTest() {
        CommentDto validRequest = new CommentDto();
        validRequest.setText("test-text");

        CommentDto invalidRequest = new CommentDto();
        invalidRequest.setText("");

        var validConstraints = validator.validate(validRequest);
        var invalidConstraints = validator.validate(invalidRequest);

        assertThat(validConstraints).isEmpty();
        assertThat(invalidConstraints).isNotEmpty();
    }
}
