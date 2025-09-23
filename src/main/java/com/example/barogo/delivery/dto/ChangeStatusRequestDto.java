package com.example.barogo.delivery.dto;

import com.example.barogo.delivery.domain.DeliveryStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangeStatusRequestDto {
  private Long deliveryId;
  private DeliveryStatus status;
}
