package ch.scaille.testing.bdd.selenium;

import static ch.scaille.testing.bdd.definition.Steps.step;
import static ch.scaille.testing.bdd.definition.Steps.automationStep;

import ch.scaille.testing.bdd.definition.Steps;
import ch.scaille.testing.bdd.definition.TestDictionary;
import ch.scaille.util.helpers.LambdaExt;

import ch.scaille.tcwriter.pilot.selenium.AbstractTestWebAppProvider;

import java.net.URL;

public class AppSteps {
    /**
     * BDD
     **/

    public static final TestDictionary<AppPages> BDD_FACTORY = new TestDictionary<>();

    public static final Steps<AppPages> OPEN_WEBSITE = BDD_FACTORY.with(
            step("I open the website", LambdaExt.uncheckC(p -> p.driver.get(new URL(AbstractTestWebAppProvider.localUrl, "/example1.html").toString()))),
            step("I see that the website is open", p -> p.examplePage.testEnabled()));

    public static final Steps<AppPages> TEST_ENABLE = BDD_FACTORY.with(
            step("I execute the Enable function", p -> p.examplePage.testEnable()),
            step("I see that the Enable function is disabled|And back to normal after some seconds",
                    p -> p.examplePage.testEnabled()));

    public static final Steps<AppPages> TEST_ALERT = BDD_FACTORY.with(
            automationStep("I expect the Alert", p -> p.examplePage.expectTestAlertDialog()),
            step("I test the Alert function", p -> p.examplePage.testAlert()),
            step("I see that the Alert was raised|I acknowledge the Alert", p -> {
                p.examplePage.checkDialogHandled();
                System.out.println(p.getContext().example);
            }));

}
