package com.tejnal.stockexchange.mapper;

import com.tejnal.stockexchange.data.entity.Orders;
import com.tejnal.stockexchange.model.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface IOrderEntityToOrderResponseMapper {

  @Mappings({
    @Mapping(target = "orderId", source = "order.id"),
    @Mapping(target = "ticker", source = "order.ticker"),
    @Mapping(target = "orderSide", source = "order.orderSide"),
    @Mapping(target = "volume", source = "order.volume"),
    @Mapping(target = "price", source = "order.price"),
    @Mapping(target = "currency", source = "order.currency"),
    @Mapping(target = "orderDate", source = "order.createdDate", dateFormat = "dd-MM-yyyy")
  })
  OrderResponse mapSavedOrderEntityToOrderResponse(Orders order);
}
