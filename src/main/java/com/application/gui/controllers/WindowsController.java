package com.application.gui.controllers;

import com.application.gui.abstracts.exceptions.DuplicateMasterThreadException;
import com.application.gui.abstracts.exceptions.DuplicateSlaveThreadException;
import com.application.gui.abstracts.exceptions.UnknownMasterThreadException;
import com.application.gui.abstracts.exceptions.UnknownSlaveThreadException;

import java.util.*;

/**
 * Complexity of this class methods looks bad, but there are really
 * not much threads in application to make it a problem.
 */

public class WindowsController {
    private static Map<Thread, Set<Thread>> masterThreads;
    
    static {
        masterThreads = new LinkedHashMap<>();
    }
    
    public static void addMasterThread(Thread newMaster) throws DuplicateMasterThreadException {
        if (masterThreads.containsKey(newMaster))
            throw new DuplicateMasterThreadException(newMaster);
        
        int var = masterThreads.size();
        
        masterThreads.put(newMaster, new HashSet<>());
    }
    
    public static void addSlaveThread(Thread master, Thread slave)
            throws DuplicateSlaveThreadException, UnknownMasterThreadException {
        
        HashSet<Thread> slavesSet = (HashSet)masterThreads.get(master);
        if (slavesSet == null)
            throw new UnknownMasterThreadException(master);
        
        if (slavesSet.contains(slave))
            throw new DuplicateSlaveThreadException(slave);
        
        slavesSet.add(slave);
    }
    
    public static void sendResultsToMaster(Thread slave) throws UnknownSlaveThreadException {
        masterThreads.forEach((master, slavesSet) -> {
            if (slavesSet.contains(slave)) {
                master.notify();
                return;
            }
        });
        
        throw new UnknownSlaveThreadException(slave);
    }
    
    public static Map<Thread, Set<Thread>> getMastersThreads() {
        return masterThreads;
    }
    
    public static void reset() {
        masterThreads = new LinkedHashMap<>();
    }
}
