package com.jgm.paladohorweb.tour.service.wompi.dto;

import lombok.Data;

@Data
public class WompiTransactionResponse {
    private TxData data;

    @Data
    public static class TxData {
        private String id;
        private String status;
        private String reference;
        private String redirect_url;
        private String payment_method_type;
    }
}