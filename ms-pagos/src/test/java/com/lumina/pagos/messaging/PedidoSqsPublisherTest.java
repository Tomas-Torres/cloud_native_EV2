package com.lumina.pagos.messaging;

import com.lumina.pagos.dto.ItemPedidoDto;
import com.lumina.pagos.dto.PedidoPagadoDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoSqsPublisherTest {

    @Mock
    private SqsTemplate sqsTemplate;

    @InjectMocks
    private PedidoSqsPublisher pedidoSqsPublisher;

    private PedidoPagadoDto pedido;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pedidoSqsPublisher, "colaPedidos", "curso-pedidos");

        pedido = PedidoPagadoDto.builder()
                .ordenId("ORD-123")
                .usuarioId(100L)
                .items(List.of(
                        ItemPedidoDto.builder()
                                .productoId(100L)
                                .nombreProducto("Laptop HP")
                                .cantidad(2)
                                .build()))
                .montoTotal(new BigDecimal("10000.00"))
                .moneda("CLP")
                .fechaPago(LocalDateTime.now())
                .build();
    }

    @Test
    void publicarPedidoPagado_DebeEnviarElMensajeALaColaDePedidos() {
        pedidoSqsPublisher.publicarPedidoPagado(pedido);

        verify(sqsTemplate).send(eq("curso-pedidos"), any(PedidoPagadoDto.class));
    }
}
