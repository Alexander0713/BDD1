package ru.netology.web.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.TransferPage;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTransferTest {
    DashboardPage dashboardPage;
    DataHelper.Card firstCard;
    DataHelper.Card secondCard;

    private int initialFirstCardBalance;
    private int initialSecondCardBalance;

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);

        firstCard = DataHelper.getFirstCard();
        secondCard = DataHelper.getSecondCard();

        initialFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        initialSecondCardBalance = dashboardPage.getCardBalance(secondCard);
    }

    @AfterEach
    void tearDown() {

        restoreInitialBalances();
    }

    private void restoreInitialBalances() {
        int currentFirstBalance = dashboardPage.getCardBalance(firstCard);
        int currentSecondBalance = dashboardPage.getCardBalance(secondCard);


        if (currentFirstBalance != initialFirstCardBalance ||
                currentSecondBalance != initialSecondCardBalance) {


            if (currentFirstBalance != initialFirstCardBalance) {
                int difference = initialFirstCardBalance - currentFirstBalance;
                if (difference != 0) {

                    if (difference > 0) {

                        TransferPage transferPage = dashboardPage.selectCardToTransfer(firstCard);
                        dashboardPage = transferPage.makeTransfer(secondCard, difference);
                    } else {

                        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
                        dashboardPage = transferPage.makeTransfer(firstCard, Math.abs(difference));
                    }
                }
            }


            currentFirstBalance = dashboardPage.getCardBalance(firstCard);
            currentSecondBalance = dashboardPage.getCardBalance(secondCard);

            if (currentSecondBalance != initialSecondCardBalance) {
                int difference = initialSecondCardBalance - currentSecondBalance;
                if (difference != 0) {
                    if (difference > 0) {
                        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
                        dashboardPage = transferPage.makeTransfer(firstCard, difference);
                    } else {
                        TransferPage transferPage = dashboardPage.selectCardToTransfer(firstCard);
                        dashboardPage = transferPage.makeTransfer(secondCard, Math.abs(difference));
                    }
                }
            }
        }
    }

    @Test
    void shouldTransferFromFirstToSecondCard() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        var amount = DataHelper.generateValidAmount(firstCardBalance);

        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
        dashboardPage = transferPage.makeTransfer(firstCard, amount);

        var newFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var newSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstCardBalance - amount, newFirstCardBalance);
        assertEquals(secondCardBalance + amount, newSecondCardBalance);
    }

    @Test
    void checkNoNegativeBalanceBug() {
        var balance = dashboardPage.getCardBalance(firstCard);

        var transferPage = dashboardPage.selectCardToTransfer(secondCard);
        var newDashboard = transferPage.makeTransfer(firstCard, balance + 1000);

        var newBalance = newDashboard.getCardBalance(firstCard);

        var isBalanceValid = newBalance >= 0;
        assertTrue(isBalanceValid,
                "Отрицательный баланс: " + newBalance);
    }

    @Test
    void shouldTransferFromSecondToFirstCard() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        var amount = DataHelper.generateValidAmount(secondCardBalance);

        TransferPage transferPage = dashboardPage.selectCardToTransfer(firstCard);
        dashboardPage = transferPage.makeTransfer(secondCard, amount);

        var newFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var newSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstCardBalance + amount, newFirstCardBalance);
        assertEquals(secondCardBalance - amount, newSecondCardBalance);
    }

    @Test
    void shouldNotTransferMoreThanBalance() {
        var balance = dashboardPage.getCardBalance(firstCard);

        var transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.makeInvalidTransfer(firstCard, balance + 1000);


        dashboardPage = new DashboardPage();
        assertEquals(balance, dashboardPage.getCardBalance(firstCard));
    }

    @Test
    void shouldTransferMaximumAmount() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);


        if (firstCardBalance > 0) {
            TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
            dashboardPage = transferPage.makeTransfer(firstCard, firstCardBalance);

            var newFirstCardBalance = dashboardPage.getCardBalance(firstCard);
            var newSecondCardBalance = dashboardPage.getCardBalance(secondCard);

            assertEquals(0, newFirstCardBalance);
            assertEquals(secondCardBalance + firstCardBalance, newSecondCardBalance);
        }
    }

    @Test
    void shouldTransferOneRuble() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);


        if (firstCardBalance >= 1) {
            TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
            dashboardPage = transferPage.makeTransfer(firstCard, 1);

            assertEquals(firstCardBalance - 1, dashboardPage.getCardBalance(firstCard));
            assertEquals(secondCardBalance + 1, dashboardPage.getCardBalance(secondCard));
        } else if (secondCardBalance >= 1) {
            TransferPage transferPage = dashboardPage.selectCardToTransfer(firstCard);
            dashboardPage = transferPage.makeTransfer(secondCard, 1);

            assertEquals(firstCardBalance + 1, dashboardPage.getCardBalance(firstCard));
            assertEquals(secondCardBalance - 1, dashboardPage.getCardBalance(secondCard));
        }
    }

    @Test
    void shouldCancelTransfer() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);


        if (firstCardBalance > 0) {
            TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
            dashboardPage = transferPage.cancel();

            var newFirstCardBalance = dashboardPage.getCardBalance(firstCard);
            var newSecondCardBalance = dashboardPage.getCardBalance(secondCard);

            assertEquals(firstCardBalance, newFirstCardBalance);
            assertEquals(secondCardBalance, newSecondCardBalance);
        } else if (secondCardBalance > 0) {
            TransferPage transferPage = dashboardPage.selectCardToTransfer(firstCard);
            dashboardPage = transferPage.cancel();

            var newFirstCardBalance = dashboardPage.getCardBalance(firstCard);
            var newSecondCardBalance = dashboardPage.getCardBalance(secondCard);

            assertEquals(firstCardBalance, newFirstCardBalance);
            assertEquals(secondCardBalance, newSecondCardBalance);
        }
    }
}
