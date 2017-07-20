package com.application.gui.elements.containers;

import com.application.gui.abstracts.consts.enums.RemoveResults;
import com.application.gui.abstracts.types.MutableBool;

import java.util.*;

public class LockingHashMap<E> {
    
    private Map<E, MutableBool> elementAndLock = new HashMap<>();
    
    public boolean isLocked(E element) {
        MutableBool lock = elementAndLock.get(element);
        
        return !lock.getValue();
    }
    
    public void unlockElement(E element) {
        elementAndLock.get(element).setFalse();
    }
    
    public boolean contains(E element) {
        return elementAndLock.containsKey(element);
    }
    
    public void add(E element) {
        elementAndLock.put(element, new MutableBool(false));
    }
    
    public void addAndLock(E element) {
        elementAndLock.put(element, new MutableBool(true));
    }
    
    public RemoveResults remove(E element) {
        if (elementAndLock.containsKey(element)) {
            MutableBool lock = elementAndLock.get(element);
            if (!lock.getValue()) {
                elementAndLock.remove(element);
                return RemoveResults.REMOVED;
            }
            else
                return RemoveResults.LOCKED;
        }
        return RemoveResults.NOTFOUND;
    }
    
    public boolean forceRemove(E element) {
        if (elementAndLock.containsKey(element)) {
            elementAndLock.remove(element);
            return true;
        }
        return false;
    }
}
