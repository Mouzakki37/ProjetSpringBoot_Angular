package com.safanner.ecomm.controller.customer;

import com.safanner.ecomm.dto.WishListDto;
import com.safanner.ecomm.services.customer.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class WishlistController {
	private final WishlistService wishlistService;
	
	@PostMapping("/wishlist")
	public ResponseEntity<?> addProductToWishlist(@RequestBody WishListDto wishlistDto){
		WishListDto posteWishlistDto = wishlistService.addProductToWishlist(wishlistDto);
		if(posteWishlistDto==null) return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("something went wrong");
		return ResponseEntity.status(HttpStatus.CREATED).body(posteWishlistDto);
	}
	
	@GetMapping("/wishlist/{userId}")
	public ResponseEntity<List<WishListDto>> getWishlistByUserId(@PathVariable Long userId){
		return ResponseEntity.ok(wishlistService.getWishlistByUserId(userId));
	}
}
