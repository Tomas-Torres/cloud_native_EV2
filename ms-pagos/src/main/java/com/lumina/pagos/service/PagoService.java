package com.lumina.pagos.service;

import com.lumina.pagos.dto.CrearPreferenciaRequest;
import com.lumina.pagos.dto.PagoResponse;
import com.lumina.pagos.entity.EstadoPago;
import com.lumina.pagos.entity.Pago;
import com.lumina.pagos.repository.PagoRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PreferenceClient preferenceClient;
    private final PaymentClient paymentClient;

    @Value("${mercadopago.notification-url}")
    private String notificationUrl;

    @Transactional
    public PagoResponse crearPreferencia(CrearPreferenciaRequest request) {
        try {
            // Calcular monto total
            BigDecimal montoTotal = request.getItems().stream()
                    .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Crear items para MercadoPago
            List<PreferenceItemRequest> mpItems = new ArrayList<>();
            for (CrearPreferenciaRequest.ItemRequest item : request.getItems()) {
                PreferenceItemRequest.PreferenceItemRequestBuilder itemBuilder = PreferenceItemRequest.builder()
                        .title(item.getTitulo())
                        .quantity(item.getCantidad())
                        .unitPrice(item.getPrecioUnitario())
                        .currencyId("CLP");

                if (item.getDescripcion() != null && !item.getDescripcion().isBlank()) {
                    itemBuilder.description(item.getDescripcion());
                }
                if (item.getImagenUrl() != null && !item.getImagenUrl().isBlank()) {
                    itemBuilder.pictureUrl(item.getImagenUrl());
                }

                mpItems.add(itemBuilder.build());
            }

            // Configurar back URLs para redirección
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:5173/pago/exitoso")
                    .failure("http://localhost:5173/pago/fallido")
                    .pending("http://localhost:5173/pago/exitoso")
                    .build();

            // Construir la preferencia
            PreferenceRequest.PreferenceRequestBuilder prefBuilder = PreferenceRequest.builder()
                    .items(mpItems)
                    .backUrls(backUrls)
                    .externalReference(request.getOrdenId());

            // Solo agregar notificationUrl si no es localhost
            if (notificationUrl != null && !notificationUrl.contains("localhost")) {
                prefBuilder.notificationUrl(notificationUrl);
            }

            PreferenceRequest preferenceRequest = prefBuilder.build();

            // Crear preferencia en MercadoPago
            Preference preference = preferenceClient.create(preferenceRequest);

            // Persistir el pago en la BD
            Pago pago = Pago.builder()
                    .ordenId(request.getOrdenId())
                    .usuarioId(request.getUsuarioId())
                    .montoTotal(montoTotal)
                    .moneda("CLP")
                    .estado(EstadoPago.PENDIENTE)
                    .mercadopagoPreferenceId(preference.getId())
                    .build();

            pago = pagoRepository.save(pago);

            log.info("Preferencia creada: {} para orden: {}", preference.getId(), request.getOrdenId());

            return mapToResponse(pago, preference.getInitPoint(), preference.getSandboxInitPoint());

        } catch (com.mercadopago.exceptions.MPApiException apiEx) {
            log.error("MercadoPago API Error para orden {}: Status={}, Content={}",
                    request.getOrdenId(), apiEx.getStatusCode(), apiEx.getApiResponse().getContent());
            throw new RuntimeException("Error MercadoPago: " + apiEx.getApiResponse().getContent(), apiEx);
        } catch (Exception e) {
            log.error("Error al crear preferencia de pago para orden {}: {}", request.getOrdenId(), e.getMessage());
            throw new RuntimeException("Error al crear preferencia de pago: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void procesarNotificacion(String topic, Long resourceId) {
        log.info("Webhook recibido - topic: {}, resourceId: {}", topic, resourceId);

        if (!"payment".equals(topic)) {
            log.info("Notificación ignorada, topic: {}", topic);
            return;
        }

        try {
            Payment payment = paymentClient.get(resourceId);

            String externalReference = payment.getExternalReference();
            String paymentStatus = payment.getStatus();

            log.info("Payment ID: {}, Status: {}, ExternalRef: {}",
                    payment.getId(), paymentStatus, externalReference);

            // Buscar pagos por orden (external reference)
            List<Pago> pagos = pagoRepository.findByOrdenId(externalReference);

            if (pagos.isEmpty()) {
                log.warn("No se encontró pago para external_reference: {}", externalReference);
                return;
            }

            Pago pago = pagos.get(0);
            pago.setMercadopagoPaymentId(String.valueOf(payment.getId()));
            pago.setMercadopagoStatus(paymentStatus);
            pago.setMercadopagoStatusDetail(payment.getStatusDetail());
            pago.setMetodoPago(payment.getPaymentMethodId());
            pago.setEstado(mapearEstado(paymentStatus));

            pagoRepository.save(pago);

            log.info("Pago actualizado - ID: {}, Estado: {}", pago.getId(), pago.getEstado());

        } catch (Exception e) {
            log.error("Error procesando webhook de pago: {}", e.getMessage());
            throw new RuntimeException("Error procesando notificación de pago", e);
        }
    }

    public PagoResponse obtenerPago(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
        return mapToResponse(pago, null, null);
    }

    public List<PagoResponse> obtenerPagosPorOrden(String ordenId) {
        return pagoRepository.findByOrdenId(ordenId).stream()
                .map(pago -> mapToResponse(pago, null, null))
                .collect(Collectors.toList());
    }

    private EstadoPago mapearEstado(String mpStatus) {
        return switch (mpStatus) {
            case "approved" -> EstadoPago.APROBADO;
            case "pending", "in_process" -> EstadoPago.PROCESANDO;
            case "rejected" -> EstadoPago.RECHAZADO;
            case "cancelled" -> EstadoPago.CANCELADO;
            case "refunded" -> EstadoPago.REEMBOLSADO;
            case "in_mediation" -> EstadoPago.EN_MEDIACION;
            default -> EstadoPago.PENDIENTE;
        };
    }

    private PagoResponse mapToResponse(Pago pago, String initPoint, String sandboxInitPoint) {
        return PagoResponse.builder()
                .id(pago.getId())
                .ordenId(pago.getOrdenId())
                .usuarioId(pago.getUsuarioId())
                .montoTotal(pago.getMontoTotal())
                .moneda(pago.getMoneda())
                .estado(pago.getEstado())
                .mercadopagoPreferenceId(pago.getMercadopagoPreferenceId())
                .mercadopagoPaymentId(pago.getMercadopagoPaymentId())
                .mercadopagoStatus(pago.getMercadopagoStatus())
                .metodoPago(pago.getMetodoPago())
                .fechaCreacion(pago.getFechaCreacion())
                .fechaActualizacion(pago.getFechaActualizacion())
                .initPoint(initPoint)
                .sandboxInitPoint(sandboxInitPoint)
                .build();
    }
}
