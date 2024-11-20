package com.demo.kafka.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import shopsqs.demo.dto.ItemPedidoDTO;
import shopsqs.demo.dto.PedidoSQSMessageDTO;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "orders", groupId = "mail-service-group")
    public void handleOrderMessage(String kafkamessage) {

        String decodedMessage;

        try {
            // Paso 1: Decodificar el mensaje escapado
            decodedMessage = new ObjectMapper().readValue(kafkamessage, String.class);
            // Paso 2: Mapear el JSON ya decodificado a PedidoSQSMessageDTO
            PedidoSQSMessageDTO order = new ObjectMapper().readValue(decodedMessage, PedidoSQSMessageDTO.class);

            System.out.println("Entramos en el consumidor de kafka que envía correo. Pedido: "+order.getPedidoId());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper;
            try {
                helper = new MimeMessageHelper(message, true);
                helper.setTo(order.getUsuarioEmail());
                helper.setSubject("Order Confirmation " + order.getPedidoId());
                helper.setFrom(new InternetAddress("hello@demomailtrap.com", "Test Shop"));
        
                StringBuilder content = new StringBuilder();
                content.append("Hello ").append(order.getUsuarioNombre()).append(",\n\n")
                       .append("Thank you for your purchase. Here is your order summary:\n\n");
        
                for (ItemPedidoDTO item : order.getItems()) {
                    content.append("Product: ").append(item.getNombreProducto())
                           .append("\nQuantity: ").append(item.getCantidad())
                           .append("\nUnit Price: $").append(item.getPrecioUnitario())
                           .append("\nTotal: $").append(item.getPrecioTotal())
                           .append("\n\n");
                }
                content.append("Total Order Price: $").append(order.getPrecioTotal())
                       .append("\n\nThank you for your purchase!");
        
                helper.setText(content.toString());
            } catch (MessagingException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
    
            mailSender.send(message);

        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }
}
