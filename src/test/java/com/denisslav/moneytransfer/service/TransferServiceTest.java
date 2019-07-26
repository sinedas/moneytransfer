package com.denisslav.moneytransfer.service;

import com.denisslav.moneytransfer.model.Account;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

public class TransferServiceTest {

    public static final int ACCOUNTS_SIZE = 10000;
    public static final int OPS_SIZE = 5000;

    /**
     * Total (5000 * (5000 - 5)) = 24.750.000 operations
     */
    @Test
    public void test() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        TransferService transferService = new TransferService();

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < ACCOUNTS_SIZE; i++) {
            accounts.add(transferService.createAccount(BigDecimal.valueOf(100000)));
        }

        long time = System.currentTimeMillis();
        for (long i = 0; i < OPS_SIZE; i++) {
            final long from = i % ACCOUNTS_SIZE;
            for (long j = 0; j < OPS_SIZE; j++) {
                final long to = j % ACCOUNTS_SIZE;
                if (from == to) {
                    continue;
                }

                executor.execute(() -> {
                    transferService.transfer(from, to, BigDecimal.valueOf(1));
                });
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.err.println("Time spend: " + (System.currentTimeMillis() - time));
        for (long i = 0; i < ACCOUNTS_SIZE; i++) {
            Assert.assertEquals(BigDecimal.valueOf(100000), transferService.getAccount(i).getBalance().get());
        }
    }
}
