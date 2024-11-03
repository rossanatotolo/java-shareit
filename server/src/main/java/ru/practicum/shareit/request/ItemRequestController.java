package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDtoOutput itemRequestCreate(@RequestHeader("X-Sharer-User-Id") final long userId,
                                                  @RequestBody final ItemRequestDto itemRequestDto) {
        return itemRequestService.itemRequestCreate(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> getAllRequestByUser(@RequestHeader("X-Sharer-User-Id") final long userId) {
        return itemRequestService.getAllRequestByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> getAllRequests(@RequestHeader("X-Sharer-User-Id") final long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOutput getRequestById(@PathVariable final long requestId) {
        return itemRequestService.getRequestById(requestId);
    }
}
