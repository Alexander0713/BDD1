package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    private SelenideElement amountField = $("[data-test-id=amount] input");
    private SelenideElement fromField = $("[data-test-id=from] input");
    private SelenideElement transferButton = $("[data-test-id=action-transfer]");
    private SelenideElement cancelButton = $("[data-test-id=action-cancel]");
    private SelenideElement errorNotification = $("[data-test-id=error-notification]");

    public TransferPage() {
        amountField.shouldBe(visible);
        fromField.shouldBe(visible);
        transferButton.shouldBe(visible);
    }

    public DashboardPage makeTransfer(DataHelper.Card fromCard, int amount) {
        amountField.setValue(String.valueOf(amount));
        fromField.setValue(fromCard.getNumber());
        transferButton.click();
        return new DashboardPage();
    }

    public void makeInvalidTransfer(DataHelper.Card fromCard, int amount) {
        amountField.setValue(String.valueOf(amount));
        fromField.setValue(fromCard.getNumber());
        transferButton.click();
        errorNotification.shouldBe(visible);
    }

    public DashboardPage cancel() {
        cancelButton.click();
        return new DashboardPage();
    }
}
