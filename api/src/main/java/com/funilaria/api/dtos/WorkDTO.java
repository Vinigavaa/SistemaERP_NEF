package com.funilaria.api.dtos;

import com.funilaria.api.enums.PecaEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class WorkDTO{
    private Long id;
    private String clientName;
    private String carModel;
    private String carPlate;
    private String carColor;
    private LocalDate serviceDate;
    private List<PecaEnum> repairedParts;
    private BigDecimal totalPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public List<PecaEnum> getRepairedParts() {
        return repairedParts;
    }

    public void setRepairedParts(List<PecaEnum> repairedParts) {
        this.repairedParts = repairedParts;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
