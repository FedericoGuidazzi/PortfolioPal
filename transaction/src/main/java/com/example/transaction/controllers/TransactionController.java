package com.example.transaction.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.services.TransactionService;

import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @SneakyThrows
    @PostMapping("/upload")
    public ResponseEntity<List<Transaction>> uploadFile(
            @RequestParam(value = "file", required = true) MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            throw new CustomException("Please upload a valid CSV file.");
        }

        return ResponseEntity.ok(transactionService.saveTransactionsFromCsv(file.getInputStream()));
    }
}
