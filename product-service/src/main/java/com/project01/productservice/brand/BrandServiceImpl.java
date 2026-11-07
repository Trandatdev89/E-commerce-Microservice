package com.project01.productservice.brand;

import com.project01.productservice.entity.Brand;
import com.project01.productservice.utils.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl {

    private final BrandRepository brandRepository;

    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAllByIsActiveTrue().stream()
                .map(brand -> new BrandDTO(brand.getId(), brand.getName(), brand.getSlug(), brand.getLogoUrl()))
                .toList();
    }

    public void createBrand(BrandDTO brandDTO) {
        Brand brand = Brand.builder()
                .name(brandDTO.getName())
                .slug(SlugUtil.toUniqueSlug(brandDTO.getName(), brandRepository::existsBySlug))
                .logoUrl(brandDTO.getLogoUrl())
                .build();
        brandRepository.save(brand);
    }

}
