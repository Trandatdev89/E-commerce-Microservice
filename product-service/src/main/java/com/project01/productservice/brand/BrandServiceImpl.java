package com.project01.productservice.brand;

import com.project01.productservice.category.Category;
import com.project01.productservice.category.CategoryDTO;
import com.project01.productservice.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl {

    private final BrandRepository brandRepository;

    public List<BrandDTO> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        List<BrandDTO> brandDTOS = new ArrayList<>();
        for (Brand brand : brands) {
            brandDTOS.add(new BrandDTO(brand.getId(),brand.getName()));
        }
        return brandDTOS;
    }

    public void createBrand(BrandDTO brandDTO) {
        Brand brand = Brand.builder()
                .name(brandDTO.getName())
                .build();
        brandRepository.save(brand);
    }

}
