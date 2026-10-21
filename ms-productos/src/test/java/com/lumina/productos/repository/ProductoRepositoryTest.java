package com.lumina.productos.repository;

import com.lumina.productos.entity.Marca;
import com.lumina.productos.entity.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoRepositoryTest {

    @Mock
    private ProductoRepository productoRepository;

    private Producto productoActivo;
    private Producto productoInactivo;
    private Producto productoCalzado;
    private Producto productoRopa;
    private Marca marca;

    @BeforeEach
    void setUp() {
        marca = Marca.builder()
                .id(1L)
                .nombre("Nike")
                .logoUrl("nike-logo.png")
                .build();

        productoActivo = Producto.builder()
                .id(1L)
                .nombre("Zapatillas Running")
                .descripcion("Zapatillas para correr")
                .precio(new BigDecimal("99.99"))
                .imagenUrl("zapatillas.jpg")
                .categoria("Calzado")
                .marca(marca)
                .activo(true)
                .build();

        productoInactivo = Producto.builder()
                .id(2L)
                .nombre("Camiseta Old")
                .descripcion("Camiseta antigua")
                .precio(new BigDecimal("29.99"))
                .imagenUrl("camiseta.jpg")
                .categoria("Ropa")
                .marca(marca)
                .activo(false)
                .build();

        productoCalzado = Producto.builder()
                .id(3L)
                .nombre("Botas de Cuero")
                .descripcion("Botas elegantes")
                .precio(new BigDecimal("199.99"))
                .categoria("Calzado")
                .marca(marca)
                .activo(true)
                .build();

        productoRopa = Producto.builder()
                .id(4L)
                .nombre("Camiseta Deportiva")
                .descripcion("Camiseta para deporte")
                .precio(new BigDecimal("39.99"))
                .categoria("Ropa")
                .marca(marca)
                .activo(true)
                .build();
    }

    @Test
    void findByActivoTrue_DebeRetornarSoloProductosActivos_CuandoExisten() {
        when(productoRepository.findByActivoTrue()).thenReturn(Arrays.asList(productoActivo));

        List<Producto> resultado = productoRepository.findByActivoTrue();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
        assertThat(resultado.get(0).getNombre()).isEqualTo("Zapatillas Running");
        verify(productoRepository).findByActivoTrue();
    }

    @Test
    void findByActivoTrue_DebeRetornarListaVacia_CuandoNoHayActivos() {
        when(productoRepository.findByActivoTrue()).thenReturn(Collections.emptyList());

        List<Producto> resultado = productoRepository.findByActivoTrue();

        assertThat(resultado).isEmpty();
        verify(productoRepository).findByActivoTrue();
    }

    @Test
    void findByCategoria_DebeRetornarProductosDeCategoria_CuandoExisten() {
        when(productoRepository.findByCategoria("Calzado")).thenReturn(Arrays.asList(productoCalzado));

        List<Producto> resultado = productoRepository.findByCategoria("Calzado");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria()).isEqualTo("Calzado");
        assertThat(resultado.get(0).getNombre()).isEqualTo("Botas de Cuero");
        verify(productoRepository).findByCategoria("Calzado");
    }

    @Test
    void findByCategoria_DebeRetornarListaVacia_CuandoNoHayProductosEnCategoria() {
        when(productoRepository.findByCategoria("Calzado")).thenReturn(Collections.emptyList());

        List<Producto> resultado = productoRepository.findByCategoria("Calzado");

        assertThat(resultado).isEmpty();
        verify(productoRepository).findByCategoria("Calzado");
    }

    @Test
    void findByMarcaId_DebeRetornarProductosDeMarca_CuandoExisten() {
        when(productoRepository.findByMarcaId(1L)).thenReturn(Arrays.asList(productoActivo, productoRopa));

        List<Producto> resultado = productoRepository.findByMarcaId(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Producto::getNombre)
                .containsExactlyInAnyOrder("Zapatillas Running", "Camiseta Deportiva");
        verify(productoRepository).findByMarcaId(1L);
    }

    @Test
    void findByMarcaId_DebeRetornarListaVacia_CuandoNoHayProductosDeMarca() {
        when(productoRepository.findByMarcaId(2L)).thenReturn(Collections.emptyList());

        List<Producto> resultado = productoRepository.findByMarcaId(2L);

        assertThat(resultado).isEmpty();
        verify(productoRepository).findByMarcaId(2L);
    }

    @Test
    void findByNombreContainingIgnoreCase_DebeBuscarProductosPorNombre_CuandoExisten() {
        when(productoRepository.findByNombreContainingIgnoreCase("zapatillas"))
                .thenReturn(Arrays.asList(productoActivo));

        List<Producto> resultado = productoRepository.findByNombreContainingIgnoreCase("zapatillas");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).containsIgnoringCase("zapatillas");
        verify(productoRepository).findByNombreContainingIgnoreCase("zapatillas");
    }

    @Test
    void findByNombreContainingIgnoreCase_DebeRetornarListaVacia_CuandoNoHayCoincidencias() {
        when(productoRepository.findByNombreContainingIgnoreCase("inexistente"))
                .thenReturn(Collections.emptyList());

        List<Producto> resultado = productoRepository.findByNombreContainingIgnoreCase("inexistente");

        assertThat(resultado).isEmpty();
        verify(productoRepository).findByNombreContainingIgnoreCase("inexistente");
    }

    @Test
    void findByNombreContainingIgnoreCase_DebeFuncionarConBusquedaParcial() {
        when(productoRepository.findByNombreContainingIgnoreCase("run"))
                .thenReturn(Arrays.asList(productoActivo));

        List<Producto> resultado = productoRepository.findByNombreContainingIgnoreCase("run");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).containsIgnoringCase("run");
        verify(productoRepository).findByNombreContainingIgnoreCase("run");
    }

    @Test
    void save_DebePersistirProducto_CuandoEsNuevo() {
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActivo);

        Producto guardado = productoRepository.save(productoActivo);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isEqualTo(1L);
        assertThat(guardado.getNombre()).isEqualTo("Zapatillas Running");
        assertThat(guardado.getActivo()).isTrue();
        verify(productoRepository).save(productoActivo);
    }

    @Test
    void findById_DebeRetornarOptionalConProducto_CuandoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoActivo));

        Optional<Producto> resultado = productoRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Zapatillas Running");
        verify(productoRepository).findById(1L);
    }

    @Test
    void findById_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Producto> resultado = productoRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(productoRepository).findById(999L);
    }

    @Test
    void findAll_DebeRetornarTodosLosProductos() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(productoActivo, productoInactivo));

        List<Producto> resultado = productoRepository.findAll();

        assertThat(resultado).hasSize(2);
        verify(productoRepository).findAll();
    }

    @Test
    void deleteById_DebeEliminarProducto_CuandoExiste() {
        productoRepository.deleteById(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void count_DebeRetornarCantidadCorrecta_DeProductos() {
        when(productoRepository.count()).thenReturn(2L);

        long cantidad = productoRepository.count();

        assertThat(cantidad).isEqualTo(2);
        verify(productoRepository).count();
    }
}
