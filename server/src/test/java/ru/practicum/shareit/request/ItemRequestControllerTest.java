package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestController_itemRequestCreate")
    void testItemRequestCreate() throws Exception {

        final ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("description111");

        final ItemRequestDtoOutput itemRequestDtoOutput = new ItemRequestDtoOutput();
        itemRequestDtoOutput.setId(1L);
        itemRequestDtoOutput.setDescription("description111");
        itemRequestDtoOutput.setCreated(LocalDateTime.now());

        when(itemRequestService.itemRequestCreate(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDtoOutput);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content("{\"description\": \"description111\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("description111"));

        verify(itemRequestService, times(1))
                .itemRequestCreate(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestController_getAllRequestByUser")
    void testGetAllRequestByUser() throws Exception {

        final List<ItemRequestDtoOutput> requests = List.of(new ItemRequestDtoOutput());

        when(itemRequestService.getAllRequestByUser(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestService, times(1)).getAllRequestByUser(anyLong());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestController_getAllRequests")
    void testGetAllRequests() throws Exception {

        final List<ItemRequestDtoOutput> itemRequestDtoOutputs = List.of(new ItemRequestDtoOutput());

        when(itemRequestService.getAllRequests(anyLong())).thenReturn(itemRequestDtoOutputs);

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestService, times(1)).getAllRequests(anyLong());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestController_getRequestById")
    void testGetRequestById() throws Exception {

        final ItemRequestDtoOutput request = new ItemRequestDtoOutput(); // создайте DTO

        when(itemRequestService.getRequestById(anyLong())).thenReturn(request);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()));

        verify(itemRequestService, times(1)).getRequestById(anyLong());
    }
}