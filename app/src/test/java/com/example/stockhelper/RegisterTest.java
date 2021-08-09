package com.example.stockhelper;


import org.junit.Test;


import static org.junit.Assert.assertEquals;


public class RegisterTest {


    @Test
    public void validateUsername() {
        Register testedActivity = new Register();
        assertEquals(1, testedActivity.ValidateUsername(""));
        assertEquals(2, testedActivity.ValidateUsername("abc"));
        assertEquals(3, testedActivity.ValidateUsername("abcdeflkmijwsrkla"));
        assertEquals(0, testedActivity.ValidateUsername("testname"));
    }

    @Test
    public void validateEmail() {
        Register testedActivity = new Register();
        assertEquals(1, testedActivity.ValidateEmail(""));
        assertEquals(2, testedActivity.ValidateEmail("abcabcabcabccom"));
        assertEquals(3, testedActivity.ValidateEmail("a@g.com"));
        assertEquals(4, testedActivity.ValidateEmail("testasdasdasdasddasdsaasdadsdasasdsadsadsadsadsdsadsadsadasdasdasdasdsadsadsaddsdasdasddasdsadsaddasd@as.com"));
        assertEquals(0, testedActivity.ValidateEmail("testmail123@test.com"));
        assertEquals(0, testedActivity.ValidateEmail("testmail123@test.com"));
    }

    @Test
    public void validatePassword() {
        Register testedActivity = new Register();
        assertEquals(1, testedActivity.ValidatePassword("",""));
        assertEquals(1, testedActivity.ValidatePassword("","123456"));
        assertEquals(2, testedActivity.ValidatePassword("123456",""));
        assertEquals(3, testedActivity.ValidatePassword("123456","654321"));
        assertEquals(4, testedActivity.ValidatePassword("1234","1234"));
        assertEquals(5, testedActivity.ValidatePassword("1234567891011121314151617181920212223242526272829303132333435","1234567891011121314151617181920212223242526272829303132333435"));
        assertEquals(0, testedActivity.ValidatePassword("123456","123456"));
    }
}