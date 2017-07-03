package com.application.gui.elements;

import javafx.scene.control.MenuItem;

public class DataTableContextMenu extends CustomContextMenu {
    
    public DataTableContextMenu() {
        super(3);
        menuItems[0] = new MenuItem("First action.");
        menuItems[1] = new MenuItem("Second action.");
        menuItems[2] = new MenuItem("Third action.");
    
        contextMenu.getItems().addAll(menuItems);
        
        menuItems[0].setOnAction(event -> System.out.println("Action 1"));
        menuItems[1].setOnAction(event -> System.out.println("Action 2"));
        menuItems[2].setOnAction(event -> System.out.println("Action 3"));
    }
}
