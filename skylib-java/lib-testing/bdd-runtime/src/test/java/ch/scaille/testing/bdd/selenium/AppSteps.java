package ch.scaille.testing.bdd.selenium;

import static ch.scaille.testing.bdd.definition.Steps.automationStep;
import static ch.scaille.testing.bdd.definition.Steps.step;

import ch.scaille.tcwriter.pilot.selenium.AbstractSeleniumUndertowTest;
import ch.scaille.testing.bdd.definition.Steps;
import ch.scaille.testing.bdd.definition.TestDictionary;
import ch.scaille.util.helpers.LambdaExt;

public class AppSteps {
    /**
     * BDD
     **/

    public static final TestDictionary<AppPages> BDD_FACTORY = new TestDictionary<>();

    public static final Steps<AppPages> OPEN_WEBSITE = BDD_FACTORY.with(
            step("I open the website", LambdaExt.uncheckedC(p -> p.driver.get(AbstractSeleniumUndertowTest.localUrl.resolve("example1.html").toString()))),
            step("I see that the website is open", p -> p.examplePage.assertedEnabledTested()));

    public static final Steps<AppPages> TEST_ENABLE = BDD_FACTORY.with(
            step("I execute the Enable function", p -> p.examplePage.executeEnable()),
            step("I see that the Enable function is disabled|And back to normal after some seconds",
                    p -> p.examplePage.assertedEnabledTested()));

    public static final Steps<AppPages> TEST_ALERT = BDD_FACTORY.with(
            automationStep("I expect the Alert", p -> p.examplePage.expectTestAlertDialog()),
            step("I test the Alert function", p -> p.examplePage.testAlert()),
            step("I see that the Alert was raised|I acknowledge the Alert", p -> {
                p.examplePage.assertDialogHandled();
                System.out.println(p.getContext().example);
            }));

}
