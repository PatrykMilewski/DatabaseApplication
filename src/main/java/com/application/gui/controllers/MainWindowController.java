package com.application.gui.controllers;

import com.application.gui.elements.Contextable;
import com.application.gui.elements.DataTableContextMenu;
import com.application.gui.elements.FiltersContextMenu;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public class MainWindowController {
    
    private Contextable dataTableContextMenu, filtersContextMenu;
    
    @FXML
    private Pane mainPane;
    
    @FXML
    private MenuBar menuBar;

    @FXML
    private ScrollPane filtersPane;
    
    @FXML
    private ScrollPane dataPane;
    
    @FXML
    private TableView dataTable;
    
    @FXML
    private ListView filtersList;
    
    @FXML
    public void initialize() {
        dataTableContextMenu = new DataTableContextMenu();
        filtersContextMenu = new FiltersContextMenu();
    }
    
    @FXML
    public void showDataTableContextMenu() {
        dataTable.setContextMenu(dataTableContextMenu.getContextMenu());
    }

    @FXML
    public void showFiltersContextMenu() {
        filtersList.setContextMenu(filtersContextMenu.getContextMenu());
    }
    
}
