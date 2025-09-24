package com.example.barogo.delivery.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.barogo.delivery.domain.Delivery;
import com.example.barogo.delivery.domain.DeliveryStatus;
import com.example.barogo.delivery.dto.ChangeStatusRequestDto;
import com.example.barogo.delivery.dto.CreateDeliveryRequestDto;
import com.example.barogo.delivery.dto.DeliveryItemRequestDto;
import com.example.barogo.delivery.dto.DeliveryRequestDto;
import com.example.barogo.delivery.dto.UpdateArrivalLocationRequestDto;
import com.example.barogo.delivery.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class DeliveryControllerRestDocsTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private DeliveryService deliveryService;

  @Test
  void createDelivery() throws Exception {
    CreateDeliveryRequestDto request = new CreateDeliveryRequestDto(
        "user1", "서울", "부산",
        List.of(new DeliveryItemRequestDto("물", 5, BigDecimal.valueOf(5000)))
    );

    mockMvc.perform(post("/barogo/delivery")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("create-delivery",
            requestFields(
                fieldWithPath("user_id").description("사용자 ID"),
                fieldWithPath("departure_location").description("출발지"),
                fieldWithPath("arrival_location").description("도착지"),
                fieldWithPath("items[].name").description("상품명"),
                fieldWithPath("items[].price").description("상품 가격"),
                fieldWithPath("items[].amount").description("상품 수량")
            )
        ));
  }

  @Test
  void getDeliveries() throws Exception {
    Delivery delivery = Delivery.builder()
        .idx(1L).userId("user1").departureLocation("서울").arrivalLocation("부산")
        .status(DeliveryStatus.PENDING).createdAt(LocalDateTime.now()).modifiedAt(LocalDateTime.now())
        .build();

    Mockito.when(deliveryService.deliveries(any(DeliveryRequestDto.class)))
        .thenReturn(List.of(delivery));

    mockMvc.perform(get("/barogo/delivery")
            .param("start_date", LocalDate.now().minusDays(1).toString())
            .param("end_date", LocalDate.now().toString())
            .param("user_id", "user1")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("get-deliveries",
            responseFields(
                fieldWithPath("[].id").description("배달 ID"),
                fieldWithPath("[].status").description("배달 상태"),
                fieldWithPath("[].created_at").description("생성 시각"),
                fieldWithPath("[].modified_at").description("수정 시각"),
                fieldWithPath("[].userId").description("사용자 ID"),
                fieldWithPath("[].departureLocation").description("출발지"),
                fieldWithPath("[].arrivalLocation").description("도착지")
            )
        ));
  }

  @Test
  void updateArrivalLocation() throws Exception {
    UpdateArrivalLocationRequestDto request = new UpdateArrivalLocationRequestDto(1L, "인천");

    mockMvc.perform(patch("/barogo/delivery/arrival-location")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("delivery-update-arrival",
            requestFields(
                fieldWithPath("delivery_id").type(JsonFieldType.NUMBER).description("배달 ID"),
                fieldWithPath("new_arrival_location").type(JsonFieldType.STRING).description("변경할 도착지")
            )
        ));
  }

  @Test
  void changeStatus() throws Exception {
    ChangeStatusRequestDto request = new ChangeStatusRequestDto(1L, DeliveryStatus.ASSIGNED);

    mockMvc.perform(patch("/barogo/delivery/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("delivery-change-status",
            requestFields(
                fieldWithPath("delivery_id").type(JsonFieldType.NUMBER).description("배달 ID"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("변경할 상태")
            )
        ));
  }
}
