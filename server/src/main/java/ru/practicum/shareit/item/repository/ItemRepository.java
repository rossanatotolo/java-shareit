package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(final long userId);

    @Query("""
            SELECT i
            FROM Item i
            WHERE i.available = true
                AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%'))
                OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))
            """)
    List<Item> itemSearch(final String text);

    List<Item> findAllByRequest(final ItemRequest request);
}
