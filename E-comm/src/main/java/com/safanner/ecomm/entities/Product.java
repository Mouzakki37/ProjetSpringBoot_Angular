package com.safanner.ecomm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safanner.ecomm.dto.ProductDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Table(name = "product")
public class Product {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String name;

   private Long price;

   @Lob
   @Column(columnDefinition = "TEXT")
   private String description;

   @Lob
   @Column(columnDefinition = "longblob")
   private byte[] img;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "category_id", nullable = false)
   @OnDelete(action = OnDeleteAction.CASCADE)
   @JsonIgnore
   private Category category;


   //	TODO  : add mapstruct
   public ProductDto getDto() {
      return ProductDto.builder()
              .id(id)
              .name(name)
              .price(price)
              .description(description)
              .byteImg(img)
              .categoryId(category.getId())
              .categoryName(category.getName())
              .build();
   }


}
