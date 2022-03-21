package com.tejnal.stockexchange.mapper;

import com.tejnal.stockexchange.data.entity.Orders;
import com.tejnal.stockexchange.model.request.NewOrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface IOrderRequestToOrderEntityMapper {
  @Mappings({
    @Mapping(target = "ticker", source = "newOrderRequest.ticker"),
    @Mapping(target = "orderSide", source = "newOrderRequest.orderSide"),
    @Mapping(target = "volume", source = "newOrderRequest.volume"),
    @Mapping(target = "price", source = "newOrderRequest.price"),
    @Mapping(target = "currency", source = "newOrderRequest.currency")
  })
  Orders mapOrderRequestToOrderEntity(NewOrderRequest newOrderRequest);
}
