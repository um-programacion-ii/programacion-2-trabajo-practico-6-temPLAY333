package um.prog2.trabajo.businessservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import um.prog2.trabajo.businessservice.dto.*;
import um.prog2.trabajo.businessservice.service.CategoriaBusinessService;
import um.prog2.trabajo.businessservice.service.InventarioBusinessService;
import um.prog2.trabajo.businessservice.service.ProductoBusinessService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@Slf4j
public class BusinessController {

    private final ProductoBusinessService productoBusinessService;
    private final CategoriaBusinessService categoriaBusinessService;
    private final InventarioBusinessService inventarioBusinessService;

    public BusinessController(ProductoBusinessService productoBusinessService,
                             CategoriaBusinessService categoriaBusinessService,
                             InventarioBusinessService inventarioBusinessService) {
        this.productoBusinessService = productoBusinessService;
        this.categoriaBusinessService = categoriaBusinessService;
        this.inventarioBusinessService = inventarioBusinessService;
    }

    // ========== ENDPOINTS DE PRODUCTOS ==========

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductos() {
        log.info("GET /api/productos - Obteniendo todos los productos");
        List<ProductoDTO> productos = productoBusinessService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(@PathVariable Long id) {
        log.info("GET /api/productos/{} - Obteniendo producto por ID", id);
        ProductoDTO producto = productoBusinessService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoRequest request) {
        log.info("POST /api/productos - Creando nuevo producto: {}", request.getNombre());
        ProductoDTO producto = productoBusinessService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id,
                                                          @Valid @RequestBody ProductoRequest request) {
        log.info("PUT /api/productos/{} - Actualizando producto", id);
        ProductoDTO producto = productoBusinessService.actualizarProducto(id, request);
        return ResponseEntity.ok(producto);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        log.info("DELETE /api/productos/{} - Eliminando producto", id);
        productoBusinessService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/productos/categoria/{nombre}")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorCategoria(@PathVariable String nombre) {
        log.info("GET /api/productos/categoria/{} - Obteniendo productos por categoría", nombre);
        List<ProductoDTO> productos = productoBusinessService.obtenerProductosPorCategoria(nombre);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/productos/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarProductosPorNombre(@RequestParam String nombre) {
        log.info("GET /api/productos/buscar?nombre={} - Buscando productos por nombre", nombre);
        List<ProductoDTO> productos = productoBusinessService.buscarProductosPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/productos/precio")
    public ResponseEntity<List<ProductoDTO>> buscarProductosPorRangoPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        log.info("GET /api/productos/precio?min={}&max={} - Buscando productos por rango de precio", min, max);
        List<ProductoDTO> productos = productoBusinessService.buscarProductosPorRangoPrecio(min, max);
        return ResponseEntity.ok(productos);
    }

    // ========== ENDPOINTS DE CATEGORÍAS ==========

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaDTO>> obtenerTodasLasCategorias() {
        log.info("GET /api/categorias - Obteniendo todas las categorías");
        List<CategoriaDTO> categorias = categoriaBusinessService.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<CategoriaDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        log.info("GET /api/categorias/{} - Obteniendo categoría por ID", id);
        CategoriaDTO categoria = categoriaBusinessService.obtenerCategoriaPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/categorias/nombre/{nombre}")
    public ResponseEntity<CategoriaDTO> obtenerCategoriaPorNombre(@PathVariable String nombre) {
        log.info("GET /api/categorias/nombre/{} - Obteniendo categoría por nombre", nombre);
        CategoriaDTO categoria = categoriaBusinessService.obtenerCategoriaPorNombre(nombre);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaDTO> crearCategoria(@Valid @RequestBody CategoriaRequest request) {
        log.info("POST /api/categorias - Creando nueva categoría: {}", request.getNombre());
        CategoriaDTO categoria = categoriaBusinessService.crearCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable Long id,
                                                            @Valid @RequestBody CategoriaRequest request) {
        log.info("PUT /api/categorias/{} - Actualizando categoría", id);
        CategoriaDTO categoria = categoriaBusinessService.actualizarCategoria(id, request);
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        log.info("DELETE /api/categorias/{} - Eliminando categoría", id);
        categoriaBusinessService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categorias/{id}/productos")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorCategoria(@PathVariable Long id) {
        log.info("GET /api/categorias/{}/productos - Obteniendo productos de la categoría", id);
        List<ProductoDTO> productos = categoriaBusinessService.obtenerProductosPorCategoria(id);
        return ResponseEntity.ok(productos);
    }

    // ========== ENDPOINTS DE INVENTARIO ==========

    @GetMapping("/inventario")
    public ResponseEntity<List<InventarioDTO>> obtenerTodoElInventario() {
        log.info("GET /api/inventario - Obteniendo todo el inventario");
        List<InventarioDTO> inventarios = inventarioBusinessService.obtenerTodoElInventario();
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/inventario/producto/{productoId}")
    public ResponseEntity<InventarioDTO> obtenerInventarioPorProductoId(@PathVariable Long productoId) {
        log.info("GET /api/inventario/producto/{} - Obteniendo inventario del producto", productoId);
        InventarioDTO inventario = inventarioBusinessService.obtenerInventarioPorProductoId(productoId);
        return ResponseEntity.ok(inventario);
    }

    @PutMapping("/inventario/producto/{productoId}/stock")
    public ResponseEntity<InventarioDTO> actualizarStock(@PathVariable Long productoId,
                                                         @RequestParam Integer cantidad) {
        log.info("PUT /api/inventario/producto/{}/stock?cantidad={} - Actualizando stock", productoId, cantidad);
        InventarioDTO inventario = inventarioBusinessService.actualizarStock(productoId, cantidad);
        return ResponseEntity.ok(inventario);
    }

    // ========== ENDPOINTS DE REPORTES ==========

    @GetMapping("/reportes/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosConStockBajo() {
        log.info("GET /api/reportes/stock-bajo - Obteniendo productos con stock bajo");
        List<ProductoDTO> productos = productoBusinessService.obtenerProductosConStockBajo();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/reportes/sin-stock")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosSinStock() {
        log.info("GET /api/reportes/sin-stock - Obteniendo productos sin stock");
        List<ProductoDTO> productos = inventarioBusinessService.obtenerProductosSinStock();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/reportes/valor-inventario")
    public ResponseEntity<BigDecimal> obtenerValorTotalInventario() {
        log.info("GET /api/reportes/valor-inventario - Calculando valor total del inventario");
        BigDecimal valorTotal = productoBusinessService.calcularValorTotalInventario();
        return ResponseEntity.ok(valorTotal);
    }

    @GetMapping("/reportes/inventario-completo")
    public ResponseEntity<ReporteInventarioDTO> generarReporteInventario() {
        log.info("GET /api/reportes/inventario-completo - Generando reporte completo de inventario");
        ReporteInventarioDTO reporte = inventarioBusinessService.generarReporteInventario();
        return ResponseEntity.ok(reporte);
    }
}
