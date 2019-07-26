package com.denisslav.moneytransfer.api;

import com.denisslav.moneytransfer.dto.AccountDto;
import com.denisslav.moneytransfer.dto.InitialBalanceDto;
import com.denisslav.moneytransfer.dto.TransactionDto;
import com.denisslav.moneytransfer.exception.AccountNotFoundException;
import com.denisslav.moneytransfer.exception.NotCorrectAmountException;
import com.denisslav.moneytransfer.exception.NotEnoughBalanceException;
import com.denisslav.moneytransfer.exception.SameAccountException;
import com.denisslav.moneytransfer.model.Account;
import com.denisslav.moneytransfer.service.TransferService;
import com.denisslav.moneytransfer.util.JsonUtil;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.jetty.io.FillInterest;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import spark.Response;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

public class TransferController {
    private final static Logger logger = Log.getLogger(FillInterest.class);

    @Inject
    public TransferController(TransferService transferService) {
        get("/account/:id", (req, res) -> {
            res.type("application/json");
            Account account = transferService.getAccount(Long.valueOf(req.params("id")));
            return JsonUtil.convertFrom(new AccountDto(account.getId(), account.getBalance().get()));
        });

        post("/account/initial", "application/json", (req, res) -> {
            res.type("application/json");
            InitialBalanceDto balance = JsonUtil.convertToBalance(req.body());
            Account account = transferService.createAccount(balance.getBalance());
            return JsonUtil.convertFrom(new AccountDto(account.getId(), account.getBalance().get()));
        });

        post("/transfer", "application/json", (req, res) -> {
            res.type("application/json");
            TransactionDto transaction = JsonUtil.convertToTransaction(req.body());
            List<Account> accounts = transferService.transfer(transaction.getFrom(), transaction.getTo(), transaction.getAmount());
            List<AccountDto> result = List.of(
                    new AccountDto(accounts.get(0).getId(), accounts.get(0).getBalance().get()),
                    new AccountDto(accounts.get(1).getId(), accounts.get(1).getBalance().get()));
            return JsonUtil.convertFrom(result);
        });

        exception(NotCorrectAmountException.class, (e, req, res) -> {
            errorResponse(e, res, "Not correct amount");
        });

        exception(AccountNotFoundException.class, (e, req, res) -> {
            errorResponse(e, res, "Account not found with id " + e.getId());
        });

        exception(SameAccountException.class, (e, req, res) -> {
            errorResponse(e, res,"Transfer to same account");
        });

        exception(NotEnoughBalanceException.class, (e, req, res) -> {
            errorResponse(e, res, "Not enough money on account");
        });

        exception(Exception.class, (e, req, res) -> {
            errorResponse(e, res, "Internal server error");
        });
    }

    private void errorResponse(Exception ex, Response res, String message) {
        ex.printStackTrace();
        res.status(404);
        res.body(message);
    }

}
