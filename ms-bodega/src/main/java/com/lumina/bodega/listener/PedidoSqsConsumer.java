package com.lumina.bodega.listener;

import com.lumina.bodega.dto.PedidoPagadoDto;
import com.lumina.bodega.service.BodegaService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageSystemAttributeName;

/**
 * Consumidor de la cola SQS "curso-pedidos" (Dead Letter Queue: "curso-pedidos-dlq").
 *
 * <p>Flujo de reintentos / DLQ:</p>
 * <ol>
 *   <li>El mensaje se borra de SQS (ACK) solo si el listener termina sin error
 *       ({@code AcknowledgementMode.ON_SUCCESS} es el default de Spring Cloud AWS).</li>
 *   <li>Si el listener lanza una excepcion, el framework NO borra el mensaje: vuelve a estar
 *       visible tras el visibility timeout y SQS incrementa el receive count.</li>
 *   <li>Al superar {@code maxReceiveCount = 2} (redrive policy de la cola), SQS mueve el
 *       mensaje a "curso-pedidos-dlq".</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoSqsConsumer {

    private final BodegaService bodegaService;

    /**
     * Procesa un pedido pagado: descuenta el stock de cada item del pedido en la base de datos
     * de bodega.
     *
     * @param pedido     pedido pagado deserializado desde el JSON de SQS
     * @param mensajeSqs mensaje original de SQS (solo para logging: id y numero de recepciones)
     */
    @SqsListener("curso-pedidos")
    public void consumirPedidoPagado(PedidoPagadoDto pedido, Message mensajeSqs) {

        String receiveCount = mensajeSqs.attributes() != null
                ? mensajeSqs.attributes().get(MessageSystemAttributeName.APPROXIMATE_RECEIVE_COUNT)
                : null;

        log.info("[SQS] Mensaje recibido en 'curso-pedidos' | mensajeId={} | receiveCount={} | ordenId={}",
                mensajeSqs.messageId(), receiveCount, pedido.getOrdenId());

        try {
            bodegaService.descontarStockPedido(pedido.getItems());
            log.info("[SQS] Stock descontado correctamente | ordenId={}", pedido.getOrdenId());

        } catch (RuntimeException e) {
            log.error("[SQS] Error descontando stock de la orden {}. El mensaje NO se borra: SQS lo reintentara "
                            + "y, tras 2 recepciones, lo movera a 'curso-pedidos-dlq'",
                    pedido.getOrdenId(), e);
            // Se relanza: sin esto el mensaje seria ACK-eado y nunca llegaria a la DLQ.
            throw e;
        }
    }
}
