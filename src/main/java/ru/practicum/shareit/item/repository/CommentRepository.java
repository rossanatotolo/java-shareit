package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Component
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(final long itemId);
}
