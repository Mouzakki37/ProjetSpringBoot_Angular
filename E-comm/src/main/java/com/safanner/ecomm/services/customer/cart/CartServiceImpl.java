package com.safanner.ecomm.services.customer.cart;

import com.safanner.ecomm.dto.AddProductInCartDto;
import com.safanner.ecomm.dto.CartItemsDto;
import com.safanner.ecomm.dto.OrderDto;
import com.safanner.ecomm.dto.PlaceOrderDto;
import com.safanner.ecomm.entities.*;
import com.safanner.ecomm.enums.OrderStatus;
import com.safanner.ecomm.exceptions.ValidationException;
import com.safanner.ecomm.repositories.*;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.safanner.ecomm.enums.OrderStatus.Pending;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CartItemsRepository cartItemsRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final CouponRepository couponRepository;

    public ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto) {

        System.out.println(addProductInCartDto.toString());

        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(),
                OrderStatus.Pending);

        if (activeOrder == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order is empty");

        // check if that same product is already present for that particular user
        Optional<CartItems> optionalCartItems = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId());

        System.out.println(addProductInCartDto.getUserId() + " <=> " + OrderStatus.Pending);

        if (optionalCartItems.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } else {
            Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
            Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());
            if (optionalUser.isPresent() && optionalProduct.isPresent()) {
                CartItems cartItems = new CartItems();
                cartItems.setProduct(optionalProduct.get());
                cartItems.setPrice(optionalProduct.get().getPrice());
                cartItems.setQuantity(1L);
                cartItems.setUser(optionalUser.get());

                cartItems.setOrder(activeOrder);

                CartItems updatedCart = cartItemsRepository.save(cartItems);
                activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cartItems.getPrice());
                activeOrder.setAmount(activeOrder.getAmount() + cartItems.getPrice());
                activeOrder.getCartItems().add(cartItems);

                orderRepository.save(activeOrder);

                return ResponseEntity.status(HttpStatus.CREATED).body(cartItems);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or product not found");
            }

        }

    }

    public OrderDto getCartByUserId(Long userId) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);
        // if order is in other state than pending
        if (activeOrder == null)
            return null;
        List<CartItemsDto> cartItemsDtosList = activeOrder.getCartItems().stream().map(CartItems::getCartDto)
                .collect(Collectors.toList());

        OrderDto orderDto = new OrderDto();
        orderDto.setId(activeOrder.getId());
        orderDto.setAmount(activeOrder.getAmount());
        orderDto.setOrderStatus(activeOrder.getOrderStatus());
        orderDto.setDiscount(activeOrder.getDiscount());
        orderDto.setTotalAmount(activeOrder.getTotalAmount());
        orderDto.setCartItems(cartItemsDtosList);
        if (activeOrder.getCoupon() != null)
            orderDto.setCouponCode(activeOrder.getCoupon().getCode());

        if (activeOrder.getCoupon() != null) {
            orderDto.setCouponName(activeOrder.getCoupon().getName());
        }

        return orderDto;
    }

    public OrderDto applyCoupon(Long userId, String code) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.Pending);
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ValidationException("coupon not found"));
        if (couponIsExpired(coupon)) {
            throw new ValidationException("coupon is expired");
        }
        double discountAmount = ((coupon.getDiscount() / 100.0) * activeOrder.getTotalAmount());
        double netAmount = activeOrder.getTotalAmount() - discountAmount;

        activeOrder.setAmount((long) netAmount);
        activeOrder.setDiscount((long) discountAmount);
        activeOrder.setCoupon(coupon);

        orderRepository.save(activeOrder);
        return activeOrder.getOrderDto();

    }

    public boolean couponIsExpired(Coupon coupon) {
        Date currentDate = new Date();
        Date expirationDate = coupon.getExpirationDate();
        return expirationDate != null && currentDate.after(expirationDate);
    }

    public OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(),
                OrderStatus.Pending);
        Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
        Optional<CartItems> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                optionalProduct.get().getId(), activeOrder.getId(), addProductInCartDto.getUserId());

        if (optionalCartItem.isPresent()) {
            CartItems cartItems = optionalCartItem.get();
            Product product = optionalProduct.get();

            activeOrder.setAmount(activeOrder.getAmount() + product.getPrice());
            activeOrder.setTotalAmount(activeOrder.getTotalAmount() + product.getPrice());

            cartItems.setQuantity(cartItems.getQuantity() + 1);

            if (activeOrder.getCoupon() != null) {
                double discountAmount = ((activeOrder.getCoupon().getDiscount() / 100.0)
                        * activeOrder.getTotalAmount());
                double netAmount = activeOrder.getTotalAmount() - discountAmount;

                activeOrder.setAmount((long) netAmount);
                activeOrder.setDiscount((long) discountAmount);
            }

            cartItemsRepository.save(cartItems);
            orderRepository.save(activeOrder);
            return activeOrder.getOrderDto();
        }
        return null;
    }

    public OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(),
                OrderStatus.Pending);
        Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
        Optional<CartItems> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                optionalProduct.get().getId(), activeOrder.getId(), addProductInCartDto.getUserId());

        // if product is present
        if (optionalCartItem.isPresent()) {
            CartItems cartItems = optionalCartItem.get();
            Product product = optionalProduct.get();

            // update amount
            activeOrder.setAmount(activeOrder.getAmount() - product.getPrice());
            activeOrder.setTotalAmount(activeOrder.getTotalAmount() - product.getPrice());

            // decrease quantity
            cartItems.setQuantity(cartItems.getQuantity() - 1);

            if (activeOrder.getCoupon() != null) {
                double discountAmount = ((activeOrder.getCoupon().getDiscount() / 100.0)
                        * activeOrder.getTotalAmount());
                double netAmount = activeOrder.getTotalAmount() - discountAmount;

                activeOrder.setAmount((long) netAmount);
                activeOrder.setDiscount((long) discountAmount);
            }

            cartItemsRepository.save(cartItems);
            orderRepository.save(activeOrder);
            return activeOrder.getOrderDto();
        }
        return null;
    }

    public OrderDto placedOrder(PlaceOrderDto placeOrderDto) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(placeOrderDto.getUserId(), OrderStatus.Pending);
        Optional<User> optionalUser = userRepository.findById(placeOrderDto.getUserId());
        if (optionalUser.isPresent()) {
            activeOrder.setOrderDescription(placeOrderDto.getOrderDescription());
            // debug
            System.out.println(placeOrderDto.getOrderDescription());
            activeOrder.setAddress(placeOrderDto.getAddress());
            activeOrder.setDate(new Date());
            activeOrder.setOrderStatus(OrderStatus.Placed);
            activeOrder.setTrackingId(UUID.randomUUID());

            orderRepository.save(activeOrder);

            Order order = new Order();
            order.setAmount(0L);
            order.setTotalAmount(0L);
            order.setDiscount(0L);
            order.setUser(optionalUser.get());
            order.setOrderStatus(OrderStatus.Pending);
            orderRepository.save(order);

            return activeOrder.getOrderDto();
        }
        return null;
    }

    public List<OrderDto> getMyPlacedOrders(Long userId) {
        return orderRepository
                .findByUserIdAndOrderStatusIn(userId,
                        List.of(OrderStatus.Shipped, OrderStatus.Placed, OrderStatus.Delivered))
                .stream().map(Order::getOrderDto).collect(Collectors.toList());
    }

    public OrderDto searchOrderByTrackingId(UUID trackingId) {
        Optional<Order> optionalOrder = orderRepository.findByTrackingId(trackingId);
        return optionalOrder.map(Order::getOrderDto).orElse(null);
    }

}
