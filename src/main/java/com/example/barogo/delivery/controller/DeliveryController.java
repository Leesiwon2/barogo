package com.example.barogo.delivery.controller;

import com.example.barogo.delivery.domain.Delivery;
import com.example.barogo.delivery.dto.CreateDeliveryRequestDto;
import com.example.barogo.delivery.dto.DeliveryRequestDto;
import com.example.barogo.delivery.service.DeliveryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/barogo/delivery")
@RequiredArgsConstructor
public class DeliveryController {

  private final DeliveryService deliveryService;

  @PostMapping
  public ResponseEntity<String> createDelivery(@RequestBody CreateDeliveryRequestDto request) {
    deliveryService.createDelivery(request);
    return ResponseEntity.ok("배달 요청 건이 생성되었습니다.");
  }

  @GetMapping
  public List<Delivery> deliveries(
      @RequestParam(name = "start_date") LocalDate startDate,
      @RequestParam(name = "end_date") LocalDate endDate,
      @RequestParam(name = "user_id", required = false) String userId) {
    DeliveryRequestDto request = new DeliveryRequestDto(startDate, endDate, userId);
    return deliveryService.deliveries(request);
  }
}
