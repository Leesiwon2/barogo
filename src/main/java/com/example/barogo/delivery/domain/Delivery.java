package com.example.barogo.delivery.domain;

import com.example.barogo.common.error.BadRequestException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "delivery")
public class Delivery {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;

  @Column(name="user_id", nullable = false)
  private String userId;

  @Column(name="departure_location", nullable = false)
  private String departureLocation;

  @Column(name="arrival_location", nullable = false)
  private String arrivalLocation;

  @Enumerated(EnumType.STRING)
  private DeliveryStatus status;

  @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<DeliveryItem> items = new ArrayList<>();

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name= "modified_at")
  private LocalDateTime modifiedAt;

  public void addItem(DeliveryItem item) {
    items.add(item);
    item.setDelivery(this);
  }

  public void changeArrivalLocation(String newArrivalLocation) {
    if (!(this.status == DeliveryStatus.PENDING || this.status == DeliveryStatus.ASSIGNED)) {
      throw new BadRequestException("픽업 완료 이후에는 도착지 주소를 변경할 수 없습니다.");
    }
    this.arrivalLocation = newArrivalLocation;
    this.modifiedAt = LocalDateTime.now();
  }

  public void changeStatus(DeliveryStatus newStatus) {
    if (!isValidTransition(this.status, newStatus)) {
      throw new BadRequestException(String.format("상태 변경 불가: %s → %s", this.status, newStatus));
    }
    this.status = newStatus;
    this.modifiedAt = LocalDateTime.now();
  }

  private boolean isValidTransition(DeliveryStatus current, DeliveryStatus target) {
    return switch (current) {
      case PENDING -> (target == DeliveryStatus.ASSIGNED || target == DeliveryStatus.CANCELLED);
      case ASSIGNED -> (target == DeliveryStatus.PICKED_UP || target == DeliveryStatus.CANCELLED);
      case PICKED_UP -> target == DeliveryStatus.IN_TRANSIT;
      case IN_TRANSIT -> target == DeliveryStatus.DELIVERED;
      default -> false;
    };
  }
}
