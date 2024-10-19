package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") @Positive final long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOutput getItemById(@RequestHeader("X-Sharer-User-Id") final Integer userId, @PathVariable @Positive final long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto itemCreate(@RequestHeader("X-Sharer-User-Id") @Positive final long userId,
                              @Valid @RequestBody final ItemDto itemDto) {
        return itemService.itemCreate(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto itemUpdate(@RequestHeader("X-Sharer-User-Id") @Positive final long userId,
                              @PathVariable @Positive final long itemId,
                              @RequestBody final ItemDto itemDto) {
        return itemService.itemUpdate(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> itemSearch(@RequestParam(required = false) final String text) {
        return itemService.itemSearch(text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void itemDelete(@PathVariable @Positive final Long itemId) {
        itemService.itemDelete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComments(@RequestHeader("X-Sharer-User-Id") final long userId,
                                  @PathVariable final long itemId,
                                  @Valid @RequestBody final CommentDto commentDto) {
        return itemService.addComments(userId, itemId, commentDto);
    }
}
