package com.bemore.api;

import org.junit.Test;

import java.time.LocalDate;

public class TestA {

    @Test
    public void m1(){
        System.out.println(LocalDate.now().getMonthValue());
        System.out.println(LocalDate.now().getDayOfMonth());
    }
}
