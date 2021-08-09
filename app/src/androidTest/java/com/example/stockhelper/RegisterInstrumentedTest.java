package com.example.stockhelper;


import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;



public class RegisterInstrumentedTest {

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(Register.class);

    @Test
    public void registerTest() throws InterruptedException {
        ActivityScenario scenario = rule.getScenario();
        String username = RandomStringUtils.random(9, true, false);
        String email = username + "@gmail.com";
        String password = RandomStringUtils.random(9, true, true);

        onView(withId(R.id.registerUsername)).perform(clearText(), typeText(username), closeSoftKeyboard());
        onView(withId(R.id.registerEmail)).perform(clearText(), typeText(email), closeSoftKeyboard());
        onView(withId(R.id.registerPassword)).perform(clearText(), typeText(password),closeSoftKeyboard());
        onView(withId(R.id.registerRepeatedPassword)).perform(clearText(), typeText(password),closeSoftKeyboard());
        onView(withId(R.id.registerButton)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.menuSearchButton)).perform(click());
    }


}
