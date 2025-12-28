package um.prog2.trabajo.dataservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.prog2.trabajo.dataservice.entity.Inventario;
import um.prog2.trabajo.dataservice.exception.RecursoNoEncontradoException;
import um.prog2.trabajo.dataservice.repository.InventarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class InventarioService {
    
    private final InventarioRepository inventarioRepository;
    
    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Inventario> obtenerTodos() {
        log.info("Obteniendo todo el inventario");
        return inventarioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Inventario buscarPorId(Long id) {
        log.info("Buscando inventario por ID: {}", id);
        return inventarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado con ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Inventario buscarPorProductoId(Long productoId) {
        log.info("Buscando inventario por producto ID: {}", productoId);
        return inventarioRepository.findByProductoId(productoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado para producto ID: " + productoId));
    }
    
    @Transactional(readOnly = true)
    public List<Inventario> obtenerProductosConStockBajo() {
        log.info("Obteniendo productos con stock bajo");
        return inventarioRepository.findProductosConStockBajo();
    }
    
    @Transactional(readOnly = true)
    public List<Inventario> obtenerProductosSinStock() {
        log.info("Obteniendo productos sin stock");
        return inventarioRepository.findProductosSinStock();
    }
    
    public Inventario guardar(Inventario inventario) {
        log.info("Guardando nuevo inventario para producto ID: {}", inventario.getProducto().getId());
        inventario.setFechaActualizacion(LocalDateTime.now());
        return inventarioRepository.save(inventario);
    }
    
    public Inventario actualizarStock(Long id, Integer nuevaCantidad) {
        log.info("Actualizando stock del inventario ID: {} a cantidad: {}", id, nuevaCantidad);
        Inventario inventario = buscarPorId(id);
        
        inventario.setCantidad(nuevaCantidad);
        inventario.setFechaActualizacion(LocalDateTime.now());
        
        return inventarioRepository.save(inventario);
    }
    
    public Inventario actualizar(Long id, Inventario inventarioActualizado) {
        log.info("Actualizando inventario con ID: {}", id);
        Inventario inventarioExistente = buscarPorId(id);
        
        inventarioExistente.setCantidad(inventarioActualizado.getCantidad());
        inventarioExistente.setStockMinimo(inventarioActualizado.getStockMinimo());
        inventarioExistente.setFechaActualizacion(LocalDateTime.now());
        
        return inventarioRepository.save(inventarioExistente);
    }
    
    public void eliminar(Long id) {
        log.info("Eliminando inventario con ID: {}", id);
        Inventario inventario = buscarPorId(id);
        inventarioRepository.delete(inventario);
    }
}
