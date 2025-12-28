package um.prog2.trabajo.dataservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.prog2.trabajo.dataservice.entity.Producto;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaNombre(String categoriaNombre);
    
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :minPrecio AND :maxPrecio")
    List<Producto> findByPrecioBetween(@Param("minPrecio") BigDecimal minPrecio, 
                                       @Param("maxPrecio") BigDecimal maxPrecio);
    
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
