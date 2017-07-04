package com.application.gui.controllers;

import com.application.database.connection.MySQLConnection;
import com.application.gui.abstracts.Contextable;
import com.application.gui.abstracts.InfoType;
import com.application.gui.elements.DataTableContextMenu;
import com.application.gui.elements.FiltersContextMenu;
import com.application.gui.windows.LoginWindow;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.logging.Logger;

public class MainWindowController {
    
    private static Logger log = Logger.getLogger(MainWindowController.class.getCanonicalName());
    
    private LoginWindow loginWindow;
    private MySQLConnection databaseConnection;
    private Contextable dataTableContextMenu, filtersContextMenu;
    
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
    private Label infoLabel;
    
    @FXML
    public void initialize() {
        dataTableContextMenu = new DataTableContextMenu();
        filtersContextMenu = new FiltersContextMenu();
    }
    
    @FXML
    public void connectToDatabase() {
        try {
            loginWindow = new LoginWindow();
        }
        catch (IOException e) {
        
        }
    }
    
    @FXML
    public void disconnectFromDatabase() {
    
    }
    
    @FXML
    public void showDataTableContextMenu() {
        dataTable.setContextMenu(dataTableContextMenu.getContextMenu());
    }

    @FXML
    public void showFiltersContextMenu() {
        filtersList.setContextMenu(filtersContextMenu.getContextMenu());
    }
    
    private void addInfo(String message, InfoType type) {
    
    }
}
