package com.cgon.executer;

import com.electrotank.electroserver5.extensions.BaseManagedObjectFactory;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorServiceManagedObjectFactory extends BaseManagedObjectFactory {
    private ExecutorService executor;
 
    @Override
    public void init( EsObjectRO params ) {
        int poolSize = params.getInteger( "pool-size", 20 );
        executor =
            new ThreadPoolExecutor( poolSize,
                                    poolSize,
                                    60,
                                    TimeUnit.SECONDS,
                                    new LinkedBlockingQueue<Runnable>(),
                                    new ThreadFactory() {
                                        final AtomicInteger threadNumber = new AtomicInteger( 1 );
 
                                        @Override
                                        public Thread newThread( Runnable r ) {
                                            return new Thread( r, "SecondThreadPool " + threadNumber.getAndIncrement() );
                                        }
                                    } );
    }
 
    @Override
    public ExecutorService acquireObject( EsObjectRO params ) {
        return executor;
    }
 
    @Override
    public void destroy() {
        executor.shutdown();
        super.destroy();
    }
}

