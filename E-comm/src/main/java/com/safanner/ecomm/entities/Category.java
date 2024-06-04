package com.safanner.ecomm.entities;

import com.safanner.ecomm.dto.CategoryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@Builder
// TODO : Change table name
@Table(name="categoty")
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    private String description;

    public CategoryDto getDto(){
        return CategoryDto.builder()
                .id(id)
                .description(description)
                .name(name)
                .build();
    }

}
