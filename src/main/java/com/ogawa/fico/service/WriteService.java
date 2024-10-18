package com.ogawa.fico.service;

import static com.ogawa.fico.db.Util.closeSilently;

import com.ogawa.fico.db.persistence.beanwriter.Creator;
import com.ogawa.fico.db.persistence.beanwriter.Updater;
import com.ogawa.fico.db.persistence.factory.PersistenceFactory;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.multithreading.ExtendedExecutorCompletionService;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteService<B> extends DequeService<B> {

    static class Persister<B> implements Consumer<B> {

        private final RowMapper<B> rowMapper;

        private final Creator<B> creator;

        private final Updater<B> updater;

        @Getter
        private long updatedCount = 0;

        @Getter
        private long createdCount = 0;

        Persister(PersistenceFactory<B> persistenceFactory) {
            this.rowMapper = persistenceFactory.getRowMapper();
            this.creator = persistenceFactory.createCreator();
            this.updater = persistenceFactory.createUpdater();
        }

        @Override
        public void accept(B bean) {
            if (rowMapper.isPrimaryKeySet(bean)) {
                updater.update(bean);
                updatedCount++;
            } else {
                creator.create(bean);
                createdCount++;
            }
        }

        public void close() {
            closeSilently(updater);
            closeSilently(creator);
        }
    }

    static public <B> WriteService create(
        @NonNull PersistenceFactory<B> persistenceFactory,
        @NonNull ExtendedExecutorCompletionService<B> executorCompletionService,
        @NonNull List<ThreadPoolExecutor> producers
    ) {
        return new WriteService<>(persistenceFactory, executorCompletionService, producers);
    }

    private WriteService(
        @NonNull PersistenceFactory<B> persistenceFactory,
        @NonNull ExtendedExecutorCompletionService<B> executorCompletionService,
        @NonNull List<ThreadPoolExecutor> producers) {
        super(executorCompletionService, producers, new Persister<B>(persistenceFactory));
    }

    @Override
    public void stop() {
        ((Persister<B>) consumer).close();
        log.debug("{} created {} rows and updated {} rows",
            getServiceName(),
            ((Persister<B>) consumer).getCreatedCount(),
            ((Persister<B>) consumer).getUpdatedCount()
        );
        super.stop();
    }

}
