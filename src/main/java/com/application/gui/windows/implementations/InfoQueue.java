package com.application.gui.windows.implementations;

import com.application.gui.abstracts.enums.InfoType;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InfoQueue implements Runnable {
    Queue<SingleLog> infoQueue = new ConcurrentLinkedQueue<>();
    
    @Override
    public void run() {
        if (!infoQueue.iterator().hasNext()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
        
        }
    }
    
    public void newInfo(String info, InfoType type) {
        infoQueue.add(new SingleLog(info, type));
        this.notify();
    }
}
