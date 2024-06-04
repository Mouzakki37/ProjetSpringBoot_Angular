package com.safanner.ecomm.controller.admin;

import com.safanner.ecomm.entities.Coupon;
import com.safanner.ecomm.exceptions.ValidationException;
import com.safanner.ecomm.services.admin.coupon.AdminCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @PostMapping()
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon){
        if(coupon.getCode()==null) ResponseEntity.status(HttpStatus.CONFLICT).body("codeEmpty");
        try {
            System.out.println(coupon.toString());
            Coupon createdCoupon = adminCouponService.createCoupon(coupon);
            return ResponseEntity.ok(createdCoupon);
        }catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupon(){
        return ResponseEntity.ok(adminCouponService.getAllCoupon());
    }

}
