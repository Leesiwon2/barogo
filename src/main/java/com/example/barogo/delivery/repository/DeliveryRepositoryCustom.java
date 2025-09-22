package com.example.barogo.delivery.repository;

import com.example.barogo.delivery.domain.Delivery;
import com.example.barogo.delivery.dto.DeliveryRequestDto;
import java.util.List;

public interface DeliveryRepositoryCustom {
  List<Delivery> searchDeliveries(DeliveryRequestDto request);
}
