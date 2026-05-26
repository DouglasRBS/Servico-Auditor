package com.example.demo.application.bo;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.application.dto.OrderItemDTO;
import com.example.demo.domain.enums.Severity;

@Component
public class ErroEventoBO {

    public int calcularTotalAmount(List<OrderItemDTO> items) {
        return items.stream()
                .mapToInt(OrderItemDTO::getAmount)
                .sum();
    }

    public Severity calcularSeverity(int totalAmount) {
        if (totalAmount > 100) {
            return Severity.HIGH;
        } else if (totalAmount >= 50) {
            return Severity.MEDIUM;
        } else {
            return Severity.LOW;
        }
    }
}