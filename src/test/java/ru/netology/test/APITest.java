package ru.netology.test;


import org.junit.jupiter.api.*;
import ru.netology.data.APIHelper;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;

import static ru.netology.data.SQLHelper.cleanAuthCodes;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class APITest {

    @AfterEach
    void tearDown() {
        cleanAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @Test
    @DisplayName("Перевод с первой карты на вторую")
    public void shouldTransferMoneyFromFirstToSecond() {
        var authInfo = DataHelper.getAuthInfoOfTestUser();
        APIHelper.makeQueryToLogin(authInfo, 200);
        var verificationCode = SQLHelper.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode.getCode());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);
        var cardBalance = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var firstCardBalance = cardBalance.get(DataHelper.getFirstCardInfo().getTestId());
        var secondCardBalance = cardBalance.get(DataHelper.getSecondCardInfo().getTestId());
        var amount = DataHelper.generateValidAmount(firstCardBalance);
        var expectedDBalanceFirstCard = firstCardBalance - amount;
        var expectedDBalanceSecondCard = secondCardBalance + amount;
        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getFirstCardInfo().getCardNumber(),
                DataHelper.getSecondCardInfo().getCardNumber(), amount);
        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);
        cardBalance = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var actualBalanceFirstCard = cardBalance.get(DataHelper.getFirstCardInfo().getTestId());
        var actualBalanceSecondCard = cardBalance.get(DataHelper.getSecondCardInfo().getTestId());
        Assertions.assertEquals(expectedDBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(expectedDBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    @DisplayName("Перевод со второй карты на первую")
    void shouldTransferMoneyFromSecondToFirst() {
        var authInfo = DataHelper.getAuthInfoOfTestUser();
        APIHelper.makeQueryToLogin(authInfo, 200);
        var verificationCode = SQLHelper.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode.getCode());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);
        var cardBalance = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var firstCardBalance = cardBalance.get(DataHelper.getFirstCardInfo().getTestId());
        var secondCardBalance = cardBalance.get(DataHelper.getSecondCardInfo().getTestId());
        var amount = DataHelper.generateValidAmount(secondCardBalance);
        var expectedDBalanceFirstCard = firstCardBalance + amount;
        var expectedDBalanceSecondCard = secondCardBalance - amount;
        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getSecondCardInfo().getCardNumber(),
                DataHelper.getFirstCardInfo().getCardNumber(), amount);
        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);
        cardBalance = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var actualBalanceFirstCard = cardBalance.get(DataHelper.getFirstCardInfo().getTestId());
        var actualBalanceSecondCard = cardBalance.get(DataHelper.getSecondCardInfo().getTestId());
        Assertions.assertEquals(expectedDBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(expectedDBalanceSecondCard, actualBalanceSecondCard);

    }
    @Test
    @DisplayName("Перевод средств больше баланса")
    void shouldNotTransferMoneyOverBalance() {
        var authInfo = DataHelper.getAuthInfoOfTestUser();
        APIHelper.makeQueryToLogin(authInfo, 200);
        var verificationCode = SQLHelper.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode.getCode());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);
        var cardBalance = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var firstCardBalance = cardBalance.get(DataHelper.getFirstCardInfo().getTestId());
        var secondCardBalance = cardBalance.get(DataHelper.getSecondCardInfo().getTestId());
        var amount = DataHelper.generateInValidAmount(firstCardBalance);
        var expectedDBalanceFirstCard = firstCardBalance - amount;
        var expectedDBalanceSecondCard = secondCardBalance + amount;
        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getFirstCardInfo().getCardNumber(),
                DataHelper.getSecondCardInfo().getCardNumber(), amount);
        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);
        cardBalance = APIHelper.sendQueryToGetCardsBalances(tokenInfo.getToken(), 200);
        var actualBalanceFirstCard = cardBalance.get(DataHelper.getFirstCardInfo().getTestId());
        var actualBalanceSecondCard = cardBalance.get(DataHelper.getSecondCardInfo().getTestId());
        Assertions.assertEquals(expectedDBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(expectedDBalanceSecondCard, actualBalanceSecondCard);
    }
}