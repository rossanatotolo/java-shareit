package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long currentId = 0;

    @Override
    public List<Item> getAllItems(final long userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .toList();
    }

    @Override
    public Optional<Item> getItemById(final long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item itemCreate(final Item item) {
        item.setId(getIdNext());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item itemUpdate(final long itemId, final Item item) {
        items.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> itemSearch(final String text) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    @Override
    public void itemDelete(final Long itemId) {
        items.remove(itemId);
    }

    private long getIdNext() {
        return ++currentId;
    }
}
