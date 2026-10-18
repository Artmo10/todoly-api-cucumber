package runner;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Runner {

    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println("=== START: " + scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        System.out.println("=== END: " + scenario.getName() + " -> " + scenario.getStatus());
    }
}