package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toComment(CommentDto commentDto, User user, Item item) {

        final Comment comment = new Comment();

        comment.setAuthor(user);
        comment.setItem(item);
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());

        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {

        final CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }
}