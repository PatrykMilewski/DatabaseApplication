package com.application.gui.elements.controllers;

import java.util.LinkedHashSet;
import java.util.Set;

public class ThreadsController {

    private Set<Thread> threadSet = new LinkedHashSet<>();
    
    
    public void addThread(Thread thread) {
        threadSet.add(thread);
    }
    
    public void removeThread(Thread thread) {
        threadSet.remove(thread);
    }
    
    public void killThreads() {
        for (Thread thread : threadSet) {
            if (thread != null)
                thread.interrupt();
        }
    }
    
}
