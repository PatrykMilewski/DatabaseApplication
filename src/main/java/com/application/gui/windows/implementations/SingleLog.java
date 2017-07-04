package com.application.gui.windows.implementations;

import com.application.gui.abstracts.InfoType;

public class SingleLog {
    String text;
    InfoType logType;
    
    SingleLog(String text, InfoType logType) {
        this.text = text;
        this.logType = logType;
    }
}
