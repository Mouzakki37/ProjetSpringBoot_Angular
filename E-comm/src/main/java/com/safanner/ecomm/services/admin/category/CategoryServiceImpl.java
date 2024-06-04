package com.safanner.ecomm.services.admin.category;

import com.safanner.ecomm.dto.CategoryDto;
import com.safanner.ecomm.entities.Category;
import com.safanner.ecomm.repositories.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryDto categoryDto) {

        return categoryRepository.save(
                Category.builder()
                        .name(categoryDto.getName())
                        .description(categoryDto.getDescription())
                        .build()
        );
    }

    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }


}
