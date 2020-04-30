package com.capgemini.demo.webflux.mapper;

import java.util.List;

import com.capgemini.demo.webflux.model.domain.Product;
import com.capgemini.demo.webflux.model.dto.ProductDTO;
import org.mapstruct.Mapper;

/**
 *
 * @author oburgosm
 */
@Mapper(componentModel="spring")
public interface ProductMapper {
    
    ProductDTO productToProductDto(Product product);
    List<ProductDTO> productToProductDto(List<Product> product);
    
    Product productDtoToProduct(ProductDTO productDTO);
    List<Product> productDtoToProduct(List<ProductDTO> productDTO);
    
}
