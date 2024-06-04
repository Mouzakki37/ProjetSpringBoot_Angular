package com.safanner.ecomm.services.admin.category;

import com.safanner.ecomm.dto.CategoryDto;
import com.safanner.ecomm.entities.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDto categoryDto);
    List<Category> getAllCategory();
}
