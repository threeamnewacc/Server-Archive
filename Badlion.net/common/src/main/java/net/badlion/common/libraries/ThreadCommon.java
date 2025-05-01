package net.badlion.common.libraries;

import java.util.ArrayList;
import java.util.List;

public class ThreadCommon {

    public static void callThreads(int numOfThreads, ThreadRunnable runnable) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numOfThreads; i++) {
            ThreadRunnable threadRunnable = runnable.clone();
            threadRunnable.setThreadId(i);
            Thread t = new Thread(threadRunnable);
            t.start();
            threads.add(t);
        }

        for (int i = 0; i < numOfThreads; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract static class ThreadRunnable implements Runnable, Cloneable {

        private int threadId;

        public int getThreadId() {
            return this.threadId;
        }

        public void setThreadId(int threadId) {
            this.threadId = threadId;
        }

        public ThreadRunnable clone() {
            try {
                return (ThreadRunnable) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }
}
