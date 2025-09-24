package com.example.barogo.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.barogo.common.error.BadRequestException;
import com.example.barogo.delivery.domain.Delivery;
import com.example.barogo.delivery.domain.DeliveryStatus;
import com.example.barogo.delivery.dto.ChangeStatusRequestDto;
import com.example.barogo.delivery.dto.CreateDeliveryRequestDto;
import com.example.barogo.delivery.dto.DeliveryItemRequestDto;
import com.example.barogo.delivery.dto.DeliveryRequestDto;
import com.example.barogo.delivery.dto.UpdateArrivalLocationRequestDto;
import com.example.barogo.delivery.repository.DeliveryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DeliveryServiceTest {
  @Mock
  private DeliveryRepository deliveryRepository;

  @InjectMocks
  private DeliveryService deliveryService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("배달 생성")
  void createDelivery() {
    CreateDeliveryRequestDto request = new CreateDeliveryRequestDto(
        "user1",
        "서울",
        "부산",
        java.util.List.of(new DeliveryItemRequestDto("물", 5, BigDecimal.valueOf(5000)))
    );

    deliveryService.createDelivery(request);

    verify(deliveryRepository, times(1)).save(any(Delivery.class));
  }

  @Test
  @DisplayName("기간으로만 배달 조회 성공")
  void searchDeliveries_success_withPeriodOnly() {
    Delivery delivery1 = Delivery.builder()
        .userId("user1")
        .departureLocation("서울")
        .arrivalLocation("부산")
        .status(DeliveryStatus.PENDING)
        .build();

    Delivery delivery2 = Delivery.builder()
        .userId("user2")
        .departureLocation("인천")
        .arrivalLocation("대전")
        .status(DeliveryStatus.ASSIGNED)
        .build();

    DeliveryRequestDto request = new DeliveryRequestDto(
        LocalDate.now().minusDays(1),
        LocalDate.now(),
        null
    );

    when(deliveryRepository.searchDeliveries(any()))
        .thenReturn(List.of(delivery1, delivery2));

    List<Delivery> results = deliveryService.deliveries(request);

    assertThat(results).hasSize(2);
    assertThat(results).extracting("userId").containsExactly("user1", "user2");
  }

  @Test
  @DisplayName("기간 + 사용자 ID로 배달 조회 성공")
  void getDeliveries_success() {
    Delivery delivery1 = Delivery.builder()
        .userId("user1")
        .departureLocation("서울")
        .arrivalLocation("부산")
        .status(DeliveryStatus.PENDING)
        .createdAt(LocalDateTime.of(2025, 9, 22, 10, 0))
        .modifiedAt(LocalDateTime.now())
        .build();

    Delivery delivery2 = Delivery.builder()
        .userId("user1")
        .departureLocation("대전")
        .arrivalLocation("광주")
        .status(DeliveryStatus.DELIVERED)
        .createdAt(LocalDateTime.of(2025, 9, 23, 14, 0))
        .modifiedAt(LocalDateTime.now())
        .build();

    DeliveryRequestDto request = new DeliveryRequestDto(
        LocalDate.of(2025, 9, 22),
        LocalDate.of(2025, 9, 23),
        "user1"
    );

    when(deliveryRepository.searchDeliveries(request))
        .thenReturn(List.of(delivery1, delivery2));

    var results = deliveryService.deliveries(request);

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getUserId()).isEqualTo("user1");
    verify(deliveryRepository, times(1)).searchDeliveries(request);
  }

  @Test
  @DisplayName("기간 + 사용자 ID로 배달 조회 실패")
  void searchDeliveries_fail_noResults() {
    DeliveryRequestDto request = new DeliveryRequestDto(
        LocalDate.now().minusDays(1),
        LocalDate.now(),
        "nonexistentUser"
    );

    when(deliveryRepository.searchDeliveries(any())).thenReturn(Collections.emptyList());

    List<Delivery> results = deliveryService.deliveries(request);

    assertThat(results).isEmpty();
  }


  @Test
  @DisplayName("배달 조회 기간 3일 초과")
  void searchDeliveries_fail_exceedPeriod() {
    DeliveryRequestDto request = new DeliveryRequestDto(
        LocalDate.now().minusDays(5),
        LocalDate.now(),
        "user1"
    );

    assertThatThrownBy(() -> deliveryService.deliveries(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("조회 기간은 최대 3일까지만 가능합니다.");
  }

  @Test
  @DisplayName("도착지 주소 변경 성공")
  void updateArrivalLocation_success() {

    Delivery delivery = Delivery.builder()
        .idx(1L)
        .userId("user1")
        .departureLocation("서울")
        .arrivalLocation("부산")
        .status(DeliveryStatus.PENDING)
        .createdAt(LocalDateTime.now())
        .modifiedAt(LocalDateTime.now())
        .build();

    UpdateArrivalLocationRequestDto request =
        new UpdateArrivalLocationRequestDto(1L, "인천");

    when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

    deliveryService.updateArrivalLocation(request);

    assertThat(delivery.getArrivalLocation()).isEqualTo("인천");
    verify(deliveryRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("픽업 완료 이후 상태에서는 도착지 주소 변경 실패")
  void updateArrivalLocation_fail_afterPickedUp() {

    Delivery delivery = Delivery.builder()
        .idx(1L)
        .userId("user1")
        .departureLocation("서울")
        .arrivalLocation("부산")
        .status(DeliveryStatus.PICKED_UP)
        .createdAt(LocalDateTime.now())
        .modifiedAt(LocalDateTime.now())
        .build();

    UpdateArrivalLocationRequestDto request =
        new UpdateArrivalLocationRequestDto(1L, "인천");

    when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));


    assertThatThrownBy(() -> deliveryService.updateArrivalLocation(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("픽업 완료 이후에는 도착지 주소를 변경할 수 없습니다.");

    verify(deliveryRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("배달 상태 변경 성공")
  void updateStatus_success() {
    Delivery delivery = Delivery.builder()
        .userId("user1")
        .status(DeliveryStatus.PENDING)
        .departureLocation("서울")
        .arrivalLocation("부산")
        .build();

    when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

    ChangeStatusRequestDto requestDto = new ChangeStatusRequestDto(1L, DeliveryStatus.ASSIGNED);

    deliveryService.changeStatus(requestDto);

    assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.ASSIGNED);
  }

  @Test
  @DisplayName("배달 상태 변경 실패 - 존재하지 않는 배달")
  void updateStatus_fail_deliveryNotFound() {
    ChangeStatusRequestDto requestDto = new ChangeStatusRequestDto(999L, DeliveryStatus.ASSIGNED);

    when(deliveryRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> deliveryService.changeStatus(requestDto))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("배달 건이 존재하지 않습니다.");
  }

  private Delivery createWithStatus(DeliveryStatus status) {
    return Delivery.builder()
        .idx(1L)
        .userId("user1")
        .departureLocation("서울")
        .arrivalLocation("부산")
        .status(status)
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("상태 변경 성공 - PENDING → ASSIGNED")
  void changeStatus_success_pendingToAssigned() {
    Delivery delivery = createWithStatus(DeliveryStatus.PENDING);
    when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

    ChangeStatusRequestDto request = new ChangeStatusRequestDto(1L, DeliveryStatus.ASSIGNED);
    deliveryService.changeStatus(request);

    assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.ASSIGNED);
    verify(deliveryRepository).findById(1L);
  }

  @Test
  @DisplayName("상태 변경 성공 - ASSIGNED → PICKED_UP")
  void changeStatus_success_assignedToPickedUp() {
    Delivery delivery = createWithStatus(DeliveryStatus.ASSIGNED);
    when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

    ChangeStatusRequestDto request = new ChangeStatusRequestDto(1L, DeliveryStatus.PICKED_UP);
    deliveryService.changeStatus(request);

    assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.PICKED_UP);
  }

  @Test
  @DisplayName("상태 변경 실패 - 존재하지 않는 배달 건")
  void changeStatus_fail_notFound() {
    when(deliveryRepository.findById(999L)).thenReturn(Optional.empty());

    ChangeStatusRequestDto request = new ChangeStatusRequestDto(999L, DeliveryStatus.ASSIGNED);

    assertThatThrownBy(() -> deliveryService.changeStatus(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("배달 건이 존재하지 않습니다.");
  }

  @Test
  @DisplayName("상태 변경 실패 - PENDING → PICKED_UP")
  void changeStatus_fail_invalidTransition() {
    Delivery delivery = createWithStatus(DeliveryStatus.PENDING);
    when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

    ChangeStatusRequestDto request = new ChangeStatusRequestDto(1L, DeliveryStatus.PICKED_UP);

    assertThatThrownBy(() -> deliveryService.changeStatus(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("상태 변경 불가: PENDING → PICKED_UP");
  }

  @Test
  @DisplayName("상태 변경 실패 - 이미 DELIVERED 된 건")
  void changeStatus_fail_alreadyDelivered() {
    Delivery delivery = createWithStatus(DeliveryStatus.DELIVERED);
    when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

    ChangeStatusRequestDto request = new ChangeStatusRequestDto(1L, DeliveryStatus.CANCELLED);

    assertThatThrownBy(() -> deliveryService.changeStatus(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("상태 변경 불가: DELIVERED → CANCELLED");
  }
}
