package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
    private SelenideElement codeField = $("[data-test-id=code] input");
    private SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private SelenideElement errorNotification = $("[data-test-id=error-notification]");

    public VerificationPage() {
        codeField.shouldBe(visible);
        verifyButton.shouldBe(visible);
    }

    public DashboardPage validVerify(DataHelper.VerificationCode verificationCode) {
        enterVerificationCode(verificationCode);
        verifyButton.click();
        return new DashboardPage();
    }

    public void assertErrorNotificationWithText(String expectedText) {
        errorNotification.shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    private void enterVerificationCode(DataHelper.VerificationCode verificationCode) {
        codeField.setValue(verificationCode.getCode());
    }
}