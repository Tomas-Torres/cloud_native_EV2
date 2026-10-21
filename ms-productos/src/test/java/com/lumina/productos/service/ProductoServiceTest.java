package com.lumina.productos.service;

import com.lumina.productos.entity.Marca;
import com.lumina.productos.entity.Producto;
import com.lumina.productos.repository.MarcaRepository;
import com.lumina.productos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MarcaRepository marcaRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Marca marca;

    @BeforeEach
    void setUp() {
        marca = Marca.builder()
                .id(1L)
                .nombre("Nike")
                .logoUrl("nike-logo.png")
                .build();

        producto = Producto.builder()
                .id(1L)
                .nombre("Zapatillas Running")
                .descripcion("Zapatillas para correr")
                .precio(new BigDecimal("99.99"))
                .imagenUrl("zapatillas.jpg")
                .categoria("Calzado")
                .marca(marca)
                .activo(true)
                .build();
    }

    @Test
    void listarActivos_DebeRetornarListaVacia_CuandoNoHayProductosActivos() {
        when(productoRepository.findByActivoTrue()).thenReturn(Collections.emptyList());

        List<Producto> resultado = productoService.listarActivos();

        assertThat(resultado).isEmpty();
        verify(productoRepository).findByActivoTrue();
    }

    @Test
    void listarActivos_DebeRetornarListaConProductosActivos_CuandoExisten() {
        when(productoRepository.findByActivoTrue()).thenReturn(Arrays.asList(producto));

        List<Producto> resultado = productoService.listarActivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getActivo()).isTrue();
        verify(productoRepository).findByActivoTrue();
    }

    @Test
    void obtenerPorId_DebeRetornarProducto_CuandoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Zapatillas Running");
        verify(productoRepository).findById(1L);
    }

    @Test
    void obtenerPorId_DebeLanzarExcepcion_CuandoNoExiste() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerPorId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado con ID: 999");

        verify(productoRepository).findById(999L);
    }

    @Test
    void buscar_DebeRetornarListaConCoincidencias_CuandoExisten() {
        when(productoRepository.findByNombreContainingIgnoreCase("zapatillas"))
                .thenReturn(Arrays.asList(producto));

        List<Producto> resultado = productoService.buscar("zapatillas");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).containsIgnoringCase("zapatillas");
        verify(productoRepository).findByNombreContainingIgnoreCase("zapatillas");
    }

    @Test
    void buscar_DebeRetornarListaVacia_CuandoNoHayCoincidencias() {
        when(productoRepository.findByNombreContainingIgnoreCase("inexistente"))
                .thenReturn(Collections.emptyList());

        List<Producto> resultado = productoService.buscar("inexistente");

        assertThat(resultado).isEmpty();
        verify(productoRepository).findByNombreContainingIgnoreCase("inexistente");
    }

    @Test
    void buscar_DebeFuncionarConBusquedaParcial_CuandoExisteCoincidencia() {
        when(productoRepository.findByNombreContainingIgnoreCase("run"))
                .thenReturn(Arrays.asList(producto));

        List<Producto> resultado = productoService.buscar("run");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).containsIgnoringCase("run");
        verify(productoRepository).findByNombreContainingIgnoreCase("run");
    }

    @Test
    void listarMarcas_DebeRetornarListaVacia_CuandoNoHayMarcas() {
        when(marcaRepository.findAll()).thenReturn(Collections.emptyList());

        List<Marca> resultado = productoService.listarMarcas();

        assertThat(resultado).isEmpty();
        verify(marcaRepository).findAll();
    }

    @Test
    void listarMarcas_DebeRetornarListaConMarcas_CuandoExisten() {
        when(marcaRepository.findAll()).thenReturn(Arrays.asList(marca));

        List<Marca> resultado = productoService.listarMarcas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Nike");
        verify(marcaRepository).findAll();
    }

    @Test
    void crear_DebeCrearYRetornarProducto_CuandoEsValido() {
        Producto nuevoProducto = Producto.builder()
                .nombre("Nuevo Producto")
                .descripcion("Descripción")
                .precio(new BigDecimal("49.99"))
                .marca(marca)
                .build();

        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.crear(nuevoProducto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(productoRepository).save(nuevoProducto);
    }

    @Test
    void actualizar_DebeActualizarProducto_CuandoExiste() {
        Producto datosActualizados = Producto.builder()
                .nombre("Zapatillas Actualizadas")
                .descripcion("Nueva descripción")
                .precio(new BigDecimal("149.99"))
                .imagenUrl("nueva-imagen.jpg")
                .categoria("Calzado Premium")
                .marca(marca)
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.actualizar(1L, datosActualizados);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Zapatillas Actualizadas");
        assertThat(resultado.getPrecio()).isEqualTo(new BigDecimal("149.99"));
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(producto);
    }

    @Test
    void actualizar_DebeLanzarExcepcion_CuandoProductoNoExiste() {
        Producto datosActualizados = Producto.builder()
                .nombre("Producto Inexistente")
                .build();

        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizar(999L, datosActualizados))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado con ID: 999");

        verify(productoRepository).findById(999L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void eliminar_DebeMarcarComoInactivo_CuandoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        productoService.eliminar(1L);

        assertThat(producto.getActivo()).isFalse();
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(producto);
    }

    @Test
    void eliminar_DebeLanzarExcepcion_CuandoProductoNoExiste() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.eliminar(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado con ID: 999");

        verify(productoRepository).findById(999L);
        verify(productoRepository, never()).save(any(Producto.class));
    }
}
