package org.dzhioev.ws.moneytransfer;

import org.dzhioev.ws.moneytransfer.entity.Wallet;
import org.dzhioev.ws.moneytransfer.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Тест запускать при запущенном docker-compose
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    private UUID walletId;

    @BeforeEach
    void setup() {
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID()); //
        wallet.setBalance(1000L);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        wallet = walletRepository.save(wallet);
        walletId = wallet.getId();
    }

    @Test
    void testDepositOperation() throws Exception {
        String requestBody = String.format("""
            {
                "walletId": "%s",
                "amount": 500,
                "operationType": "DEPOSIT"
            }
            """, walletId);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/wallets/" + walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void testWithdrawOperation() throws Exception {
        String requestBody = String.format("""
            {
                "walletId": "%s",
                "amount": 400,
                "operationType": "WITHDRAW"
            }
            """, walletId);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/wallets/" + walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(600));
    }

    @Test
    void testWithdrawInsufficientFunds() throws Exception {
        String requestBody = String.format("""
            {
                "walletId": "%s",
                "amount": 2000,
                "operationType": "WITHDRAW"
            }
            """, walletId);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBalanceWalletNotFound() throws Exception {
        UUID fakeId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/wallets/" + fakeId))
                .andExpect(status().isNotFound());
    }
}
