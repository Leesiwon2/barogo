package com.example.barogo.delivery.repository;

import com.example.barogo.delivery.domain.Delivery;
import com.example.barogo.delivery.domain.QDelivery;
import com.example.barogo.delivery.dto.DeliveryRequestDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Delivery> searchDeliveries(DeliveryRequestDto request) {
    QDelivery delivery = QDelivery.delivery;
    BooleanBuilder builder = new BooleanBuilder();

    if (request.getStartDate() != null && request.getEndDate() != null) {
      builder.and(delivery.createdAt.between(
          request.getStartDate().atStartOfDay(),
          request.getEndDate().atTime(23, 59, 59)
      ));
    }

    if (request.getUserId() != null && !request.getUserId().isEmpty()) {
      builder.and(delivery.userId.eq(request.getUserId()));
    }

    return queryFactory
        .selectFrom(delivery)
        .where(builder)
        .fetch();
  }
}
