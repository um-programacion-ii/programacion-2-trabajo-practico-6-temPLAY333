package um.prog2.trabajo.businessservice.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.prog2.trabajo.businessservice.client.DataServiceClient;
import um.prog2.trabajo.businessservice.dto.InventarioDTO;
import um.prog2.trabajo.businessservice.dto.ProductoDTO;
import um.prog2.trabajo.businessservice.dto.ReporteInventarioDTO;
import um.prog2.trabajo.businessservice.exception.MicroserviceCommunicationException;
import um.prog2.trabajo.businessservice.exception.ValidacionNegocioException;
import um.prog2.trabajo.dataservice.entity.Inventario;
import um.prog2.trabajo.dataservice.entity.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventarioBusinessService {

    private final DataServiceClient dataServiceClient;

    public InventarioBusinessService(DataServiceClient dataServiceClient) {
        this.dataServiceClient = dataServiceClient;
    }

    public List<InventarioDTO> obtenerTodoElInventario() {
        try {
            log.info("Obteniendo todo el inventario desde el microservicio de datos");
            List<Inventario> inventarios = dataServiceClient.obtenerTodoElInventario();
            return inventarios.stream()
                    .map(this::convertirAInventarioDTO)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error al obtener inventario del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public InventarioDTO obtenerInventarioPorProductoId(Long productoId) {
        try {
            log.info("Obteniendo inventario del producto con ID: {}", productoId);
            Inventario inventario = dataServiceClient.obtenerInventarioPorProductoId(productoId);
            return convertirAInventarioDTO(inventario);
        } catch (FeignException.NotFound e) {
            log.error("Inventario no encontrado para producto ID: {}", productoId);
            throw new ValidacionNegocioException("Inventario no encontrado para el producto con ID: " + productoId);
        } catch (FeignException e) {
            log.error("Error al obtener inventario del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public List<ProductoDTO> obtenerProductosConStockBajo() {
        try {
            log.info("Obteniendo productos con stock bajo");
            List<Inventario> inventarios = dataServiceClient.obtenerProductosConStockBajo();
            return inventarios.stream()
                    .map(inv -> {
                        ProductoDTO dto = convertirProductoADTO(inv.getProducto());
                        dto.setStock(inv.getCantidad());
                        dto.setStockBajo(true);
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error al obtener productos con stock bajo del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public List<ProductoDTO> obtenerProductosSinStock() {
        try {
            log.info("Obteniendo productos sin stock");
            List<Inventario> inventarios = dataServiceClient.obtenerProductosSinStock();
            return inventarios.stream()
                    .map(inv -> {
                        ProductoDTO dto = convertirProductoADTO(inv.getProducto());
                        dto.setStock(0);
                        dto.setStockBajo(true);
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error al obtener productos sin stock del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public InventarioDTO actualizarStock(Long productoId, Integer nuevaCantidad) {
        log.info("Actualizando stock del producto ID: {} a cantidad: {}", productoId, nuevaCantidad);
        
        // Validaciones de negocio
        if (nuevaCantidad < 0) {
            throw new ValidacionNegocioException("La cantidad de stock no puede ser negativa");
        }

        try {
            Inventario inventario = dataServiceClient.obtenerInventarioPorProductoId(productoId);
            Inventario inventarioActualizado = dataServiceClient.actualizarStock(inventario.getId(), nuevaCantidad);
            return convertirAInventarioDTO(inventarioActualizado);
        } catch (FeignException.NotFound e) {
            log.error("Inventario no encontrado para producto ID: {}", productoId);
            throw new ValidacionNegocioException("Inventario no encontrado para el producto con ID: " + productoId);
        } catch (FeignException e) {
            log.error("Error al actualizar stock del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public ReporteInventarioDTO generarReporteInventario() {
        log.info("Generando reporte completo de inventario");
        
        try {
            List<Inventario> inventarios = dataServiceClient.obtenerTodoElInventario();
            List<Inventario> stockBajo = dataServiceClient.obtenerProductosConStockBajo();
            List<Inventario> sinStock = dataServiceClient.obtenerProductosSinStock();
            
            BigDecimal valorTotal = inventarios.stream()
                    .map(inv -> {
                        BigDecimal precio = inv.getProducto().getPrecio();
                        BigDecimal cantidad = BigDecimal.valueOf(inv.getCantidad());
                        return precio.multiply(cantidad);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            ReporteInventarioDTO reporte = new ReporteInventarioDTO();
            reporte.setTotalProductos(inventarios.size());
            reporte.setProductosConStockBajo(stockBajo.size());
            reporte.setProductosSinStock(sinStock.size());
            reporte.setValorTotalInventario(valorTotal);
            
            return reporte;
        } catch (FeignException e) {
            log.error("Error al generar reporte de inventario", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    private InventarioDTO convertirAInventarioDTO(Inventario inventario) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(inventario.getId());
        dto.setCantidad(inventario.getCantidad());
        dto.setStockMinimo(inventario.getStockMinimo());
        dto.setFechaActualizacion(inventario.getFechaActualizacion());
        
        if (inventario.getProducto() != null) {
            dto.setProductoId(inventario.getProducto().getId());
            dto.setProductoNombre(inventario.getProducto().getNombre());
        }
        
        dto.setStockBajo(inventario.getCantidad() <= inventario.getStockMinimo());
        
        return dto;
    }

    private ProductoDTO convertirProductoADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        
        if (producto.getCategoria() != null) {
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        
        if (producto.getInventario() != null) {
            dto.setStock(producto.getInventario().getCantidad());
            dto.setStockBajo(producto.getInventario().getCantidad() <= producto.getInventario().getStockMinimo());
        }
        
        return dto;
    }
}
