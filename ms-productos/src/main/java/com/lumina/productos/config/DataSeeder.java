package com.lumina.productos.config;

import com.lumina.productos.entity.Marca;
import com.lumina.productos.entity.Producto;
import com.lumina.productos.repository.MarcaRepository;
import com.lumina.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Siembra datos de ejemplo (marcas y productos) SOLO si las tablas
 * estan vacias. Reemplaza a data.sql: al chequear el conteo en Java
 * en vez de correr un script SQL plano, evitamos errores de sintaxis
 * por codificacion/comillas y el problema de duplicados al reiniciar
 * el contenedor con el volumen de MySQL ya poblado.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final MarcaRepository marcaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (marcaRepository.count() > 0) {
            log.info("Ya existen marcas en la base de datos, se omite el seed.");
            return;
        }

        Map<String, Marca> marcas = Map.of(
                "Samsung", marcaRepository.save(Marca.builder().nombre("Samsung").logoUrl("https://placehold.co/200x100?text=Samsung").build()),
                "Logitech", marcaRepository.save(Marca.builder().nombre("Logitech").logoUrl("https://placehold.co/200x100?text=Logitech").build()),
                "Lenovo", marcaRepository.save(Marca.builder().nombre("Lenovo").logoUrl("https://placehold.co/200x100?text=Lenovo").build()),
                "Sony", marcaRepository.save(Marca.builder().nombre("Sony").logoUrl("https://placehold.co/200x100?text=Sony").build()),
                "Kingston", marcaRepository.save(Marca.builder().nombre("Kingston").logoUrl("https://placehold.co/200x100?text=Kingston").build())
        );

        List<Producto> productos = List.of(
                producto("Monitor Samsung 24 pulgadas", "Monitor Full HD de 24 pulgadas para trabajo y entretenimiento.", 129990, 15, "https://placehold.co/600x400?text=Monitor+Samsung", "Monitores", marcas.get("Samsung")),
                producto("Monitor Samsung 27 pulgadas", "Monitor de 27 pulgadas con resolucion Full HD.", 179990, 8, "https://placehold.co/600x400?text=Monitor+27", "Monitores", marcas.get("Samsung")),
                producto("Teclado Logitech K380", "Teclado inalambrico compacto y silencioso.", 39990, 25, "https://placehold.co/600x400?text=Logitech+K380", "Teclados", marcas.get("Logitech")),
                producto("Mouse Logitech M185", "Mouse inalambrico compacto para uso diario.", 14990, 30, "https://placehold.co/600x400?text=Logitech+M185", "Mouse", marcas.get("Logitech")),
                producto("Webcam Logitech C270", "Webcam HD para videollamadas y reuniones.", 29990, 12, "https://placehold.co/600x400?text=Logitech+C270", "Webcams", marcas.get("Logitech")),
                producto("Notebook Lenovo IdeaPad 3", "Notebook para trabajo, estudio y uso diario.", 499990, 7, "https://placehold.co/600x400?text=Lenovo+IdeaPad", "Notebooks", marcas.get("Lenovo")),
                producto("Notebook Lenovo ThinkPad", "Notebook empresarial para productividad.", 799990, 5, "https://placehold.co/600x400?text=ThinkPad", "Notebooks", marcas.get("Lenovo")),
                producto("Audifonos Sony WH-CH520", "Audifonos inalambricos con bateria de larga duracion.", 44990, 20, "https://placehold.co/600x400?text=Sony+WH-CH520", "Audio", marcas.get("Sony")),
                producto("Parlante Sony SRS-XB100", "Parlante Bluetooth portatil y compacto.", 59990, 14, "https://placehold.co/600x400?text=Sony+SRS-XB100", "Audio", marcas.get("Sony")),
                producto("SSD Kingston NV2 1TB", "Unidad SSD NVMe de 1TB para almacenamiento rapido.", 69990, 18, "https://placehold.co/600x400?text=Kingston+NV2", "Almacenamiento", marcas.get("Kingston")),
                producto("Memoria Kingston Fury 16GB", "Memoria RAM DDR4 de 16GB para computadores.", 44990, 22, "https://placehold.co/600x400?text=Kingston+Fury", "Memorias RAM", marcas.get("Kingston")),
                producto("SSD Kingston XS1000 1TB", "SSD externo portatil de 1TB.", 89990, 10, "https://placehold.co/600x400?text=Kingston+XS1000", "Almacenamiento", marcas.get("Kingston"))
        );

        productoRepository.saveAll(productos);
        log.info("Seed completado: {} marcas y {} productos creados.", marcas.size(), productos.size());
    }

    private Producto producto(String nombre, String descripcion, long precio, int stock, String imagenUrl, String categoria, Marca marca) {
        return Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(BigDecimal.valueOf(precio))
                .stock(stock)
                .imagenUrl(imagenUrl)
                .categoria(categoria)
                .marca(marca)
                .activo(true)
                .build();
    }
}