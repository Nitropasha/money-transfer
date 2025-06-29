package org.dzhioev.ws.moneytransfer.enums;

import org.dzhioev.ws.moneytransfer.entity.Wallet;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum OperationType {
    DEPOSIT {
        @Override
        public void apply(Wallet wallet, long amount) {
            wallet.setBalance(wallet.getBalance() + amount);
        }
    },
    WITHDRAW {
        @Override
        public void apply(Wallet wallet, long amount) {
            if (wallet.getBalance() < amount) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
            }
            wallet.setBalance(wallet.getBalance() - amount);
        }
    };

    public abstract void apply(Wallet wallet, long amount);
}
