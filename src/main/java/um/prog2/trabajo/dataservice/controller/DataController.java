package um.prog2.trabajo.dataservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import um.prog2.trabajo.dataservice.entity.Categoria;
import um.prog2.trabajo.dataservice.entity.Inventario;
import um.prog2.trabajo.dataservice.entity.Producto;
import um.prog2.trabajo.dataservice.service.CategoriaService;
import um.prog2.trabajo.dataservice.service.InventarioService;
import um.prog2.trabajo.dataservice.service.ProductoService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/data")
@Validated
@Slf4j
public class DataController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final InventarioService inventarioService;

    public DataController(ProductoService productoService,
                         CategoriaService categoriaService,
                         InventarioService inventarioService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.inventarioService = inventarioService;
    }

    // ========== ENDPOINTS DE PRODUCTOS ==========
    
    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        log.info("GET /data/productos - Obteniendo todos los productos");
        List<Producto> productos = productoService.obtenerTodos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        log.info("GET /data/productos/{} - Obteniendo producto por ID", id);
        Producto producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto) {
        log.info("POST /data/productos - Creando nuevo producto: {}", producto.getNombre());
        Producto nuevoProducto = productoService.guardar(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, 
                                                       @Valid @RequestBody Producto producto) {
        log.info("PUT /data/productos/{} - Actualizando producto", id);
        Producto productoActualizado = productoService.actualizar(id, producto);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        log.info("DELETE /data/productos/{} - Eliminando producto", id);
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/productos/categoria/{nombre}")
    public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@PathVariable String nombre) {
        log.info("GET /data/productos/categoria/{} - Obteniendo productos por categoría", nombre);
        List<Producto> productos = productoService.buscarPorCategoria(nombre);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/productos/buscar")
    public ResponseEntity<List<Producto>> buscarProductosPorNombre(@RequestParam String nombre) {
        log.info("GET /data/productos/buscar?nombre={} - Buscando productos por nombre", nombre);
        List<Producto> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/productos/precio")
    public ResponseEntity<List<Producto>> buscarProductosPorRangoPrecio(
            @RequestParam BigDecimal min, 
            @RequestParam BigDecimal max) {
        log.info("GET /data/productos/precio?min={}&max={} - Buscando productos por rango de precio", min, max);
        List<Producto> productos = productoService.buscarPorRangoPrecio(min, max);
        return ResponseEntity.ok(productos);
    }

    // ========== ENDPOINTS DE CATEGORÍAS ==========

    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> obtenerTodasLasCategorias() {
        log.info("GET /data/categorias - Obteniendo todas las categorías");
        List<Categoria> categorias = categoriaService.obtenerTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Long id) {
        log.info("GET /data/categorias/{} - Obteniendo categoría por ID", id);
        Categoria categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/categorias/nombre/{nombre}")
    public ResponseEntity<Categoria> obtenerCategoriaPorNombre(@PathVariable String nombre) {
        log.info("GET /data/categorias/nombre/{} - Obteniendo categoría por nombre", nombre);
        Categoria categoria = categoriaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping("/categorias")
    public ResponseEntity<Categoria> crearCategoria(@Valid @RequestBody Categoria categoria) {
        log.info("POST /data/categorias - Creando nueva categoría: {}", categoria.getNombre());
        Categoria nuevaCategoria = categoriaService.guardar(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable Long id, 
                                                         @Valid @RequestBody Categoria categoria) {
        log.info("PUT /data/categorias/{} - Actualizando categoría", id);
        Categoria categoriaActualizada = categoriaService.actualizar(id, categoria);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        log.info("DELETE /data/categorias/{} - Eliminando categoría", id);
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ========== ENDPOINTS DE INVENTARIO ==========

    @GetMapping("/inventario")
    public ResponseEntity<List<Inventario>> obtenerTodoElInventario() {
        log.info("GET /data/inventario - Obteniendo todo el inventario");
        List<Inventario> inventarios = inventarioService.obtenerTodos();
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/inventario/{id}")
    public ResponseEntity<Inventario> obtenerInventarioPorId(@PathVariable Long id) {
        log.info("GET /data/inventario/{} - Obteniendo inventario por ID", id);
        Inventario inventario = inventarioService.buscarPorId(id);
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/inventario/producto/{productoId}")
    public ResponseEntity<Inventario> obtenerInventarioPorProductoId(@PathVariable Long productoId) {
        log.info("GET /data/inventario/producto/{} - Obteniendo inventario por producto ID", productoId);
        Inventario inventario = inventarioService.buscarPorProductoId(productoId);
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/inventario/stock-bajo")
    public ResponseEntity<List<Inventario>> obtenerProductosConStockBajo() {
        log.info("GET /data/inventario/stock-bajo - Obteniendo productos con stock bajo");
        List<Inventario> inventarios = inventarioService.obtenerProductosConStockBajo();
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/inventario/sin-stock")
    public ResponseEntity<List<Inventario>> obtenerProductosSinStock() {
        log.info("GET /data/inventario/sin-stock - Obteniendo productos sin stock");
        List<Inventario> inventarios = inventarioService.obtenerProductosSinStock();
        return ResponseEntity.ok(inventarios);
    }

    @PostMapping("/inventario")
    public ResponseEntity<Inventario> crearInventario(@Valid @RequestBody Inventario inventario) {
        log.info("POST /data/inventario - Creando nuevo inventario");
        Inventario nuevoInventario = inventarioService.guardar(inventario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInventario);
    }

    @PutMapping("/inventario/{id}")
    public ResponseEntity<Inventario> actualizarInventario(@PathVariable Long id, 
                                                           @Valid @RequestBody Inventario inventario) {
        log.info("PUT /data/inventario/{} - Actualizando inventario", id);
        Inventario inventarioActualizado = inventarioService.actualizar(id, inventario);
        return ResponseEntity.ok(inventarioActualizado);
    }

    @PutMapping("/inventario/{id}/stock")
    public ResponseEntity<Inventario> actualizarStock(@PathVariable Long id, 
                                                      @RequestParam Integer cantidad) {
        log.info("PUT /data/inventario/{}/stock?cantidad={} - Actualizando stock", id, cantidad);
        Inventario inventarioActualizado = inventarioService.actualizarStock(id, cantidad);
        return ResponseEntity.ok(inventarioActualizado);
    }

    @DeleteMapping("/inventario/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable Long id) {
        log.info("DELETE /data/inventario/{} - Eliminando inventario", id);
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
