package com.capgemini.demo.webflux.mapper;


import java.util.List;

import com.capgemini.demo.webflux.model.domain.Order;
import com.capgemini.demo.webflux.model.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 *
 * @author oburgosm
 */
@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, ProductMapper.class, CustomerMapper.class})
public interface OrderMapper {

    @Mappings({
        @Mapping(target = "customer.orders", ignore = true)
    })
    Order orderDTOToOrder(OrderDTO order);
    

    List<Order> orderDTOToOrder(List<OrderDTO> orders);

    @Mappings({
        @Mapping(target = "customer.orders", ignore = true)        
    })
    OrderDTO orderToOrderDTO(Order order);

    List<OrderDTO> orderToOrderDTO(List<OrderDTO> orders);
    
    
}
