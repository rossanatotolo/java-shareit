package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") @Positive final long userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") final long userId, @PathVariable @Positive final long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> itemCreate(@RequestHeader("X-Sharer-User-Id") @Positive final long userId,
                                             @Valid @RequestBody final ItemDto itemDto) {
        return itemClient.itemCreate(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> itemUpdate(@RequestHeader("X-Sharer-User-Id") @Positive final long userId,
                                             @PathVariable @Positive final long itemId, @RequestBody final ItemDto itemDto) {
        return itemClient.itemUpdate(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> itemSearch(@RequestHeader("X-Sharer-User-Id") @Positive final long userId,
                                             @RequestParam(required = false) final String text) {
        return itemClient.itemSearch(userId, text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void itemDelete(@PathVariable @Positive final Long itemId) {
        itemClient.itemDelete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComments(@RequestHeader("X-Sharer-User-Id") final long userId,
                                              @PathVariable final long itemId, @Valid @RequestBody final CommentDto commentDto) {
        return itemClient.addComments(userId, itemId, commentDto);
    }
}
