package com.denisslav.moneytransfer.api;

import com.denisslav.moneytransfer.dto.AccountDto;
import com.denisslav.moneytransfer.dto.InitialBalanceDto;
import com.denisslav.moneytransfer.dto.TransactionDto;
import com.denisslav.moneytransfer.service.TransferService;
import com.denisslav.moneytransfer.util.JsonUtil;
import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class TransferControllerTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static class AccountControllerTestApplication implements SparkApplication {
        @Override
        public void init() {
            new TransferController(new TransferService());
        }
    }

    @ClassRule
    public static SparkServer<AccountControllerTestApplication> testServer =
            new SparkServer<>(AccountControllerTestApplication.class, 4567);

    @Test
    public void notExistingAccount() throws Exception {
        GetMethod get = testServer.get("/account/99", false);
        HttpResponse httpResponse = testServer.execute(get);
        assertEquals(404, httpResponse.code());
        assertEquals("Account not found with id 99", new String(httpResponse.body()));

        assertNotNull(testServer.getApplication());
    }

    @Test
    public void initialAndGetAccount() throws Exception {
        InitialBalanceDto balance = new InitialBalanceDto(BigDecimal.TEN);
        PostMethod post = testServer.post("/account/initial", JsonUtil.convertFrom(balance), false);

        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        AccountDto account = JsonUtil.convertToAccount(new String(httpResponse.body()));
        assertEquals(BigDecimal.TEN, account.getBalance());

        GetMethod get = testServer.get("/account/" + account.getId().toString(), false);
        httpResponse = testServer.execute(get);
        assertEquals(200, httpResponse.code());
        account = JsonUtil.convertToAccount(new String(httpResponse.body()));
        assertEquals(BigDecimal.TEN, account.getBalance());

        assertNotNull(testServer.getApplication());
    }

    @Test
    public void twoInitialsAndTransfer() throws Exception {
        InitialBalanceDto balance = new InitialBalanceDto(BigDecimal.TEN);
        PostMethod post = testServer.post("/account/initial", JsonUtil.convertFrom(balance), false);
        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        AccountDto account1 = JsonUtil.convertToAccount(new String(httpResponse.body()));
        assertEquals(BigDecimal.TEN, account1.getBalance());

        post = testServer.post("/account/initial", JsonUtil.convertFrom(balance), false);
        httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        AccountDto account2 = JsonUtil.convertToAccount(new String(httpResponse.body()));
        assertEquals(BigDecimal.TEN, account2.getBalance());

        TransactionDto transactionDto = new TransactionDto(account1.getId(), account2.getId(), BigDecimal.valueOf(5));
        post = testServer.post("/transfer", JsonUtil.convertFrom(transactionDto), false);
        httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        List list = JsonUtil.convertToList(new String(httpResponse.body()));
        assertEquals(5, ((Map)list.get(0)).get("balance"));
        assertEquals(15, ((Map)list.get(1)).get("balance"));

        assertNotNull(testServer.getApplication());
    }

    @Test
    public void twoInitialsAndNotEnoughMoneyTransfer() throws Exception {
        InitialBalanceDto balance = new InitialBalanceDto(BigDecimal.TEN);
        PostMethod post = testServer.post("/account/initial", JsonUtil.convertFrom(balance), false);
        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        AccountDto account1 = JsonUtil.convertToAccount(new String(httpResponse.body()));
        assertEquals(BigDecimal.TEN, account1.getBalance());

        post = testServer.post("/account/initial", JsonUtil.convertFrom(balance), false);
        httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        AccountDto account2 = JsonUtil.convertToAccount(new String(httpResponse.body()));
        assertEquals(BigDecimal.TEN, account2.getBalance());

        TransactionDto transactionDto = new TransactionDto(account1.getId(), account2.getId(), BigDecimal.valueOf(15));
        post = testServer.post("/transfer", JsonUtil.convertFrom(transactionDto), false);
        httpResponse = testServer.execute(post);
        assertEquals(404, httpResponse.code());
        assertEquals("Not enough money on account", new String(httpResponse.body()));


        assertNotNull(testServer.getApplication());
    }


}
