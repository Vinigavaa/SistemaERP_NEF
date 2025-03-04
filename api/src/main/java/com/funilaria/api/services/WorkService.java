package com.funilaria.api.services;

import com.funilaria.api.dtos.WorkDTO;
import com.funilaria.api.exceptions.DuplicateWorkException;
import com.funilaria.api.models.Work;
import com.funilaria.api.repositories.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkService {

    @Autowired
    private WorkRepository workRepository;

    public WorkDTO createWork(WorkDTO workDTO) {
        LocalDate generatedDate = LocalDate.now();
        validateDuplicateWork(workDTO, generatedDate);

        Work work = convertToEntity(workDTO);
        work.setServiceDate(LocalDate.now());  // Define a serviceDate automaticamente como a data atual
        work.setInvoiceNumber(generateInvoiceNumber());
        work = workRepository.save(work);

        return convertToDTO(work);
    }

    public WorkDTO findWorkById(Long id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com id: " + id));
        return convertToDTO(work);
    }

    public Work updateWork(Long id, Work updatedWork) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com id: " + id));

        updateWorkFields(work, updatedWork);
        return workRepository.save(work);
    }

    public List<WorkDTO> findWorksByClientName(String clientName) {
        return workRepository.findByClientNameContainingIgnoreCase(clientName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<WorkDTO> findAllWorks() {
        return workRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal getWeeklyEarnings() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        return calculateEarningsBetweenDates(startOfWeek, endOfWeek);
    }

    public BigDecimal getMonthlyEarnings() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        return calculateEarningsBetweenDates(startOfMonth, endOfMonth);
    }

    public BigDecimal getEarningsByMonth(int year, int month) {
        return workRepository.findAll()
                .stream()
                .filter(work -> work.getServiceDate().getYear() == year)
                .filter(work -> work.getServiceDate().getMonthValue() == month)
                .map(Work::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Métodos auxiliares

    private void validateDuplicateWork(WorkDTO workDTO,LocalDate generatedDate) {
        boolean exists = workRepository.existsByClientNameAndCarPlateAndServiceDate(
                workDTO.getClientName(),
                workDTO.getCarPlate(),
                generatedDate
        );

        if (exists) {
            throw new DuplicateWorkException(
                    "Já existe um serviço registrado para este cliente, veículo e data. Se necessário altere o serviço!"
            );
        }
    }

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal calculateEarningsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return workRepository.findAll()
                .stream()
                .filter(work -> !work.getServiceDate().isBefore(startDate)
                        && !work.getServiceDate().isAfter(endDate))
                .map(Work::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateWorkFields(Work work, Work updatedWork) {
        work.setClientName(updatedWork.getClientName());
        work.setCarModel(updatedWork.getCarModel());
        work.setCarPlate(updatedWork.getCarPlate());
        work.setCarColor(updatedWork.getCarColor());
        work.setServiceDate(updatedWork.getServiceDate()); // Mantém a data de serviço original, caso necessário
        work.setRepairedParts(updatedWork.getRepairedParts());
        work.setTotalPrice(updatedWork.getTotalPrice());
    }

    public WorkDTO convertToDTO(Work work) {
        WorkDTO dto = new WorkDTO();
        dto.setId(work.getId());
        dto.setClientName(work.getClientName());
        dto.setCarModel(work.getCarModel());
        dto.setCarPlate(work.getCarPlate());
        dto.setCarColor(work.getCarColor());
        dto.setServiceDate(work.getServiceDate());
        dto.setRepairedParts(work.getRepairedParts());
        dto.setTotalPrice(work.getTotalPrice());
        return dto;
    }

    public Work convertToEntity(WorkDTO dto) {
        Work work = new Work();
        work.setId(dto.getId());
        work.setClientName(dto.getClientName());
        work.setCarModel(dto.getCarModel());
        work.setCarPlate(dto.getCarPlate());
        work.setCarColor(dto.getCarColor());
        work.setRepairedParts(dto.getRepairedParts());
        work.setTotalPrice(dto.getTotalPrice());
        return work;
    }

    public void deleteWork(Long id) {
        workRepository.deleteById(id);
    }
}
