package com.example.transaction.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.transaction.custom_exceptions.CustomException;
import com.example.transaction.models.Transaction;
import com.example.transaction.models.bin.PostTransactionBin;
import com.example.transaction.models.bin.PutTransactionBin;
import com.example.transaction.models.daos.PutTransactionDao;
import com.example.transaction.services.TransactionService;

import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody PostTransactionBin transaction) {
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }

    @SneakyThrows
    @PutMapping("update/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String id, @RequestBody PutTransactionDao entity) {
        return ResponseEntity.ok(transactionService.updateTransaction(
                PutTransactionBin.builder()
                        .id(Long.parseLong(id))
                        .transaction(entity)
                        .build()));
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(Long.parseLong(id));
        return ResponseEntity.ok().build();
    }

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
