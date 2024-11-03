package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("ItemController_itemCreate")
    void testItemCreate() throws Exception {

        final ItemDto itemDto = new ItemDto();

        when(itemService.itemCreate(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content("{\"name\": \"name1\", \"description\": \"desc1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).itemCreate(anyLong(), any(ItemDto.class));
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("ItemController_itemUpdate")
    void testItemUpdate() throws Exception {

        final ItemDto itemDto = new ItemDto();

        when(itemService.itemUpdate(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .content("{\"name\": \"Updated item\", \"description\": \"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).itemUpdate(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("ItemController_getItemById")
    void testGetItemById() throws Exception {

        final ItemDtoOutput itemDtoOutput = new ItemDtoOutput();

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoOutput);

        mockMvc.perform(get("/items/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoOutput.getId()));

        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("ItemController_getAllItems")
    void testGetAllItems() throws Exception {

        final List<ItemDto> items = List.of(new ItemDto());

        when(itemService.getAllItems(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemService, times(1)).getAllItems(anyLong());
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("ItemController_itemSearch")
    void testItemSearch() throws Exception {

        final List<ItemDto> items = List.of(new ItemDto());

        when(itemService.itemSearch(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemService, times(1)).itemSearch(anyString());
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("ItemController_addComments")
    void testAddComments() throws Exception {

        final CommentDto commentDto = new CommentDto();
        commentDto.setText("Test-text");

        when(itemService.addComments(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content("{\"text\": \"Test-text\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test-text"));

        verify(itemService, times(1)).addComments(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("ItemController_itemDelete")
    void testItemDelete() throws Exception {

        mockMvc.perform(delete("/items/1")
                        .header(HEADER, 1))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).itemDelete(1L);
    }
}

