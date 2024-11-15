package com.ltjeda.web.app.iobank.dto;

import lombok.Data;

@Data
public class ConvertDto {
    private String fromCurrency;
    private String toCurrency;
    private double amount;
}
