package com.example.barogo.delivery.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "delivery_item")
@Entity
public class DeliveryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idx;
  private String name;
  private int amount;
  private BigDecimal price;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_id")
  @JsonIgnore
  private Delivery delivery;

  public void setDelivery(Delivery delivery) {
    this.delivery = delivery;
  }
  public static DeliveryItem create(String name, int amount, BigDecimal price) {
    DeliveryItem item = new DeliveryItem();
    item.name = name;
    item.amount = amount;
    item.price = price;
    return item;
  }
}
