package com.ogawa.fico.application;

import com.ogawa.fico.function.Suppliers;
import java.util.function.Function;
import java.util.function.Supplier;

public class Test {


    static void test(Function<Integer, Integer> f) {
        System.out.println(f.apply(1));
    }


    public static void main(String[] args) {

        Supplier<String> s1;
        Supplier<String> s2;

        s1 = Suppliers.ofNull();
        s2 = Suppliers.ofNull();

        System.out.println(s1);
        System.out.println(s2);


    }

}
