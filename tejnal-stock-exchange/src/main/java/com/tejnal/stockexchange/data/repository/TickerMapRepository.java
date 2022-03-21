package com.tejnal.stockexchange.data.repository;

import com.tejnal.stockexchange.data.entity.TickerMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface TickerMapRepository extends JpaRepository<TickerMap, Long> {
  @Query("FROM TickerMap a WHERE UPPER(a.tickerCode) = :tickerCode")
  Optional<TickerMap> findByTickerCode(@Param("tickerCode") String tickerCode);
}
