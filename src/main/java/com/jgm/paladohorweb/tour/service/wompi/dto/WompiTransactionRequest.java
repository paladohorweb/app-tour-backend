package com.jgm.paladohorweb.tour.service.wompi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WompiTransactionRequest {
    private Long amount_in_cents;
    private String currency;
    private String customer_email;
    private String payment_method_type;
    private String reference;
    private String redirect_url;
    private String acceptance_token;
    private String accept_personal_auth;
}