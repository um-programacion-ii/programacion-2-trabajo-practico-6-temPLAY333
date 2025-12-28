package um.prog2.trabajo.businessservice.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.prog2.trabajo.businessservice.client.DataServiceClient;
import um.prog2.trabajo.businessservice.dto.CategoriaDTO;
import um.prog2.trabajo.businessservice.dto.CategoriaRequest;
import um.prog2.trabajo.businessservice.dto.ProductoDTO;
import um.prog2.trabajo.businessservice.exception.CategoriaNoEncontradaException;
import um.prog2.trabajo.businessservice.exception.MicroserviceCommunicationException;
import um.prog2.trabajo.businessservice.exception.ValidacionNegocioException;
import um.prog2.trabajo.dataservice.entity.Categoria;
import um.prog2.trabajo.dataservice.entity.Producto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoriaBusinessService {

    private final DataServiceClient dataServiceClient;

    public CategoriaBusinessService(DataServiceClient dataServiceClient) {
        this.dataServiceClient = dataServiceClient;
    }

    public List<CategoriaDTO> obtenerTodasLasCategorias() {
        try {
            log.info("Obteniendo todas las categorías desde el microservicio de datos");
            List<Categoria> categorias = dataServiceClient.obtenerTodasLasCategorias();
            return categorias.stream()
                    .map(this::convertirACategoriaDTO)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Error al obtener categorías del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public CategoriaDTO obtenerCategoriaPorId(Long id) {
        try {
            log.info("Obteniendo categoría por ID: {}", id);
            Categoria categoria = dataServiceClient.obtenerCategoriaPorId(id);
            return convertirACategoriaDTO(categoria);
        } catch (FeignException.NotFound e) {
            log.error("Categoría no encontrada con ID: {}", id);
            throw new CategoriaNoEncontradaException("Categoría no encontrada con ID: " + id);
        } catch (FeignException e) {
            log.error("Error al obtener categoría del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public CategoriaDTO obtenerCategoriaPorNombre(String nombre) {
        try {
            log.info("Obteniendo categoría por nombre: {}", nombre);
            Categoria categoria = dataServiceClient.obtenerCategoriaPorNombre(nombre);
            return convertirACategoriaDTO(categoria);
        } catch (FeignException.NotFound e) {
            log.error("Categoría no encontrada con nombre: {}", nombre);
            throw new CategoriaNoEncontradaException("Categoría no encontrada con nombre: " + nombre);
        } catch (FeignException e) {
            log.error("Error al obtener categoría del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public CategoriaDTO crearCategoria(CategoriaRequest request) {
        log.info("Creando nueva categoría: {}", request.getNombre());
        
        // Validaciones de negocio
        validarCategoria(request);

        try {
            Categoria categoria = new Categoria();
            categoria.setNombre(request.getNombre());
            categoria.setDescripcion(request.getDescripcion());
            
            Categoria categoriaCreada = dataServiceClient.crearCategoria(categoria);
            return convertirACategoriaDTO(categoriaCreada);
        } catch (FeignException e) {
            log.error("Error al crear categoría en el microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public CategoriaDTO actualizarCategoria(Long id, CategoriaRequest request) {
        log.info("Actualizando categoría con ID: {}", id);
        
        // Validaciones de negocio
        validarCategoria(request);

        try {
            Categoria categoriaExistente = dataServiceClient.obtenerCategoriaPorId(id);
            
            categoriaExistente.setNombre(request.getNombre());
            categoriaExistente.setDescripcion(request.getDescripcion());
            
            Categoria categoriaActualizada = dataServiceClient.actualizarCategoria(id, categoriaExistente);
            return convertirACategoriaDTO(categoriaActualizada);
        } catch (FeignException.NotFound e) {
            log.error("Categoría no encontrada con ID: {}", id);
            throw new CategoriaNoEncontradaException("Categoría no encontrada con ID: " + id);
        } catch (FeignException e) {
            log.error("Error al actualizar categoría en el microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public void eliminarCategoria(Long id) {
        log.info("Eliminando categoría con ID: {}", id);
        
        try {
            // Validar que la categoría no tenga productos antes de eliminar
            Categoria categoria = dataServiceClient.obtenerCategoriaPorId(id);
            if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
                throw new ValidacionNegocioException(
                    "No se puede eliminar la categoría porque tiene productos asociados");
            }
            
            dataServiceClient.eliminarCategoria(id);
        } catch (FeignException.NotFound e) {
            log.error("Categoría no encontrada con ID: {}", id);
            throw new CategoriaNoEncontradaException("Categoría no encontrada con ID: " + id);
        } catch (ValidacionNegocioException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Error al eliminar categoría del microservicio de datos", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    public List<ProductoDTO> obtenerProductosPorCategoria(Long categoriaId) {
        log.info("Obteniendo productos de la categoría con ID: {}", categoriaId);
        
        try {
            Categoria categoria = dataServiceClient.obtenerCategoriaPorId(categoriaId);
            List<Producto> productos = dataServiceClient.obtenerProductosPorCategoria(categoria.getNombre());
            
            return productos.stream()
                    .map(this::convertirProductoADTO)
                    .collect(Collectors.toList());
        } catch (FeignException.NotFound e) {
            log.error("Categoría no encontrada con ID: {}", categoriaId);
            throw new CategoriaNoEncontradaException("Categoría no encontrada con ID: " + categoriaId);
        } catch (FeignException e) {
            log.error("Error al obtener productos de la categoría", e);
            throw new MicroserviceCommunicationException("Error de comunicación con el servicio de datos");
        }
    }

    private void validarCategoria(CategoriaRequest request) {
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new ValidacionNegocioException("El nombre de la categoría es obligatorio");
        }
        
        if (request.getNombre().length() > 100) {
            throw new ValidacionNegocioException("El nombre de la categoría no puede exceder los 100 caracteres");
        }
    }

    private CategoriaDTO convertirACategoriaDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        
        if (categoria.getProductos() != null) {
            dto.setCantidadProductos(categoria.getProductos().size());
            dto.setProductos(categoria.getProductos().stream()
                    .map(this::convertirProductoADTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setCantidadProductos(0);
        }
        
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
