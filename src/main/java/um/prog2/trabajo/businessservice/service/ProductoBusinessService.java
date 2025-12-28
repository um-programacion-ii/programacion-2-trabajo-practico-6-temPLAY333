package um.prog2.trabajo.businessservice.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.prog2.trabajo.businessservice.client.DataServiceClient;
import um.prog2.trabajo.businessservice.dto.ProductoDTO;
import um.prog2.trabajo.businessservice.dto.ProductoRequest;
import um.prog2.trabajo.businessservice.exception.MicroserviceCommunicationException;
import um.prog2.trabajo.businessservice.exception.ProductoNoEncontradoException;
import um.prog2.trabajo.businessservice.exception.ValidacionNegocioException;
import um.prog2.trabajo.dataservice.entity.Categoria;
import um.prog2.trabajo.dataservice.entity.Inventario;
import um.prog2.trabajo.dataservice.entity.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductoBusinessService {

    private final DataServiceClient dataServiceClient;

    public ProductoBusinessService(DataServiceClient dataServiceClient) {
        this.dataServiceClient = dataServiceClient;
    }

    public List<ProductoDTO> obtenerTodosLosProductos() {
        try {
            log.info("Obteniendo todos los productos desde el microservicio de datos");
            List<Producto> productos = dataServiceClient.obtenerTodosLosProductos();
            return productos.stream()
                    .map(this::convertirAProductoDTO)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error al obtener productos del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public ProductoDTO obtenerProductoPorId(Long id) {
        try {
            log.info("Obteniendo producto por ID: {}", id);
            Producto producto = dataServiceClient.obtenerProductoPorId(id);
            return convertirAProductoDTO(producto);
        } catch (FeignException.NotFound e) {
            log.error("Producto no encontrado con ID: {}", id);
            throw new ProductoNoEncontradoException("Producto no encontrado con ID: " + id);
        } catch (FeignException e) {
            log.error("Error al obtener producto del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public ProductoDTO crearProducto(ProductoRequest request) {
        log.info("Creando nuevo producto: {}", request.getNombre());
        
        // Validaciones de negocio
        validarProducto(request);

        try {
            // Obtener la categoría
            Categoria categoria = dataServiceClient.obtenerCategoriaPorId(request.getCategoriaId());
            
            // Crear el producto
            Producto producto = new Producto();
            producto.setNombre(request.getNombre());
            producto.setDescripcion(request.getDescripcion());
            producto.setPrecio(request.getPrecio());
            producto.setCategoria(categoria);
            
            Producto productoCreado = dataServiceClient.crearProducto(producto);
            
            // Crear el inventario asociado
            Inventario inventario = new Inventario();
            inventario.setProducto(productoCreado);
            inventario.setCantidad(request.getStock());
            inventario.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : 10);
            inventario.setFechaActualizacion(LocalDateTime.now());
            
            dataServiceClient.crearInventario(inventario);
            
            return convertirAProductoDTO(productoCreado);
        } catch (FeignException e) {
            log.error("Error al crear producto en el microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public ProductoDTO actualizarProducto(Long id, ProductoRequest request) {
        log.info("Actualizando producto con ID: {}", id);
        
        // Validaciones de negocio
        validarProducto(request);

        try {
            // Verificar que el producto existe
            Producto productoExistente = dataServiceClient.obtenerProductoPorId(id);
            
            // Obtener la categoría
            Categoria categoria = dataServiceClient.obtenerCategoriaPorId(request.getCategoriaId());
            
            // Actualizar el producto
            productoExistente.setNombre(request.getNombre());
            productoExistente.setDescripcion(request.getDescripcion());
            productoExistente.setPrecio(request.getPrecio());
            productoExistente.setCategoria(categoria);
            
            Producto productoActualizado = dataServiceClient.actualizarProducto(id, productoExistente);
            
            // Actualizar el inventario si se proporcionó stock
            if (request.getStock() != null) {
                try {
                    Inventario inventario = dataServiceClient.obtenerInventarioPorProductoId(id);
                    inventario.setCantidad(request.getStock());
                    if (request.getStockMinimo() != null) {
                        inventario.setStockMinimo(request.getStockMinimo());
                    }
                    dataServiceClient.actualizarInventario(inventario.getId(), inventario);
                } catch (FeignException.NotFound e) {
                    // Si no existe inventario, crear uno nuevo
                    Inventario nuevoInventario = new Inventario();
                    nuevoInventario.setProducto(productoActualizado);
                    nuevoInventario.setCantidad(request.getStock());
                    nuevoInventario.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : 10);
                    nuevoInventario.setFechaActualizacion(LocalDateTime.now());
                    dataServiceClient.crearInventario(nuevoInventario);
                }
            }
            
            return convertirAProductoDTO(productoActualizado);
        } catch (FeignException.NotFound e) {
            log.error("Producto o categoría no encontrada");
            throw new ProductoNoEncontradoException("Producto o categoría no encontrada");
        } catch (FeignException e) {
            log.error("Error al actualizar producto en el microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public void eliminarProducto(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        try {
            dataServiceClient.eliminarProducto(id);
        } catch (FeignException.NotFound e) {
            log.error("Producto no encontrado con ID: {}", id);
            throw new ProductoNoEncontradoException("Producto no encontrado con ID: " + id);
        } catch (FeignException e) {
            log.error("Error al eliminar producto del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public List<ProductoDTO> obtenerProductosPorCategoria(String categoriaNombre) {
        log.info("Obteniendo productos por categoría: {}", categoriaNombre);
        try {
            List<Producto> productos = dataServiceClient.obtenerProductosPorCategoria(categoriaNombre);
            return productos.stream()
                    .map(this::convertirAProductoDTO)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error al obtener productos por categoría del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public List<ProductoDTO> buscarProductosPorNombre(String nombre) {
        log.info("Buscando productos por nombre: {}", nombre);
        try {
            List<Producto> productos = dataServiceClient.buscarProductosPorNombre(nombre);
            return productos.stream()
                    .map(this::convertirAProductoDTO)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error al buscar productos por nombre del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public List<ProductoDTO> buscarProductosPorRangoPrecio(BigDecimal min, BigDecimal max) {
        log.info("Buscando productos por rango de precio: {} - {}", min, max);
        
        // Validación de negocio
        if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacionNegocioException("Los precios no pueden ser negativos");
        }
        if (min.compareTo(max) > 0) {
            throw new ValidacionNegocioException("El precio mínimo no puede ser mayor al precio máximo");
        }
        
        try {
            List<Producto> productos = dataServiceClient.buscarProductosPorRangoPrecio(min, max);
            return productos.stream()
                    .map(this::convertirAProductoDTO)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error al buscar productos por rango de precio del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public List<ProductoDTO> obtenerProductosConStockBajo() {
        log.info("Obteniendo productos con stock bajo");
        try {
            List<Inventario> inventarios = dataServiceClient.obtenerProductosConStockBajo();
            return inventarios.stream()
                    .map(inventario -> {
                        ProductoDTO dto = convertirAProductoDTO(inventario.getProducto());
                        dto.setStock(inventario.getCantidad());
                        dto.setStockBajo(true);
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error al obtener productos con stock bajo del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public BigDecimal calcularValorTotalInventario() {
        log.info("Calculando valor total del inventario");
        try {
            List<Inventario> inventarios = dataServiceClient.obtenerTodoElInventario();
            return inventarios.stream()
                    .map(inv -> {
                        BigDecimal precio = inv.getProducto().getPrecio();
                        BigDecimal cantidad = BigDecimal.valueOf(inv.getCantidad());
                        return precio.multiply(cantidad);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (FeignException e) {
            log.error("Error al calcular valor total del inventario", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    private void validarProducto(ProductoRequest request) {
        if (request.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionNegocioException("El precio debe ser mayor a cero");
        }

        if (request.getStock() != null && request.getStock() < 0) {
            throw new ValidacionNegocioException("El stock no puede ser negativo");
        }

        if (request.getStockMinimo() != null && request.getStockMinimo() < 0) {
            throw new ValidacionNegocioException("El stock mínimo no puede ser negativo");
        }
    }

    private ProductoDTO convertirAProductoDTO(Producto producto) {
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
