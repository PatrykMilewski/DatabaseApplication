package com.application.gui.elements.infobox;

import java.util.logging.Level;

class SingleLog {
    
    private String text;
    private Level type;
    
    SingleLog(String text, Level type) {
        this.text = text;
        this.type = type;
    }
    
    
    Level getType() {
        return type;
    }
    
    String getText() {
        return text;
    }
}
