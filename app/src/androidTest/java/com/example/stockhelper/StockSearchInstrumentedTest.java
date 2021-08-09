package com.example.stockhelper;

import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SearchView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;


public class StockSearchInstrumentedTest {

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(Login.class);


    @Test
    public void searchingAppleStock() throws InterruptedException{
        ActivityScenario scenario = rule.getScenario();
        Thread.sleep(5000);
        onView(withId(R.id.menuSearchButton)).perform(click());
        onView(withContentDescription("Search")).perform(click());
        onView(withId(R.id.search_src_text)).perform(typeText("Apple"), pressImeActionButton());
        onData(anything()).inAdapterView(withId(R.id.listItem)).atPosition(0).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.stockSymbol)).check(matches(withText("AAPL")));
    }


    @Test
    public void searchingTeslaStock() throws InterruptedException{
        ActivityScenario scenario = rule.getScenario();
        Thread.sleep(5000);
        onView(withId(R.id.menuSearchButton)).perform(click());
        onView(withContentDescription("Search")).perform(click());
        onView(withId(R.id.search_src_text)).perform(typeText("Tesla"), pressImeActionButton());
        onData(anything()).inAdapterView(withId(R.id.listItem)).atPosition(0).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.stockSymbol)).check(matches(withText("TSLA")));
    }
}