package org.dzhioev.ws.moneytransfer;

import org.dzhioev.ws.moneytransfer.entity.Wallet;
import org.dzhioev.ws.moneytransfer.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WalletRepository walletRepository;

    private UUID walletId;

    @BeforeEach
    void setup() {
        walletRepository.deleteAll();

        Wallet wallet = new Wallet();
        wallet.setBalance(1000L);
        wallet = walletRepository.save(wallet);
        walletId = wallet.getId();
    }

    @Test
    void testDepositOperation() {
        String requestBody = String.format("""
            {
                "walletId": "%s",
                "amount": 500,
                "operationType": "DEPOSIT"
            }
            """, walletId);

        webTestClient.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/api/v1/wallets/" + walletId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(1500);
    }

    @Test
    void testWithdrawOperation() {
        String requestBody = String.format("""
            {
                "walletId": "%s",
                "amount": 400,
                "operationType": "WITHDRAW"
            }
            """, walletId);

        webTestClient.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/api/v1/wallets/" + walletId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(600);
    }

    @Test
    void testWithdrawInsufficientFunds() {
        String requestBody = String.format("""
            {
                "walletId": "%s",
                "amount": 2000,
                "operationType": "WITHDRAW"
            }
            """, walletId);

        webTestClient.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetBalanceWalletNotFound() {
        UUID fakeId = UUID.randomUUID();
        webTestClient.get()
                .uri("/api/v1/wallets/" + fakeId)
                .exchange()
                .expectStatus().isNotFound();
    }
}
