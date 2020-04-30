package com.capgemini.demo.webflux.mapper;

import java.util.List;

import com.capgemini.demo.webflux.model.domain.OrderItem;
import com.capgemini.demo.webflux.model.dto.OrderItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 *
 * @author oburgosm
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mappings({
        @Mapping(target = "order", ignore = true)
    })
    OrderItem orderItemDTOToOrderItem(OrderItemDTO order);

    List<OrderItem> orderItemDTOToOrderItem(List<OrderItemDTO> order);

    @Mappings({
        @Mapping(target = "order", ignore = true),
    })
    OrderItemDTO orderItemToOrderItemDTO(OrderItem order);

    List<OrderItemDTO> orderItemToOrderItemDTO(List<OrderItem> order);

}
