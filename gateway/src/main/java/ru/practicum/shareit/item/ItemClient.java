package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> itemCreate(final long userId, final ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> itemUpdate(final long userId, final long itemId, final ItemDto itemDto) {
        return patch("/" + userId, itemId, itemDto);
    }

    public ResponseEntity<Object> getItemById(final long userId, final long itemId) {
        return get("/" + itemId, userId);
    }

    public void itemDelete(final long itemId) {
        delete("/" + itemId);
    }

    public ResponseEntity<Object> getAllItems(final long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> itemSearch(final long userId, final String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> addComments(final long userId, final long itemId, final CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
