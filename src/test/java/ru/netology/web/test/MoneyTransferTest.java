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

        // Всегда переводим с первой карты, но только если на ней есть деньги
        // Используем Math.max чтобы убедиться что amount >= 1
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

        // Всегда переводим 1 рубль с первой карты на вторую
        var amount = 1;

        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
        dashboardPage = transferPage.makeTransfer(firstCard, amount);

        // Проверяем, что баланс изменился корректно
        // (если денег не было, тест упадет, что и покажет проблему)
        assertEquals(firstCardBalance - amount, dashboardPage.getCardBalance(firstCard));
        assertEquals(secondCardBalance + amount, dashboardPage.getCardBalance(secondCard));
    }

    @Test
    void shouldCancelTransfer() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        // Всегда пытаемся отменить перевод со второй карты на первую
        TransferPage transferPage = dashboardPage.selectCardToTransfer(firstCard);
        dashboardPage = transferPage.cancel();

        var newFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var newSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstCardBalance, newFirstCardBalance);
        assertEquals(secondCardBalance, newSecondCardBalance);
    }
}