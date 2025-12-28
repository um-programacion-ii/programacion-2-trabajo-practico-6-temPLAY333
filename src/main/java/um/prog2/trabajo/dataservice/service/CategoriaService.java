package um.prog2.trabajo.dataservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.prog2.trabajo.dataservice.entity.Categoria;
import um.prog2.trabajo.dataservice.exception.RecursoNoEncontradoException;
import um.prog2.trabajo.dataservice.repository.CategoriaRepository;

import java.util.List;

@Service
@Slf4j
@Transactional
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodas() {
        log.info("Obteniendo todas las categorías");
        return categoriaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        log.info("Buscando categoría por ID: {}", id);
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Categoria buscarPorNombre(String nombre) {
        log.info("Buscando categoría por nombre: {}", nombre);
        return categoriaRepository.findByNombre(nombre)
            .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con nombre: " + nombre));
    }
    
    public Categoria guardar(Categoria categoria) {
        log.info("Guardando nueva categoría: {}", categoria.getNombre());
        return categoriaRepository.save(categoria);
    }
    
    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        log.info("Actualizando categoría con ID: {}", id);
        Categoria categoriaExistente = buscarPorId(id);
        
        categoriaExistente.setNombre(categoriaActualizada.getNombre());
        categoriaExistente.setDescripcion(categoriaActualizada.getDescripcion());
        
        return categoriaRepository.save(categoriaExistente);
    }
    
    public void eliminar(Long id) {
        log.info("Eliminando categoría con ID: {}", id);
        Categoria categoria = buscarPorId(id);
        categoriaRepository.delete(categoria);
    }
}
