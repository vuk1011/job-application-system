package com.vuk.spring_webapp.transfer.dto;

import lombok.Data;

@Data
public class OfferDto {
    private Long id;
    private String name;
    private Boolean accepted;
}
