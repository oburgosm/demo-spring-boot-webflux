package com.capgemini.demo.webflux.mapper;

import java.util.List;

import com.capgemini.demo.webflux.model.domain.Customer;
import com.capgemini.demo.webflux.model.dto.CustomerDTO;
import org.mapstruct.Mapper;

/**
 *
 * @author oburgosm
 */
@Mapper(componentModel="spring")
public interface CustomerMapper {
    
    Customer customerDTOToCustomer(CustomerDTO customer);
    List<Customer> customerDTOToCustomer(List<CustomerDTO> customers);
    
    CustomerDTO customerToCustomerDTO(Customer customer);
    List<CustomerDTO> customerToCustomerDTO(List<CustomerDTO> customers);
    
}
