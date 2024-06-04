package com.safanner.ecomm.services.customer.review;

import com.safanner.ecomm.dto.OrderedProductsResponseDto;
import com.safanner.ecomm.dto.ReviewDto;

import java.io.IOException;

public interface ReviewService {
    OrderedProductsResponseDto getOrderedProductsDetailsByOrderId(Long orderId);

    ReviewDto giveReview(ReviewDto reviewDto) throws IOException ;
}
