package com.lumina.bodega.listener;

import com.lumina.bodega.dto.ItemPedidoDto;
import com.lumina.bodega.dto.PedidoPagadoDto;
import com.lumina.bodega.service.BodegaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageSystemAttributeName;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoSqsConsumerTest {

    @Mock
    private BodegaService bodegaService;

    @InjectMocks
    private PedidoSqsConsumer pedidoSqsConsumer;

    private PedidoPagadoDto pedido;
    private Message mensajeSqs;

    @BeforeEach
    void setUp() {
        pedido = PedidoPagadoDto.builder()
                .ordenId("ORD-123")
                .usuarioId(100L)
                .items(List.of(
                        ItemPedidoDto.builder()
                                .productoId(100L)
                                .nombreProducto("Laptop HP")
                                .cantidad(2)
                                .build()))
                .build();

        mensajeSqs = Message.builder()
                .messageId("msg-abc123")
                .receiptHandle("rh-1")
                .body("{}")
                .attributes(Map.of(MessageSystemAttributeName.APPROXIMATE_RECEIVE_COUNT, "1"))
                .build();
    }

    @Test
    void consumirPedidoPagado_DebeDescontarStock_CuandoElProcesamientoEsExitoso() {
        pedidoSqsConsumer.consumirPedidoPagado(pedido, mensajeSqs);

        verify(bodegaService).descontarStockPedido(pedido.getItems());
    }

    @Test
    void consumirPedidoPagado_DebeRelanzarLaExcepcion_CuandoElDescuentoFalla() {
        doThrow(new RuntimeException("Stock insuficiente para producto: 100"))
                .when(bodegaService).descontarStockPedido(anyList());

        // Si la excepcion no se relanzara, Spring Cloud AWS ACK-earia el mensaje y este
        // nunca llegaria a la cola "curso-pedidos-dlq".
        assertThatThrownBy(() -> pedidoSqsConsumer.consumirPedidoPagado(pedido, mensajeSqs))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente para producto: 100");
    }
}
