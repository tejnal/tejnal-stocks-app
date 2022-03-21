package com.tejnal.stockexchange.data.repository;

import com.tejnal.stockexchange.data.entity.Orders;
import com.tejnal.stockexchange.data.enums.EOrderSide;
import com.tejnal.stockexchange.model.response.OrderSummaryResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Repository
@Transactional
public interface OrderRepository extends JpaRepository<Orders, Long> {

  @Query(
      "SELECT new com.tejnal.stockexchange.model.response.OrderSummaryResponse(CAST(a.createdDate as date), (SUM(a.price*a.volume)/SUM(a.volume)), MIN(a.price), MAX(a.price), COUNT(*)) FROM Orders a WHERE a.ticker = :ticker AND a.orderSide = :orderSide AND CAST(a.createdDate AS date) = CAST(:orderDate AS date) AND a.createdBy = :userId GROUP BY CAST(a.createdDate as date)")
  Optional<OrderSummaryResponse> fetchOrderSummary(
      @Param("userId") Long userId,
      @Param("ticker") String ticker,
      @Param("orderSide") EOrderSide orderSide,
      @Param("orderDate") Date orderDate);

  @Query("FROM Orders a WHERE a.createdBy = :userId AND a.id = :orderId")
  Optional<Orders> fetchOrderById(@Param("userId") Long userId, @Param("orderId") Long orderId);

  @Query(
      "SELECT DISTINCT a.currency FROM Orders a WHERE a.createdBy = :userId AND CAST(a.createdDate AS date) = CAST(CURRENT_DATE AS date)")
  Optional<String> fetchDistinctOrderCurrByUserOnSameDay(@Param("userId") Long userId);

  @Modifying
  @Query("DELETE FROM Orders")
  @Profile("test")
  void truncateAllOrdersForTest();
}
