package com.lumina.pagos.messaging;

import com.lumina.pagos.dto.PedidoPagadoDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publica el evento "pedido pagado" en la cola SQS "curso-pedidos".
 *
 * <p>ms-bodega es el consumidor de esta cola: al recibir el mensaje descuenta el stock de su
 * base de datos. Si el procesamiento falla, SQS reintenta y, tras agotar los reintentos
 * definidos en la redrive policy, el mensaje termina en la DLQ "curso-pedidos-dlq".</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoSqsPublisher {

    private final SqsTemplate sqsTemplate;

    @Value("${lumina.sqs.cola-pedidos:curso-pedidos}")
    private String colaPedidos;

    /**
     * Envia el pedido pagado a la cola de pedidos.
     *
     * @param pedido evento con los items del pedido
     */
    public void publicarPedidoPagado(PedidoPagadoDto pedido) {
        sqsTemplate.send(colaPedidos, pedido);
        log.info("[SQS] Pedido publicado en '{}' | ordenId={} | items={}",
                colaPedidos, pedido.getOrdenId(), pedido.getItems() != null ? pedido.getItems().size() : 0);
    }
}
