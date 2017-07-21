package com.application.gui.abstracts.factories;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {
    
    private final static boolean DEBUG = true;
    private final static Level DEBUG_LEVEL = Level.ALL;
    private final static Level NORMAL_LEVEL = Level.SEVERE;
    
    private LoggerFactory() { }
    
    public static Logger getLogger(String name) {
        Logger newLogger = Logger.getLogger(name);
        if (DEBUG)
            newLogger.setLevel(DEBUG_LEVEL);
        else
            newLogger.setLevel(NORMAL_LEVEL);
        
        return newLogger;
    }
}
