package ru.netology.web.test;

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
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        var transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.makeInvalidTransfer(firstCard, firstCardBalance + 1000);

        dashboardPage = new DashboardPage();

        var newFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var newSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstCardBalance, newFirstCardBalance);
        assertEquals(secondCardBalance, newSecondCardBalance);
    }

    @Test
    void shouldTransferMaximumAmount() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        var amount = Math.max(1, firstCardBalance);

        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
        dashboardPage = transferPage.makeTransfer(firstCard, amount);

        var newFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var newSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstCardBalance - amount, newFirstCardBalance);
        assertEquals(secondCardBalance + amount, newSecondCardBalance);
    }

    @Test
    void shouldTransferOneRuble() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        var amount = 1;

        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
        dashboardPage = transferPage.makeTransfer(firstCard, amount);

        assertEquals(firstCardBalance - amount, dashboardPage.getCardBalance(firstCard));
        assertEquals(secondCardBalance + amount, dashboardPage.getCardBalance(secondCard));
    }

    @Test
    void shouldCancelTransfer() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        TransferPage transferPage = dashboardPage.selectCardToTransfer(firstCard);
        dashboardPage = transferPage.cancel();

        var newFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var newSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstCardBalance, newFirstCardBalance);
        assertEquals(secondCardBalance, newSecondCardBalance);
    }
}