package com.example.barogo.delivery.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.barogo.delivery.domain.Delivery;
import com.example.barogo.delivery.domain.DeliveryStatus;
import com.example.barogo.delivery.dto.CreateDeliveryRequestDto;
import com.example.barogo.delivery.dto.DeliveryItemRequestDto;
import com.example.barogo.delivery.dto.DeliveryRequestDto;
import com.example.barogo.delivery.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class DeliveryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private DeliveryService deliveryService;

  @Test
  void createDelivery() throws Exception {
    CreateDeliveryRequestDto request = new CreateDeliveryRequestDto(
        "user1",
        "서울",
        "부산",
        List.of(new DeliveryItemRequestDto("물", 5, BigDecimal.valueOf(5000)))
    );

    mockMvc.perform(post("/barogo/delivery")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("배달 요청 건이 생성되었습니다."));
  }


  @Test
  void getDeliveries() throws Exception {
    // given
    Delivery delivery = Delivery.builder()
        .idx(1L)
        .userId("user1")
        .departureLocation("서울")
        .arrivalLocation("부산")
        .status(DeliveryStatus.PENDING)
        .createdAt(LocalDateTime.now())
        .modifiedAt(LocalDateTime.now())
        .build();

    Mockito.when(deliveryService.deliveries(any(DeliveryRequestDto.class)))
        .thenReturn(List.of(delivery));

    // when & then
    mockMvc.perform(get("/barogo/delivery")
            .param("start_date", LocalDate.now().minusDays(1).toString())
            .param("end_date", LocalDate.now().toString())
            .param("user_id", "user1")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").value("user1"))
        .andExpect(jsonPath("$[0].departureLocation").value("서울"))
        .andExpect(jsonPath("$[0].arrivalLocation").value("부산"))
        .andExpect(jsonPath("$[0].status").value("PENDING"));
  }
}
