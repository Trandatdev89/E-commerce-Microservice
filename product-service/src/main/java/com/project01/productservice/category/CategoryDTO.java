package com.project01.productservice.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private Long parentId;
    private String name;
    private String slug;
    private String imageUrl;
    private Integer sortOrder;
}
