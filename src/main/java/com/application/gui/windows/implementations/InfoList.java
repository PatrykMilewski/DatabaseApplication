package com.application.gui.windows.implementations;

import java.util.LinkedList;
import java.util.List;

public class InfoList implements Runnable {
    List<SingleLog> list = new LinkedList<>();
    
    
    @Override
    public void run() {
        if (!list.iterator().hasNext()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
        
        }
    }
    
}
