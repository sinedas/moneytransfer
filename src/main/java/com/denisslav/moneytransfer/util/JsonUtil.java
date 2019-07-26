package com.denisslav.moneytransfer.util;

import com.denisslav.moneytransfer.dto.AccountDto;
import com.denisslav.moneytransfer.dto.InitialBalanceDto;
import com.denisslav.moneytransfer.dto.TransactionDto;
import com.denisslav.moneytransfer.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String convertFrom(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }

    public static InitialBalanceDto convertToBalance(String json) throws IOException {
        return mapper.readValue(json, InitialBalanceDto.class);
    }

    public static TransactionDto convertToTransaction(String json) throws IOException {
        return mapper.readValue(json, TransactionDto.class);
    }

    public static AccountDto convertToAccount(String json) throws IOException {
        return mapper.readValue(json, AccountDto.class);
    }

    public static List convertToList(String json) throws IOException {
        return mapper.readValue(json, List.class);
    }
}
