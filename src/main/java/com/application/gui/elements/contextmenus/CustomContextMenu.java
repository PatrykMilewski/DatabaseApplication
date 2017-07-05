package com.application.gui.elements.contextmenus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

abstract class CustomContextMenu implements Contextable {
    
    final ContextMenu contextMenu;
    final MenuItem menuItems[];
    
    CustomContextMenu(int menuItemsAmount) {
        contextMenu = new ContextMenu();
        menuItems = new MenuItem[menuItemsAmount];
    }
    
    @Override
    public ContextMenu getContextMenu() {
        return contextMenu;
    }
}
