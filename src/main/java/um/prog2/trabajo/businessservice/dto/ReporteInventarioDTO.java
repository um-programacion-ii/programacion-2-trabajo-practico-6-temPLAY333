package um.prog2.trabajo.businessservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteInventarioDTO {
    private Integer totalProductos;
    private Integer productosConStockBajo;
    private Integer productosSinStock;
    private BigDecimal valorTotalInventario;
}
