package com.safanner.ecomm.services.admin.coupon;

import com.safanner.ecomm.entities.Coupon;

import java.util.List;


public interface AdminCouponService {
    Coupon createCoupon(Coupon coupon);
    List<Coupon> getAllCoupon();
}



