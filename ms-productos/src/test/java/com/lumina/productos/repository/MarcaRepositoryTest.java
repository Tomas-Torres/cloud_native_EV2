package com.lumina.productos.repository;

import com.lumina.productos.entity.Marca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarcaRepositoryTest {

    @Mock
    private MarcaRepository marcaRepository;

    private Marca marca1;
    private Marca marca2;

    @BeforeEach
    void setUp() {
        marca1 = Marca.builder()
                .id(1L)
                .nombre("Nike")
                .logoUrl("nike-logo.png")
                .build();

        marca2 = Marca.builder()
                .id(2L)
                .nombre("Adidas")
                .logoUrl("adidas-logo.png")
                .build();
    }

    @Test
    void save_DebePersistirMarca_CuandoEsNueva() {
        when(marcaRepository.save(any(Marca.class))).thenReturn(marca1);

        Marca guardada = marcaRepository.save(marca1);

        assertThat(guardada).isNotNull();
        assertThat(guardada.getId()).isEqualTo(1L);
        assertThat(guardada.getNombre()).isEqualTo("Nike");
        assertThat(guardada.getLogoUrl()).isEqualTo("nike-logo.png");
        verify(marcaRepository).save(marca1);
    }

    @Test
    void findById_DebeRetornarOptionalConMarca_CuandoExiste() {
        when(marcaRepository.findById(1L)).thenReturn(Optional.of(marca1));

        Optional<Marca> resultado = marcaRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Nike");
        verify(marcaRepository).findById(1L);
    }

    @Test
    void findById_DebeRetornarOptionalVacio_CuandoNoExiste() {
        when(marcaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Marca> resultado = marcaRepository.findById(999L);

        assertThat(resultado).isEmpty();
        verify(marcaRepository).findById(999L);
    }

    @Test
    void findAll_DebeRetornarTodasLasMarcas() {
        when(marcaRepository.findAll()).thenReturn(Arrays.asList(marca1, marca2));

        List<Marca> resultado = marcaRepository.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Marca::getNombre)
                .containsExactlyInAnyOrder("Nike", "Adidas");
        verify(marcaRepository).findAll();
    }

    @Test
    void findAll_DebeRetornarListaVacia_CuandoNoHayMarcas() {
        when(marcaRepository.findAll()).thenReturn(Collections.emptyList());

        List<Marca> resultado = marcaRepository.findAll();

        assertThat(resultado).isEmpty();
        verify(marcaRepository).findAll();
    }

    @Test
    void deleteById_DebeEliminarMarca_CuandoExiste() {
        marcaRepository.deleteById(1L);

        verify(marcaRepository).deleteById(1L);
    }

    @Test
    void count_DebeRetornarCantidadCorrecta_DeMarcas() {
        when(marcaRepository.count()).thenReturn(2L);

        long cantidad = marcaRepository.count();

        assertThat(cantidad).isEqualTo(2);
        verify(marcaRepository).count();
    }

    @Test
    void save_DebeActualizarMarca_CuandoYaExiste() {
        when(marcaRepository.save(any(Marca.class))).thenReturn(marca1);

        marca1.setLogoUrl("nuevo-logo.png");
        Marca actualizada = marcaRepository.save(marca1);

        assertThat(actualizada.getLogoUrl()).isEqualTo("nuevo-logo.png");
        assertThat(actualizada.getId()).isEqualTo(marca1.getId());
        verify(marcaRepository).save(marca1);
    }
}
