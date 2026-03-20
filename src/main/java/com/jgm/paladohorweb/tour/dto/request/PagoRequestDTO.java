
package com.jgm.paladohorweb.tour.dto.request;

import com.jgm.paladohorweb.tour.entity.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO{

        @NotNull
         private Long reservaId;

        @NotNull
        private MetodoPago metodoPago;



}


////        /**
//         * Monto en la moneda mínima:
//         * - COP: 2000 = $2.000 COP (zero-decimal)
//         * - USD: 2000 = $20.00 USD (centavos)
//         */
//        @NotNull
//        @Positive
//        private Long monto;
//}
