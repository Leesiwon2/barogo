package com.example.barogo.delivery.service;

import com.example.barogo.common.error.BadRequestException;
import com.example.barogo.delivery.domain.Delivery;
import com.example.barogo.delivery.domain.DeliveryItem;
import com.example.barogo.delivery.domain.DeliveryStatus;
import com.example.barogo.delivery.dto.CreateDeliveryRequestDto;
import com.example.barogo.delivery.dto.DeliveryRequestDto;
import com.example.barogo.delivery.dto.UpdateArrivalLocationRequestDto;
import com.example.barogo.delivery.repository.DeliveryRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;

  @Transactional
  public void createDelivery(CreateDeliveryRequestDto request) {
    Delivery delivery = Delivery.builder()
        .userId(request.getUserId())
        .departureLocation(request.getDepartureLocation())
        .arrivalLocation(request.getArrivalLocation())
        .createdAt(LocalDateTime.now())
        .status(DeliveryStatus.PENDING).build();

    request.getItems().forEach(dto -> {
      DeliveryItem item = DeliveryItem.create(dto.getName(), dto.getAmount(), dto.getPrice());
      delivery.addItem(item);
    });
    deliveryRepository.save(delivery);
  }

  @Transactional
  public List<Delivery> deliveries(DeliveryRequestDto request) {
    if (request.getStartDate() == null || request.getEndDate() == null) {
      throw new IllegalArgumentException("startDate와 endDate는 필수값입니다.");
    }

    Period period = Period.between(request.getStartDate(), request.getEndDate());
    if (period.getDays() > 3) {
      throw new BadRequestException("조회 기간은 최대 3일까지만 가능합니다.");
    }
      return deliveryRepository.searchDeliveries(request);
  }


  @Transactional
  public void updateArrivalLocation(UpdateArrivalLocationRequestDto request) {
    Delivery delivery = deliveryRepository.findById(request.getDeliveryId())
        .orElseThrow(() -> new BadRequestException("해당 배달을 찾을 수 없습니다."));

    delivery.changeArrivalLocation(request.getNewArrivalLocation());
  }
}
