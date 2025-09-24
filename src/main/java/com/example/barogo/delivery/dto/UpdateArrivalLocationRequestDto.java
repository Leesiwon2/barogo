package com.example.barogo.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateArrivalLocationRequestDto {
  private Long deliveryId;
  private String newArrivalLocation;
}
