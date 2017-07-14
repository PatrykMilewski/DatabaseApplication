package com.application.gui.elements.infobox;

import java.util.logging.Level;

public class SingleLog {
    
    private String text;
    private Level type;
    
    public SingleLog(String text, Level type) {
        this.text = text;
        this.type = type;
    }
    
    
    public Level getType() {
        return type;
    }
    
    public String getText() {
        return text;
    }
}
