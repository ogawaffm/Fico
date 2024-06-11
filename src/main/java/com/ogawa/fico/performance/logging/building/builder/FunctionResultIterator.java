package com.ogawa.fico.performance.logging.building.builder;

import java.util.Iterator;
import java.util.function.Function;

public class FunctionResultIterator<FUNC_ARG, FUNC_RESULT> implements Iterator<FUNC_RESULT> {

    private Iterator<FUNC_ARG> argumentIterator;
    private Function<FUNC_ARG, FUNC_RESULT> function;

    public FunctionResultIterator(Iterator<FUNC_ARG> argumentIterator, Function<FUNC_ARG, FUNC_RESULT> function) {
        this.argumentIterator = argumentIterator;
        this.function = function;
    }

    @Override
    public boolean hasNext() {
        return argumentIterator.hasNext();
    }

    @Override
    public FUNC_RESULT next() {
        return function.apply(argumentIterator.next());
    }

}
