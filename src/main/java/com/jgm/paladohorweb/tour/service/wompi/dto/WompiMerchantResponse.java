package com.jgm.paladohorweb.tour.service.wompi.dto;

import lombok.Data;

@Data
public class WompiMerchantResponse {
    private MerchantData data;

    @Data
    public static class MerchantData {
        private PresignedAcceptance acceptance_token;
        private PresignedAcceptance presigned_personal_data_auth;
    }

    @Data
    public static class PresignedAcceptance {
        private String acceptance_token;
        private String permalink;
        private String type;
    }
}