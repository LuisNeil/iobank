package com.ltjeda.web.app.iobank.dto;

import lombok.Data;

@Data
public class TransferDto {

    private long recipientAccountNumber;
    private double amount;
    private String code;
}
