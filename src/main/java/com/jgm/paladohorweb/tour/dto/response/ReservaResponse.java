package com.jgm.paladohorweb.tour.dto.response;

import java.math.BigDecimal;

public record ReservaResponse(


        Long id,
        //String tour,
        String tourNombre,
        BigDecimal monto,
        String estado
) {}
