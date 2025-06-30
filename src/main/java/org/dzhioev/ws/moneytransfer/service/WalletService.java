package org.dzhioev.ws.moneytransfer.service;

import org.dzhioev.ws.moneytransfer.dto.WalletOperationRequest;
import org.dzhioev.ws.moneytransfer.entity.Wallet;
import org.dzhioev.ws.moneytransfer.repository.WalletRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void processOperation(WalletOperationRequest request) {
        Wallet wallet = walletRepository.findByIdForUpdate(request.getWalletId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Кошелек не найден"));

        request.getOperationType().apply(wallet, request.getAmount());

        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public long getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Кошелек не найден"));
        return wallet.getBalance();
    }
}
