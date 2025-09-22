package com.example.barogo.delivery.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryItemRequestDto {
  private String name;
  private int amount;
  private BigDecimal price;
}