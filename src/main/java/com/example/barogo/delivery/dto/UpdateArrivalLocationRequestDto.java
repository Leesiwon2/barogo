package com.example.barogo.delivery.dto;

import lombok.Data;

@Data
public class UpdateArrivalLocationRequestDto {
  private Long deliveryId;
  private String newArrivalLocation;
}
