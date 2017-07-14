package com.application.gui.elements.contextmenus;

import javafx.scene.control.MenuItem;

public class FiltersContextMenu extends CustomContextMenu {
    
    public FiltersContextMenu() {
        super(3);
        menuItems[0] = new MenuItem("First action.");
        menuItems[1] = new MenuItem("Second action.");
        menuItems[2] = new MenuItem("Third action.");
        
        contextMenu.getItems().addAll(menuItems);
    
        menuItems[0].setOnAction(event -> action1());
        menuItems[1].setOnAction(event -> action2());
        menuItems[2].setOnAction(event -> action3());
    }
    
    private void action1() {
        System.out.println("Action 1");
    }
    
    private void action2() {
        System.out.println("Action 2");
    }
    
    private void action3() {
        System.out.println("Action 3");
    }
}
