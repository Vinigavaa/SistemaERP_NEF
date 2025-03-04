package com.funilaria.api.controllers;

import com.funilaria.api.dtos.WorkDTO;
import com.funilaria.api.models.Work;
import com.funilaria.api.services.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/service")
public class WorkController {
    @Autowired
    private WorkService workService;

    @PostMapping()
    public ResponseEntity<WorkDTO> createWork(@RequestBody WorkDTO workDTO){
        WorkDTO work = workService.createWork(workDTO);
        return ResponseEntity.ok(work);
    }

    @GetMapping
    public ResponseEntity<List<WorkDTO>> findAllWorks(){
        List<WorkDTO> works = workService.findAllWorks();
        return ResponseEntity.ok(works);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkDTO> findWorkById(@PathVariable Long id){
        WorkDTO work = workService.findWorkById(id);
        return ResponseEntity.ok(work);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Work> updateWork(@PathVariable Long id, @RequestBody Work workAtualizado) {
        Work work = workService.updateWork(id, workAtualizado);
        return ResponseEntity.ok(work);
    }

    @GetMapping("/search")
    public ResponseEntity<List<WorkDTO>> findWorksByClientName(@RequestParam String clientName) {
        List<WorkDTO> works = workService.findWorksByClientName(clientName);
        return ResponseEntity.ok(works);
    }

    @GetMapping("/earnings/week")
    public ResponseEntity<BigDecimal> getWeeklyEarnings() {
        return ResponseEntity.ok(workService.getWeeklyEarnings());
    }

    @GetMapping("/earnings/month")
    public ResponseEntity<BigDecimal> getMonthlyEarnings() {
        return ResponseEntity.ok(workService.getMonthlyEarnings());
    }

    @GetMapping("/earnings/{year}/{month}")
    public ResponseEntity<BigDecimal> getEarningsByMonth(
            @PathVariable int year,
            @PathVariable int month) {
        BigDecimal earnings = workService.getEarningsByMonth(year, month);
        return ResponseEntity.ok(earnings);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        workService.deleteWork(id);
        return ResponseEntity.noContent().build();
    }
}
