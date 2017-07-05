package com.application.gui.abstracts.exceptions;

import java.util.Arrays;

public class UnknownSlaveThreadException extends Exception {
    public UnknownSlaveThreadException(Thread input) {
        super("Duplicated thread name: "
                + input.getName() + " id: " + input.getId()
                + "\n" + Arrays.toString(input.getStackTrace()));
    }
}
