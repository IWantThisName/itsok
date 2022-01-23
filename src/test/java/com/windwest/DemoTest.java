package com.windwest;

import java.util.concurrent.locks.ReentrantLock;

public class DemoTest {

    public void testReentrantLock() {
        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();
        try {
            //something to do
        } catch (Exception e) {
            reentrantLock.unlock();
        }
    }

}
