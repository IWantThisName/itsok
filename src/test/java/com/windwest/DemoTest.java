package com.windwest;

import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

public class DemoTest {

    @Test
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
