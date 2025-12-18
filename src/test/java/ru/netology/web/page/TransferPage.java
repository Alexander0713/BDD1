package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Condition;
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
        assertTransferFormVisible();
    }

    public void assertTransferFormVisible() {
        amountField.shouldBe(visible);
        fromField.shouldBe(visible);
        transferButton.shouldBe(visible);
    }

    public DashboardPage makeTransfer(DataHelper.Card fromCard, int amount) {
        return makeTransferWithExpectation(fromCard, amount, true);
    }

    public void makeInvalidTransfer(DataHelper.Card fromCard, int amount) {
        makeTransferWithExpectation(fromCard, amount, false);
        assertErrorNotificationVisible();
    }

    public void makeTransferWithError(DataHelper.Card fromCard, int amount, String expectedError) {
        makeTransferWithExpectation(fromCard, amount, false);
        assertErrorNotificationWithText(expectedError);
    }

    private DashboardPage makeTransferWithExpectation(DataHelper.Card fromCard, int amount, boolean expectSuccess) {
        fillTransferForm(fromCard, amount);
        transferButton.click();
        return expectSuccess ? new DashboardPage() : null;
    }

    public DashboardPage cancel() {
        cancelButton.click();
        return new DashboardPage();
    }

    public void assertErrorNotificationVisible() {
        errorNotification.shouldBe(visible);
    }

    public void assertErrorNotificationWithText(String expectedText) {
        errorNotification.shouldBe(visible)
                .shouldHave(Condition.text(expectedText));
    }

    public String getErrorNotificationText() {
        return errorNotification.text();
    }

    private void fillTransferForm(DataHelper.Card fromCard, int amount) {
        setFieldValue(amountField, String.valueOf(amount));
        setFieldValue(fromField, fromCard.getNumber());
    }

    private void setFieldValue(SelenideElement element, String value) {
        element.clear();
        element.setValue(value);
    }

    public void clearForm() {
        amountField.clear();
        fromField.clear();
    }
}