package com.ogawa.fico.service;

import java.util.concurrent.Callable;

public interface BeanEncapsulatingCallable<B> extends Callable<B> {

    B getBean();

}
