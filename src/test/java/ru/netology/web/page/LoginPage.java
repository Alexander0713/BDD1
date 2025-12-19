package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Condition;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private SelenideElement loginField = $("[data-test-id=login] input");
    private SelenideElement passwordField = $("[data-test-id=password] input");
    private SelenideElement loginButton = $("[data-test-id=action-login]");
    private SelenideElement errorNotification = $("[data-test-id=error-notification]");

    public LoginPage() {
        assertLoginFormVisible();
    }

    public void assertLoginFormVisible() {
        loginField.shouldBe(visible);
        passwordField.shouldBe(visible);
        loginButton.shouldBe(visible);
    }

    public VerificationPage validLogin(DataHelper.AuthInfo authInfo) {
        loginWithCredentials(authInfo);
        return new VerificationPage();
    }

    private void loginWithCredentials(DataHelper.AuthInfo authInfo) {
        fillAuthForm(authInfo);
        loginButton.click();
    }

    public void assertErrorNotificationWithText(String expectedText) {
        errorNotification.shouldBe(visible)
                .shouldHave(Condition.text(expectedText));
    }

    private void fillAuthForm(DataHelper.AuthInfo authInfo) {
        setFieldValue(loginField, authInfo.getLogin());
        setFieldValue(passwordField, authInfo.getPassword());
    }

    private void setFieldValue(SelenideElement element, String value) {
        element.clear();
        element.setValue(value);
    }
}