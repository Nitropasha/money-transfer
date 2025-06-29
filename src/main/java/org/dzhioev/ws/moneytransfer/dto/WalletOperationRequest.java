package org.dzhioev.ws.moneytransfer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.dzhioev.ws.moneytransfer.enums.OperationType;
import java.util.UUID;

@Setter
@Getter
public class WalletOperationRequest {

    @NotNull
    private UUID walletId;

    @NotNull
    private OperationType operationType;

    @Min(1)
    private long amount;

}
