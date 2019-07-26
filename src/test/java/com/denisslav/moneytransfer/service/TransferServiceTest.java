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
import static org.junit.Assert.assertEquals;

/**
 * Stress tests.
 */
public class TransferServiceTest {

    public static final int ACCOUNTS_SIZE = 2000;

    public static final int ACCOUNTS_SIZE2 = 200000;
    public static final int OPS_SIZE = 20;

    @Test
    public void test() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        TransferService transferService = new TransferService();

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < ACCOUNTS_SIZE; i++) {
            accounts.add(transferService.createAccount(BigDecimal.valueOf(100000)));
        }

        long time = System.currentTimeMillis();
        for (long i = 0; i < ACCOUNTS_SIZE; i++) {
            final long from = i % ACCOUNTS_SIZE;
            for (long j = 0; j < ACCOUNTS_SIZE; j++) {
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
            assertEquals(BigDecimal.valueOf(100000), transferService.getAccount(i).getBalance().get());
        }
    }

    /**
     * 100.000 transfers to each account (total 20).
     */
    @Test
    public void test2() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        TransferService transferService = new TransferService();

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < ACCOUNTS_SIZE2; i++) {
            accounts.add(transferService.createAccount(BigDecimal.valueOf(OPS_SIZE)));
        }

        for (int i = ACCOUNTS_SIZE2; i < ACCOUNTS_SIZE2 + OPS_SIZE; i++) {
            accounts.add(transferService.createAccount(BigDecimal.valueOf(0)));
        }

        long time = System.currentTimeMillis();
        for (long i = 0; i < ACCOUNTS_SIZE2; i++) {
            final long from = i;
            for (long j = ACCOUNTS_SIZE2; j < ACCOUNTS_SIZE2 + OPS_SIZE; j++) {
                final long to = j;

                executor.execute(() -> {
                    transferService.transfer(from, to, BigDecimal.valueOf(1));
                });
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.err.println("Time spend: " + (System.currentTimeMillis() - time));
        for (long i = ACCOUNTS_SIZE2; i < ACCOUNTS_SIZE2 + OPS_SIZE; i++) {
            assertEquals(transferService.getAccount(i).getBalance().get(), BigDecimal.valueOf(ACCOUNTS_SIZE2));
        }
    }
}
