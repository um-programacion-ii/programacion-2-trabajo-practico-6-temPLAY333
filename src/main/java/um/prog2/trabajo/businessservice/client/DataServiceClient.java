package um.prog2.trabajo.businessservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.prog2.trabajo.dataservice.entity.Categoria;
import um.prog2.trabajo.dataservice.entity.Inventario;
import um.prog2.trabajo.dataservice.entity.Producto;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "data-service", url = "${data.service.url}")
public interface DataServiceClient {

    // ========== ENDPOINTS DE PRODUCTOS ==========
    
    @GetMapping("/data/productos")
    List<Producto> obtenerTodosLosProductos();

    @GetMapping("/data/productos/{id}")
    Producto obtenerProductoPorId(@PathVariable Long id);

    @PostMapping("/data/productos")
    Producto crearProducto(@RequestBody Producto producto);

    @PutMapping("/data/productos/{id}")
    Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto producto);

    @DeleteMapping("/data/productos/{id}")
    void eliminarProducto(@PathVariable Long id);

    @GetMapping("/data/productos/categoria/{nombre}")
    List<Producto> obtenerProductosPorCategoria(@PathVariable String nombre);

    @GetMapping("/data/productos/buscar")
    List<Producto> buscarProductosPorNombre(@RequestParam String nombre);

    @GetMapping("/data/productos/precio")
    List<Producto> buscarProductosPorRangoPrecio(@RequestParam BigDecimal min, @RequestParam BigDecimal max);

    // ========== ENDPOINTS DE CATEGORÍAS ==========

    @GetMapping("/data/categorias")
    List<Categoria> obtenerTodasLasCategorias();

    @GetMapping("/data/categorias/{id}")
    Categoria obtenerCategoriaPorId(@PathVariable Long id);

    @GetMapping("/data/categorias/nombre/{nombre}")
    Categoria obtenerCategoriaPorNombre(@PathVariable String nombre);

    @PostMapping("/data/categorias")
    Categoria crearCategoria(@RequestBody Categoria categoria);

    @PutMapping("/data/categorias/{id}")
    Categoria actualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoria);

    @DeleteMapping("/data/categorias/{id}")
    void eliminarCategoria(@PathVariable Long id);

    // ========== ENDPOINTS DE INVENTARIO ==========

    @GetMapping("/data/inventario")
    List<Inventario> obtenerTodoElInventario();

    @GetMapping("/data/inventario/{id}")
    Inventario obtenerInventarioPorId(@PathVariable Long id);

    @GetMapping("/data/inventario/producto/{productoId}")
    Inventario obtenerInventarioPorProductoId(@PathVariable Long productoId);

    @GetMapping("/data/inventario/stock-bajo")
    List<Inventario> obtenerProductosConStockBajo();

    @GetMapping("/data/inventario/sin-stock")
    List<Inventario> obtenerProductosSinStock();

    @PostMapping("/data/inventario")
    Inventario crearInventario(@RequestBody Inventario inventario);

    @PutMapping("/data/inventario/{id}")
    Inventario actualizarInventario(@PathVariable Long id, @RequestBody Inventario inventario);

    @PutMapping("/data/inventario/{id}/stock")
    Inventario actualizarStock(@PathVariable Long id, @RequestParam Integer cantidad);

    @DeleteMapping("/data/inventario/{id}")
    void eliminarInventario(@PathVariable Long id);
}
