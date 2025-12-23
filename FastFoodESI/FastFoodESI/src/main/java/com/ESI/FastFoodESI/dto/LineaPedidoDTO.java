package com.ESI.FastFoodESI.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


public record LineaPedidoDTO(
    
    @NotNull(message = "El ID del producto es obligatorio")
    UUID productoId,
    
    @Min(value = 1, message = "La cantidad mínima es 1")
    @NotNull(message = "La cantidad es obligatoria")
    Integer cantidad
) {

}
