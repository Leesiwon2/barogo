package com.example.barogo.delivery.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateDeliveryRequestDto {
  private String userId;
  private String departureLocation;
  private String arrivalLocation;
  private List<DeliveryItemRequestDto> items;

}
