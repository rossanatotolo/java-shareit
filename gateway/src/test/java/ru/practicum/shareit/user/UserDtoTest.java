package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoTest {

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("UserDto_serializeJson")
    void serializeJsonTest() throws Exception {

        final UserDto userDto = new UserDto();
        userDto.setName("Katia");
        userDto.setEmail("gromgrommolnia@yandex.ru");

        String json = objectMapper.writeValueAsString(userDto);
        assertThat(json).contains("\"name\":\"Katia\"", "\"email\":\"gromgrommolnia@yandex.ru\"");
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("UserDto_deserializeJson")
    void deserializeJsonTest() throws Exception {

        final String json = "{\"id\":1,\"name\":\"Katia\",\"email\":\"gromgrommolnia@yandex.ru\"}";

        final UserDto userDto = objectMapper.readValue(json, UserDto.class);
        assertThat(userDto.getName()).isEqualTo("Katia");
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("UserDto_validation")
    void validationTest() {

        final UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Katia");
        userDto.setEmail("gromgrommolnia@yandex.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("UserDto_invalid")
    public void invalidTest() {

        final UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("");
        userDto.setEmail("grom");

        var invalidConstraints = validator.validate(userDto);

        assertThat(invalidConstraints).isNotEmpty();
    }
}
