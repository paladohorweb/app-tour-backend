package com.jgm.paladohorweb.tour.service;

import com.jgm.paladohorweb.tour.entity.MetodoPago;
import com.jgm.paladohorweb.tour.service.wompi.dto.WompiMerchantResponse;
import com.jgm.paladohorweb.tour.service.wompi.dto.WompiTransactionRequest;
import com.jgm.paladohorweb.tour.service.wompi.dto.WompiTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WompiService {

    private final RestTemplate restTemplate;

    @Value("${wompi.base-url}")
    private String baseUrl;

    @Value("${wompi.public.key}")
    private String publicKey;

    @Value("${wompi.private.key}")
    private String privateKey;

    @Value("${wompi.redirect-url}")
    private String redirectUrl;

    public WompiMerchantResponse obtenerMerchant() {
        String url = baseUrl + "/merchants/" + publicKey;
        return restTemplate.getForObject(url, WompiMerchantResponse.class);
    }

    public WompiTransactionResponse crearTransaccion(
            Long reservaId,
            Long amountInCents,
            String email,
            MetodoPago metodoPago
    ) {
        WompiMerchantResponse merchant = obtenerMerchant();

        String acceptanceToken = merchant.getData().getAcceptance_token().getAcceptance_token();
        String personalDataToken = merchant.getData().getPresigned_personal_data_auth().getAcceptance_token();

        String paymentMethodType = switch (metodoPago) {
            case TARJETA -> "CARD";
            case PSE -> "PSE";
            case NEQUI -> "NEQUI";
            default -> "CARD";
        };

        String reference = "RES-" + reservaId + "-" + System.currentTimeMillis();

        WompiTransactionRequest request = WompiTransactionRequest.builder()
                .amount_in_cents(amountInCents)
                .currency("COP")
                .customer_email(email)
                .payment_method_type(paymentMethodType)
                .reference(reference)
                .redirect_url(redirectUrl + "/order/success?reservaId=" + reservaId)
                .acceptance_token(acceptanceToken)
                .accept_personal_auth(personalDataToken)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(privateKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WompiTransactionRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<WompiTransactionResponse> response = restTemplate.exchange(
                baseUrl + "/transactions",
                HttpMethod.POST,
                entity,
                WompiTransactionResponse.class
        );

        return response.getBody();
    }
}