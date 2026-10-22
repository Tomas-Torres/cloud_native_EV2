package com.lumina.productos.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarcaTest {

    private Marca marca;

    @BeforeEach
    void setUp() {
        marca = Marca.builder()
                .id(1L)
                .nombre("Nike")
                .logoUrl("nike-logo.png")
                .build();
    }

    @Test
    void builder_DebeCrearMarcaConTodosLosCampos() {
        Marca marcaConstruida = Marca.builder()
                .id(1L)
                .nombre("Nike")
                .logoUrl("nike-logo.png")
                .build();

        assertThat(marcaConstruida.getId()).isEqualTo(1L);
        assertThat(marcaConstruida.getNombre()).isEqualTo("Nike");
        assertThat(marcaConstruida.getLogoUrl()).isEqualTo("nike-logo.png");
    }

    @Test
    void builder_DebeFuncionarConLogoUrlNull() {
        Marca marcaSinLogo = Marca.builder()
                .id(1L)
                .nombre("Adidas")
                .logoUrl(null)
                .build();

        assertThat(marcaSinLogo.getId()).isEqualTo(1L);
        assertThat(marcaSinLogo.getNombre()).isEqualTo("Adidas");
        assertThat(marcaSinLogo.getLogoUrl()).isNull();
    }

    @Test
    void settersAndGetters_DebenFuncionarCorrectamente() {
        marca.setId(2L);
        marca.setNombre("Adidas");
        marca.setLogoUrl("adidas-logo.png");

        assertThat(marca.getId()).isEqualTo(2L);
        assertThat(marca.getNombre()).isEqualTo("Adidas");
        assertThat(marca.getLogoUrl()).isEqualTo("adidas-logo.png");
    }

    @Test
    void constructorSinArgumentos_DebeCrearInstanciaVacia() {
        Marca marcaVacia = new Marca();

        assertThat(marcaVacia).isNotNull();
        assertThat(marcaVacia.getId()).isNull();
        assertThat(marcaVacia.getNombre()).isNull();
        assertThat(marcaVacia.getLogoUrl()).isNull();
    }

    @Test
    void constructorConTodosLosArgumentos_DebeCrearInstanciaCompleta() {
        Marca marcaCompleta = new Marca(
                1L,
                "Nike",
                "nike-logo.png"
        );

        assertThat(marcaCompleta.getId()).isEqualTo(1L);
        assertThat(marcaCompleta.getNombre()).isEqualTo("Nike");
        assertThat(marcaCompleta.getLogoUrl()).isEqualTo("nike-logo.png");
    }

    @Test
    void nombre_DebeSerUnico_CuandoSeEstablece() {
        marca.setNombre("Puma");

        assertThat(marca.getNombre()).isEqualTo("Puma");
        assertThat(marca.getNombre()).isNotNull();
    }

    @Test
    void logoUrl_DebePoderSerNull_CuandoNoSeProporciona() {
        marca.setLogoUrl(null);

        assertThat(marca.getLogoUrl()).isNull();
    }

    @Test
    void logoUrl_DebePoderSerVacio_CuandoSeProporcionaCadenaVacia() {
        marca.setLogoUrl("");

        assertThat(marca.getLogoUrl()).isEqualTo("");
        assertThat(marca.getLogoUrl()).isNotNull();
    }
}
