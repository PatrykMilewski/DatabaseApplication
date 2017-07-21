package com.application.gui.elements.contextmenus;

import javafx.scene.control.ContextMenu;

import java.awt.event.ActionEvent;

public interface Contextable {
    ContextMenu getContextMenu(ActionEvent event);
}
