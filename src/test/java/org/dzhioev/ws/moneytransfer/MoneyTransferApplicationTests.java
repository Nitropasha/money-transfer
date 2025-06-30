package org.dzhioev.ws.moneytransfer;

import org.dzhioev.ws.moneytransfer.dto.WalletOperationRequest;
import org.dzhioev.ws.moneytransfer.entity.Wallet;
import org.dzhioev.ws.moneytransfer.enums.OperationType;
import org.dzhioev.ws.moneytransfer.repository.WalletRepository;
import org.dzhioev.ws.moneytransfer.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoneyTransferApplicationTests {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void depositShouldIncreaseBalanceTest() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(1000L);

        WalletOperationRequest request = mock(WalletOperationRequest.class);
        when(request.getWalletId()).thenReturn(walletId);
        when(request.getAmount()).thenReturn(500L);
        when(request.getOperationType()).thenReturn(OperationType.DEPOSIT);

        when(walletRepository.findByIdForUpdate(walletId)).thenReturn(Optional.of(wallet));

        walletService.processOperation(request);

        assertEquals(1500L, wallet.getBalance());
        assertNotNull(wallet.getUpdatedAt());
        verify(walletRepository).save(wallet);
    }

    @Test
    void withdrawShouldDecreaseBalanceTest() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(1000L);

        WalletOperationRequest request = mock(WalletOperationRequest.class);
        when(request.getWalletId()).thenReturn(walletId);
        when(request.getAmount()).thenReturn(400L);
        when(request.getOperationType()).thenReturn(OperationType.WITHDRAW);

        when(walletRepository.findByIdForUpdate(walletId)).thenReturn(Optional.of(wallet));

        walletService.processOperation(request);

        assertEquals(600L, wallet.getBalance());
        assertNotNull(wallet.getUpdatedAt());
        verify(walletRepository).save(wallet);
    }

    @Test
    void WithdrawInsufficientFundsShouldThrowTest() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(100L);

        WalletOperationRequest request = mock(WalletOperationRequest.class);
        when(request.getWalletId()).thenReturn(walletId);
        when(request.getAmount()).thenReturn(200L);
        when(request.getOperationType()).thenReturn(OperationType.WITHDRAW);

        when(walletRepository.findByIdForUpdate(walletId)).thenReturn(Optional.of(wallet));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> walletService.processOperation(request));

        assertEquals("400 BAD_REQUEST \"Недостаточно средств\"", ex.getMessage());
        verify(walletRepository, never()).save(any());
    }

    @Test
    void WalletNotFoundShouldThrowTest() {
        UUID walletId = UUID.randomUUID();
        WalletOperationRequest request = mock(WalletOperationRequest.class);
        when(request.getWalletId()).thenReturn(walletId);

        when(walletRepository.findByIdForUpdate(walletId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> walletService.processOperation(request));

        assertEquals("404 NOT_FOUND \"Кошелек не найден\"", ex.getMessage());
    }

    @Test
    void getBalanceShouldReturnCorrectBalanceTest() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(2000L);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        long result = walletService.getBalance(walletId);
        assertEquals(2000L, result);
    }

}