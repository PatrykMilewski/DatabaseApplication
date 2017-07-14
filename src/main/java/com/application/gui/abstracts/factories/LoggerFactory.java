package com.application.gui.abstracts.factories;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {
    
    private final static boolean DEBUG = true;
    private final static Level DEBUGLEVEL = Level.ALL;
    private final static Level NORMALLEVEL = Level.SEVERE;
    
    private LoggerFactory() { }
    
    public static Logger getLogger(String name) {
        Logger newLogger = Logger.getLogger(name);
        if (DEBUG)
            newLogger.setLevel(DEBUGLEVEL);
        else
            newLogger.setLevel(NORMALLEVEL);
        
        return newLogger;
    }
}
