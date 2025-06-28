package org.dzhioev.ws.moneytransfer.controller;

import jakarta.validation.Valid;
import org.dzhioev.ws.moneytransfer.dto.WalletOperationRequest;
import org.dzhioev.ws.moneytransfer.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<Void> operate(@Valid @RequestBody WalletOperationRequest request) {
        walletService.processOperation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Long>> getBalance(@PathVariable UUID id) {
        long balance = walletService.getBalance(id);
        return ResponseEntity.ok(Map.of("balance", balance));
    }
}
