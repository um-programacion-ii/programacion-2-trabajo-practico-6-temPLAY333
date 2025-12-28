package um.prog2.trabajo.dataservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import um.prog2.trabajo.dataservice.entity.Inventario;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProductoId(Long productoId);
    
    @Query("SELECT i FROM Inventario i WHERE i.cantidad <= i.stockMinimo")
    List<Inventario> findProductosConStockBajo();
    
    @Query("SELECT i FROM Inventario i WHERE i.cantidad = 0")
    List<Inventario> findProductosSinStock();
}
