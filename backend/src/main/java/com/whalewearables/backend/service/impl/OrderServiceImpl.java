package com.whalewearables.backend.service.impl;

import com.cashfree.pg.model.PaymentEntity;

import com.whalewearables.backend.dto.BillingDTO;
import com.whalewearables.backend.dto.OrderRequest;
import com.whalewearables.backend.dto.PaymentDTO;
import com.whalewearables.backend.dto.ShippingDTO;
import com.whalewearables.backend.model.*;
import com.whalewearables.backend.repository.*;
import com.whalewearables.backend.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Order createOrder(OrderRequest request) {
        System.out.println("➡️ createOrder() called with: " + request);

        String orderId = "order_" + UUID.randomUUID();
        System.out.println("Generated orderId = " + orderId);

        Order order = new Order();
        order.setOrderId(orderId);
        order.setAmount(request.getAmount());
        order.setPhone(request.getPhone());
        order.setEmail(request.getEmail());
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Link user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("✅ Linked user: " + user.getId() + " (" + user.getEmail() + ")");
        order.setUser(user);

        // Billing
        if (request.getBilling() != null) {
            BillingDTO dto = request.getBilling();
            System.out.println("📦 Billing info received: " + dto);

            OrderBilling billing = new OrderBilling();
            billing.setFullName(dto.getFullName());
            billing.setAddressLine1(dto.getAddressLine1());
            billing.setAddressLine2(dto.getAddressLine2());
            billing.setCity(dto.getCity());
            billing.setState(dto.getState());
            billing.setPostalCode(dto.getPostalCode());
            billing.setCountry(dto.getCountry());
            billing.setPhoneNumber(dto.getPhoneNumber());
            billing.setEmail(dto.getEmail());

            billing.setOrder(order);
            order.setBilling(billing);
        }

        // Shipping
        if (request.getShipping() != null) {
            ShippingDTO dto = request.getShipping();
            System.out.println("📦 Shipping info received: " + dto);

            OrderShipping shipping = new OrderShipping();
            shipping.setFullName(dto.getFullName());
            shipping.setAddressLine1(dto.getAddressLine1());
            shipping.setAddressLine2(dto.getAddressLine2());
            shipping.setCity(dto.getCity());
            shipping.setState(dto.getState());
            shipping.setPostalCode(dto.getPostalCode());
            shipping.setCountry(dto.getCountry());
            shipping.setPhoneNumber(dto.getPhoneNumber());
            shipping.setEmail(dto.getEmail());

            shipping.setOrder(order);
            order.setShipping(shipping);
        }

        // Items
        if (request.getItems() != null) {
            System.out.println("🛒 Items count = " + request.getItems().size());
            order.setItems(
                    request.getItems().stream().map(itemDTO -> {
                        Product product = productRepository.findById(itemDTO.getProductId())
                                .orElseThrow(() -> new RuntimeException("Invalid productId: " + itemDTO.getProductId()));

                        System.out.println("   ↳ Adding item: " + product.getName() +
                                " | qty=" + itemDTO.getQuantity() +
                                " | price=" + product.getPrice());

                        OrderItem item = new OrderItem();
                        item.setOrder(order);
                        item.setProduct(product);
                        item.setProductName(product.getName());
                        item.setQuantity(itemDTO.getQuantity());
                        item.setPrice(product.getPrice());
                        return item;
                    }).collect(Collectors.toList())
            );
        }

        Order saved = orderRepository.save(order);
        System.out.println("✅ Order persisted: orderId=" + order.getOrderId());
        return saved;
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderId, String status) {
        System.out.println("➡️ updateOrderStatus(" + orderId + ", " + status + ")");
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        System.out.println("✅ Order status updated to " + status);
    }

    @Override
    public Order getOrder(String orderId) {
        System.out.println("➡️ getOrder(" + orderId + ")");
        return orderRepository.findByOrderId(orderId).orElse(null);
    }

    /**
     * Called after successful payment confirmation
     */
    @Transactional
    public void markOrderAsPaid(PaymentDTO paymentDTO) {
//        System.out.println("➡️ markOrderAsPaid() called with: " + paymentDTO);
        Order order = orderRepository.findByOrderId(paymentDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        System.out.println("Found order: " + order.getOrderId() + " status=" + order.getStatus());

        if ("PAID".equalsIgnoreCase(order.getStatus())) {
            System.out.println("⚠️ Order already paid, skipping duplicate payment.");
            return;
        }

        // Update status
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());

        // Save payment
        savePayment(order, paymentDTO);

        // Reduce stock
        reduceStockAfterPurchase(order.getOrderId());

        // Clear cart
        if (paymentDTO.getUserId() != null) {
            System.out.println("🧹 Clearing cart for userId=" + paymentDTO.getUserId());
            cartRepository.findByUserId(paymentDTO.getUserId())
                    .ifPresent(cart -> cartItemRepository.deleteByCart(cart));
        }

        // Update order status
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order); // saves payment too
        System.out.println("✅ Order marked as PAID and saved with payments."+order);
    }

    @Transactional
    public Payments savePayment(Order order, PaymentDTO dto) {
        System.out.println("inside the payment method"+order+dto);
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payments payment = new Payments();
        payment.setPaymentId(dto.getTransactionId());
        payment.setOrder(order);
        payment.setUser(user); // ✅ use relation, not raw userId
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "INR");
        payment.setStatus(dto.getStatus() != null ? dto.getStatus() : PaymentStatus.PENDING);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setTransactionId(dto.getTransactionId());

        return paymentRepository.save(payment);
    }

    @Transactional
    public void reduceStockAfterPurchase(String orderId) {
        System.out.println("➡️ reduceStockAfterPurchase(" + orderId + ")");
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int currentStock = product.getStockQuantity();
            System.out.println("   ↳ Reducing stock for " + product.getName() +
                    " | currentStock=" + currentStock +
                    " | qtyOrdered=" + item.getQuantity());

            if (currentStock < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            product.setStockQuantity(currentStock - item.getQuantity());
            productRepository.save(product);
            System.out.println("   ✅ New stock=" + product.getStockQuantity());
        }
    }
}
