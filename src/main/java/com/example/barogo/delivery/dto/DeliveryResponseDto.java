package com.example.barogo.delivery.dto;

import com.example.barogo.delivery.domain.Delivery;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryResponseDto {
  private Long id;

  @JsonProperty("userId")
  private String userId;

  @JsonProperty("departureLocation")
  private String departureLocation;

  @JsonProperty("arrivalLocation")
  private String arrivalLocation;

  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  public static DeliveryResponseDto fromEntity(Delivery delivery) {
    return new DeliveryResponseDto(
        delivery.getIdx(),
        delivery.getUserId(),
        delivery.getDepartureLocation(),
        delivery.getArrivalLocation(),
        delivery.getStatus().name(),
        delivery.getCreatedAt(),
        delivery.getModifiedAt()
    );
  }
}

