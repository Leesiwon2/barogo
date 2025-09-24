package com.example.barogo.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.example.barogo.common.error.BadRequestException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DeliveryTest {

  private Delivery createDelivery(DeliveryStatus status) {
    return Delivery.builder()
        .userId("user1")
        .departureLocation("서울")
        .arrivalLocation("부산")
        .status(status)
        .createdAt(LocalDateTime.now())
        .build();
  }
  @Test
  @DisplayName("PENDING → ASSIGNED 변경 성공")
  void changeStatus_pendingToAssigned_success() {
    Delivery delivery = createDelivery(DeliveryStatus.PENDING);

    delivery.changeStatus(DeliveryStatus.ASSIGNED);

    assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.ASSIGNED);
  }

  @Test
  @DisplayName("PENDING → CANCELLED 변경 성공")
  void changeStatus_pendingToCancelled_success() {
    Delivery delivery = createDelivery(DeliveryStatus.PENDING);

    delivery.changeStatus(DeliveryStatus.CANCELLED);

    assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
  }

  @Test
  @DisplayName("PENDING → PICKED_UP 실패")
  void changeStatus_pendingToPickedUp_fail() {
    Delivery delivery = createDelivery(DeliveryStatus.PENDING);

    assertThatThrownBy(() -> delivery.changeStatus(DeliveryStatus.PICKED_UP))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("상태 변경 불가: PENDING → PICKED_UP");
  }

  @Test
  @DisplayName("ASSIGNED → PICKED_UP 변경 성공")
  void changeStatus_assignedToPickedUp_success() {
    Delivery delivery = createDelivery(DeliveryStatus.ASSIGNED);

    delivery.changeStatus(DeliveryStatus.PICKED_UP);

    assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.PICKED_UP);
  }

  @Test
  @DisplayName("ASSIGNED → CANCELLED 변경 성공")
  void changeStatus_assignedToCancelled_success() {
    Delivery delivery = createDelivery(DeliveryStatus.ASSIGNED);

    delivery.changeStatus(DeliveryStatus.CANCELLED);

    assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
  }

  @Test
  @DisplayName("ASSIGNED → DELIVERED 실패")
  void changeStatus_assignedToDelivered_fail() {
    Delivery delivery = createDelivery(DeliveryStatus.ASSIGNED);

    assertThatThrownBy(() -> delivery.changeStatus(DeliveryStatus.DELIVERED))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("상태 변경 불가: ASSIGNED → DELIVERED");
  }

  @Test
  @DisplayName("PICKED_UP → IN_TRANSIT 변경 성공")
  void changeStatus_pickedUpToInTransit_success() {
    Delivery delivery = createDelivery(DeliveryStatus.PICKED_UP);

    delivery.changeStatus(DeliveryStatus.IN_TRANSIT);

    assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_TRANSIT);
  }

  @Test
  @DisplayName("IN_TRANSIT → DELIVERED 변경 성공")
  void changeStatus_inTransitToDelivered_success() {
    Delivery delivery = createDelivery(DeliveryStatus.IN_TRANSIT);

    delivery.changeStatus(DeliveryStatus.DELIVERED);

    assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
  }

  @Test
  @DisplayName("DELIVERED 이후 상태 변경 실패")
  void changeStatus_delivered_fail() {
    Delivery delivery = createDelivery(DeliveryStatus.DELIVERED);

    assertThatThrownBy(() -> delivery.changeStatus(DeliveryStatus.CANCELLED))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("상태 변경 불가: DELIVERED → CANCELLED");
  }

  @Test
  @DisplayName("CANCELLED 이후 상태 변경 실패")
  void changeStatus_cancelled_fail() {
    Delivery delivery = createDelivery(DeliveryStatus.CANCELLED);

    assertThatThrownBy(() -> delivery.changeStatus(DeliveryStatus.ASSIGNED))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("상태 변경 불가: CANCELLED → ASSIGNED");
  }
}
