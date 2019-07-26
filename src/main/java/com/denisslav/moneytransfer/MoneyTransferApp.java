package com.denisslav.moneytransfer;

import com.denisslav.moneytransfer.api.TransferController;
import com.google.inject.Guice;
import static spark.Spark.port;

public class MoneyTransferApp {

    public static void main(String[] args) {
        port(8080);

        Guice.createInjector().getInstance(TransferController.class);
    }
}
