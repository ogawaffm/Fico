package com.ogawa.fico.db.persistence.bindvarwriter;

import lombok.NonNull;

public interface BindVarWriter extends AutoCloseable {

    void write(@NonNull Object[] var);

    boolean isClosed();

}
