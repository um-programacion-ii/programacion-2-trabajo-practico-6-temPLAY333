package um.prog2.trabajo.dataservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.prog2.trabajo.dataservice.entity.Producto;
import um.prog2.trabajo.dataservice.exception.RecursoNoEncontradoException;
import um.prog2.trabajo.dataservice.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodos() {
        log.info("Obteniendo todos los productos");
        return productoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        log.info("Buscando producto por ID: {}", id);
        return productoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String categoriaNombre) {
        log.info("Buscando productos por categoría: {}", categoriaNombre);
        return productoRepository.findByCategoriaNombre(categoriaNombre);
    }
    
    @Transactional(readOnly = true)
    public List<Producto> buscarPorRangoPrecio(BigDecimal minPrecio, BigDecimal maxPrecio) {
        log.info("Buscando productos por rango de precio: {} - {}", minPrecio, maxPrecio);
        return productoRepository.findByPrecioBetween(minPrecio, maxPrecio);
    }
    
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        log.info("Buscando productos por nombre: {}", nombre);
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public Producto guardar(Producto producto) {
        log.info("Guardando nuevo producto: {}", producto.getNombre());
        return productoRepository.save(producto);
    }
    
    public Producto actualizar(Long id, Producto productoActualizado) {
        log.info("Actualizando producto con ID: {}", id);
        Producto productoExistente = buscarPorId(id);
        
        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setPrecio(productoActualizado.getPrecio());
        productoExistente.setCategoria(productoActualizado.getCategoria());
        
        return productoRepository.save(productoExistente);
    }
    
    public void eliminar(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        Producto producto = buscarPorId(id);
        productoRepository.delete(producto);
    }
}
