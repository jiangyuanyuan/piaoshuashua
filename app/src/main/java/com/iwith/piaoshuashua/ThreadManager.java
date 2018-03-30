package com.iwith.piaoshuashua;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class ThreadManager {
    //网络请求、3G、4G
    private static ThreadPoolProxy mNormalPool   = null;

    public static ThreadPoolProxy getNormalPool() {
        if(mNormalPool == null){
            synchronized (ThreadManager.class){
                if(mNormalPool == null){
                    mNormalPool = new ThreadPoolProxy(5, 5, 5 * 1000);
                    return mNormalPool;
                }
            }
        }
        return mNormalPool;
    }

    public static ThreadPoolProxy getDownLoadPool() {
        if(mNormalPool == null){
            synchronized (ThreadManager.class){
                if(mNormalPool == null){
                    mNormalPool = new ThreadPoolProxy(1, 1, 5 * 1000);
                    return mNormalPool;
                }
            }
        }
        return mNormalPool;
    }
    public static class ThreadPoolProxy {
        private final int                mCorePoolSize;
        private final int                mMaximumPoolSize;
        private final long               mKeepAliveTime;
        private ThreadPoolExecutor mPool;


        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.mCorePoolSize = corePoolSize;
            this.mMaximumPoolSize = maximumPoolSize;
            this.mKeepAliveTime = keepAliveTime;
        }

        private void initPool() {
            if (mPool == null || mPool.isShutdown()) {
                TimeUnit unit      = TimeUnit.MILLISECONDS;//单位
                BlockingQueue<Runnable> workQueue = null;//阻塞队列
                workQueue = new ArrayBlockingQueue<Runnable>(10);//FIFO,大小有限制
                ThreadFactory threadFactory = Executors.defaultThreadFactory();//线程工厂
                RejectedExecutionHandler handler = null;//异常捕获器
                handler = new ThreadPoolExecutor.DiscardPolicy();//不做任何处理
                mPool = new ThreadPoolExecutor(mCorePoolSize,
                        mMaximumPoolSize,
                        mKeepAliveTime,
                        unit,
                        workQueue,
                        threadFactory,
                        handler);
            }
        }

        /**
         * 执行任务
         * @param task
         */
        public void execute(Runnable task) {
            initPool();

            //执行任务
            mPool.execute(task);
        }


        public Future<?> submit(Runnable task) {
            initPool();
            Future future = mPool.submit(task);
            return mPool.submit(task);
        }

        public void remove(Runnable task) {
            if (mPool != null && !mPool.isShutdown()) {
                mPool.getQueue()
                        .remove(task);
            }
        }
        public boolean haveRunnable( ) {
            if (mPool != null && !mPool.isShutdown()) {
              if (mPool.getActiveCount()==0){
                  return false;
              }
            }
            return true;
        }

    }

}
