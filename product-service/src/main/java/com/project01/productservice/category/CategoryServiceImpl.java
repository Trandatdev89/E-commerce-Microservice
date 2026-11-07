package com.project01.productservice.category;

import com.project01.common.core.exception.BusinessException;
import com.project01.productservice.entity.Category;
import com.project01.productservice.exception.ErrorCode;
import com.project01.productservice.utils.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl {

    private final CategoryRepository categoryRepository;

    public void createCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getParentId() != null && !categoryRepository.existsById(categoryDTO.getParentId())) {
            throw new BusinessException(ErrorCode.CATEGORY_NOTFOUND);
        }
        Category category = Category.builder()
                .parentId(categoryDTO.getParentId())
                .name(categoryDTO.getName())
                .slug(SlugUtil.toUniqueSlug(categoryDTO.getName(), categoryRepository::existsBySlug))
                .imageUrl(categoryDTO.getImageUrl())
                .sortOrder(categoryDTO.getSortOrder() == null ? 0 : categoryDTO.getSortOrder())
                .build();
        categoryRepository.save(category);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAllByIsActiveTrueOrderBySortOrder().stream()
                .map(category -> CategoryDTO.builder()
                        .id(category.getId())
                        .parentId(category.getParentId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .imageUrl(category.getImageUrl())
                        .sortOrder(category.getSortOrder())
                        .build())
                .toList();
    }
}
