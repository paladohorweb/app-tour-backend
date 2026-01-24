package com.jgm.paladohorweb.tour.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeKey;

    @PostConstruct
    void init() {
        Stripe.apiKey = stripeKey;
    }

    public PaymentIntent crearPaymentIntent(BigDecimal monto) throws StripeException {

        Map<String, Object> params = new HashMap<>();
        params.put("amount", monto.multiply(BigDecimal.valueOf(100)).longValue());
        params.put("currency", "usd");
        params.put("payment_method_types", List.of("card"));

        return PaymentIntent.create(params);
    }
}
