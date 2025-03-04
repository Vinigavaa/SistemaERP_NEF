package com.funilaria.api.services;

import com.funilaria.api.dtos.WorkDTO;
import com.funilaria.api.exceptions.DuplicateWorkException;
import com.funilaria.api.models.Work;
import com.funilaria.api.repositories.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Autowired
    private InvoiceService invoiceService;  // Serviço para gerar a nota fiscal

    // Método para criar um novo trabalho
    public WorkDTO createWork(WorkDTO workDTO) {
        // Validar se o trabalho já existe
        validateDuplicateWork(workDTO);

        // Converter o DTO para a entidade Work
        Work work = convertToEntity(workDTO);
        work.setServiceDate(LocalDate.now());  // Define a data do serviço como a data atual
        work.setInvoiceNumber(generateInvoiceNumber()); // Gerar número da nota fiscal

        // Salvar o trabalho no banco de dados
        work = workRepository.save(work);

        // Gerar automaticamente o PDF da nota fiscal
        try {
            invoiceService.generateInvoice(work);  // Chama o serviço para gerar o PDF da nota fiscal
        } catch (IOException e) {
            // Caso haja um erro ao gerar o PDF
            throw new RuntimeException("Erro ao gerar o PDF da nota fiscal: " + e.getMessage());
        }

        // Retorna o DTO do trabalho
        return convertToDTO(work);
    }

    // Método para buscar um trabalho pelo ID
    public WorkDTO findWorkById(Long id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com id: " + id));
        return convertToDTO(work);
    }

    // Método para atualizar os dados de um trabalho
    public Work updateWork(Long id, Work updatedWork) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com id: " + id));

        updateWorkFields(work, updatedWork);
        return workRepository.save(work);
    }

    // Método para buscar todos os trabalhos de um cliente pelo nome
    public List<WorkDTO> findWorksByClientName(String clientName) {
        return workRepository.findByClientNameContainingIgnoreCase(clientName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para buscar todos os trabalhos
    public List<WorkDTO> findAllWorks() {
        return workRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para calcular os ganhos semanais
    public BigDecimal getWeeklyEarnings() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        return calculateEarningsBetweenDates(startOfWeek, endOfWeek);
    }

    // Método para calcular os ganhos mensais
    public BigDecimal getMonthlyEarnings() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        return calculateEarningsBetweenDates(startOfMonth, endOfMonth);
    }

    // Método para calcular os ganhos por mês e ano
    public BigDecimal getEarningsByMonth(int year, int month) {
        return workRepository.findAll()
                .stream()
                .filter(work -> work.getServiceDate().getYear() == year)
                .filter(work -> work.getServiceDate().getMonthValue() == month)
                .map(Work::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Valida se o trabalho já existe para o cliente, veículo e data
    private void validateDuplicateWork(WorkDTO workDTO) {
        boolean exists = workRepository.existsByClientNameAndCarPlateAndServiceDate(
                workDTO.getClientName(),
                workDTO.getCarPlate(),
                workDTO.getServiceDate()
        );

        if (exists) {
            throw new DuplicateWorkException(
                    "Já existe um serviço registrado para este cliente, veículo e data. Se necessário altere o serviço!"
            );
        }
    }

    // Gera um número de nota fiscal único
    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Calcula os ganhos entre duas datas
    private BigDecimal calculateEarningsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return workRepository.findAll()
                .stream()
                .filter(work -> !work.getServiceDate().isBefore(startDate)
                        && !work.getServiceDate().isAfter(endDate))
                .map(Work::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Atualiza os campos do trabalho
    private void updateWorkFields(Work work, Work updatedWork) {
        work.setClientName(updatedWork.getClientName());
        work.setCarModel(updatedWork.getCarModel());
        work.setCarPlate(updatedWork.getCarPlate());
        work.setCarColor(updatedWork.getCarColor());
        work.setServiceDate(updatedWork.getServiceDate()); // Mantém a data de serviço original, caso necessário
        work.setRepairedParts(updatedWork.getRepairedParts());
        work.setTotalPrice(updatedWork.getTotalPrice());
    }

    // Converte uma entidade Work para um DTO
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

    // Converte um DTO para a entidade Work
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
