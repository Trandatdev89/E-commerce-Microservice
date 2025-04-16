package com.project01.productservice.dto.request;

import com.project01.productservice.entity.Address;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Name product is not null or empty")
    private String name;
    private String description;
    @Min(value = 1)
    private Long availableQuantity;
    @NotNull(message = "Price product is not null or empty")
    private Long price;
    @NotNull(message = "Category is not null")
    private Long categoryId;
    @NotNull(message = "brand is not null")
    private Long brandId;
    private Address address;
    @NotNull(message = "ship is not null")
    private List<Long> shipId;
}
