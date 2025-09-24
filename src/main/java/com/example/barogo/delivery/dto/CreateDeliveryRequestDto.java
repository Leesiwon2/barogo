package com.example.barogo.delivery.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRequestDto {
  private String userId;
  private String departureLocation;
  private String arrivalLocation;
  private List<DeliveryItemRequestDto> items;

}
