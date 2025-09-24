package com.example.barogo.delivery.dto;

import com.example.barogo.delivery.domain.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequestDto {
  private Long deliveryId;
  private DeliveryStatus status;
}
