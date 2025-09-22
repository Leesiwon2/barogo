package com.example.barogo.delivery.domain;

public enum DeliveryStatus {
  PENDING,        // 주문 생성 후 아직 배정되지 않음
  ASSIGNED,       // 배달원이 배정됨
  PICKED_UP,      // 픽업 완료
  IN_TRANSIT,     // 배달 중
  DELIVERED,      // 배달 완료
  CANCELLED      // 취소됨
}
