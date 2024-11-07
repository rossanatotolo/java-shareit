package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("CommentRepository_empty")
    public void testEmpty() {

        final List<Comment> comments = commentRepository.findAll();

        assertTrue(comments.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("CommentRepository_findAllByItemId_ThereAreComments")
    public void testFindAllByItemId_ThereAreComments() {

        final User user = new User();
        user.setName("Name");
        user.setEmail("ex@yandex.ru");
        userRepository.save(user);

        final User user1 = new User();
        user1.setName("Name1");
        user1.setEmail("ex1@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Name2");
        user2.setEmail("ex2@yandex.ru");
        userRepository.save(user2);

        final Item item = new Item();
        item.setName("Item");
        item.setDescription("desc");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        final Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("desc1");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("desc2");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Item item3 = new Item();
        item3.setName("Item3");
        item3.setDescription("desc3");
        item3.setOwner(user1);
        item3.setAvailable(true);
        itemRepository.save(item3);

        final Comment comment = new Comment();
        comment.setText("text-test");
        comment.setItem(item);
        comment.setAuthor(user1);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        final Comment comment1 = new Comment();
        comment1.setText("text-test1");
        comment1.setItem(item1);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        final Comment comment2 = new Comment();
        comment2.setText("text-test2");
        comment2.setItem(item1);
        comment2.setAuthor(user2);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);

        final List<Comment> comments = commentRepository.findAllByItemId(2);

        assertEquals(2, comments.size());
    }

    @Test
    @DirtiesContext
    @DisplayName("CommentRepository_findAllByItemId_ThereAreNotComments")
    public void testFindAllByItemId_ThereAreNotComments() {

        final User user = new User();
        user.setName("Name");
        user.setEmail("ex@yandex.ru");
        userRepository.save(user);

        final User user1 = new User();
        user1.setName("Name1");
        user1.setEmail("ex1@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Name2");
        user2.setEmail("ex2@yandex.ru");
        userRepository.save(user2);

        final Item item = new Item();
        item.setName("Item");
        item.setDescription("desc");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        final Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("desc1");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("desc2");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Item item3 = new Item();
        item3.setName("Item3");
        item3.setDescription("desc3");
        item3.setOwner(user1);
        item3.setAvailable(true);
        itemRepository.save(item3);

        final Comment comment = new Comment();
        comment.setText("text-test");
        comment.setItem(item);
        comment.setAuthor(user1);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        final Comment comment1 = new Comment();
        comment1.setText("text-test1");
        comment1.setItem(item1);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        final Comment comment2 = new Comment();
        comment2.setText("text-test2");
        comment2.setItem(item1);
        comment2.setAuthor(user2);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);

        final List<Comment> comments = commentRepository.findAllByItemId(3);

        assertTrue(comments.isEmpty());
    }
}
