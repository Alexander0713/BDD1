package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private ElementsCollection cards = $$(".list__item div[data-test-id]");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public DashboardPage() {
        heading.shouldBe(visible);
        cards.first().shouldBe(visible);
    }

    public int getCardBalance(DataHelper.Card card) {
        var cardElement = cards.findBy(Condition.attribute("data-test-id", card.getTestId()));
        return extractBalance(cardElement.text());
    }

    private int extractBalance(String text) {
        var start = text.indexOf(balanceStart);
        var finish = text.indexOf(balanceFinish);
        var value = text.substring(start + balanceStart.length(), finish).trim();
        return Integer.parseInt(value);
    }

    public TransferPage selectCardToTransfer(DataHelper.Card toCard) {
        $("[data-test-id='" + toCard.getTestId() + "'] [data-test-id=action-deposit]").click();
        return new TransferPage();
    }
}