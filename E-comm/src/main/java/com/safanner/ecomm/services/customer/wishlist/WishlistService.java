package com.safanner.ecomm.services.customer.wishlist;


import com.safanner.ecomm.dto.WishListDto;

import java.util.List;

public interface WishlistService {
    WishListDto addProductToWishlist( WishListDto wishlistDto);

    List<WishListDto> getWishlistByUserId(Long userId);
}
