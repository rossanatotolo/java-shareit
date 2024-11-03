package ru.practicum.shareit.request.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoResponseRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Component
public class ItemRequestDtoOutput {
    private Long id;

    private String description;

    private UserDto requester;

    private LocalDateTime created;

    private List<ItemDtoResponseRequest> items;
}
