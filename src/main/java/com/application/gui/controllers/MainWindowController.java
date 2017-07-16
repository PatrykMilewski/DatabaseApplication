package com.application.gui.controllers;

import com.application.gui.abstracts.consts.values.ConstValues;
import com.application.gui.abstracts.exceptions.FailedToConnectToDatabase;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.contextmenus.Contextable;
import com.application.gui.elements.controllers.ThreadsController;
import com.application.gui.elements.contextmenus.DataTableContextMenu;
import com.application.gui.elements.contextmenus.FiltersContextMenu;
import com.application.gui.elements.infobox.LogBox;
import com.application.gui.windows.LoginWindow;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindowController extends Controller {
    
    private static Logger log = LoggerFactory.getLogger(MainWindowController.class.getCanonicalName());
    
    private ThreadsController threadsController;
    private LoginWindow loginWindow;
    private LogBox logBox;
    
    private Connection databaseConnection;
    private Contextable dataTableContextMenu, filtersContextMenu;
    
    private boolean connectedToDatabase = false;
    
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
    private Label logLabel;
    
    @FXML
    public void initialize() {
        resultsReady = true;
        logBox = new LogBox(logLabel);
        threadsController = new ThreadsController();
        dataTableContextMenu = new DataTableContextMenu();
        filtersContextMenu = new FiltersContextMenu();
    }
    
    @FXML
    public void connectToDatabase() {
        try {
            loginWindow = new LoginWindow();
            Thread connectionWorker = new Thread(this::waitForDBConnection);
            connectionWorker.start();
            threadsController.addThread(connectionWorker);
        } catch (IOException e) {
            addLog(Level.SEVERE, "Nie udało się otworzyć okna logowania.");
            e.printStackTrace();
        }
    }
    
    @FXML
    public void resetSettings() {
        Model model = ConstValues.getModel();
        String appData = System.getenv("APPDATA");
        String dataPath = Paths.get(appData, model.getArtifactId(), model.getVersion()).toString();
    
        try {
            FileUtils.deleteDirectory(new File(dataPath));
            addLog(Level.INFO, "Usunięto dane aplikacji.");
            log.log(Level.INFO, "Deleted application data.");
        } catch (IOException e) {
            addLog(Level.SEVERE, "Nie udało się usunąć danych aplikacji.");
            log.log(Level.SEVERE, "Failed to delete application data.");
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    @FXML
    public void disconnectFromDatabase() {
        closeDBConnection(true);
    }
    
    @FXML
    public void showDataTableContextMenu() {
        dataTable.setContextMenu(dataTableContextMenu.getContextMenu());
    }
    
    @FXML
    public void showFiltersContextMenu() {
        filtersList.setContextMenu(filtersContextMenu.getContextMenu());
    }
    
    @FXML
    public void menuBarCloseAction() {
        closeApplication();
    }
    
    @FXML
    public void menuBarActionOpenSQLQuery() {
    
    }
    
    private void addLog(Level level, String message) {
        startLogBox();
        
        logBox.addLog(message, level);
    }
    
    private void addLog(Level level, String message, Exception e) {
        startLogBox();
        
        logBox.addLog(message, level);
        log.log(level, e.getMessage(), e);
    }
    
    private void addLog(String message, Level level, String logMessage) {
        startLogBox();
        
        logBox.addLog(message, level);
        log.log(level, logMessage);
    }
    
    private void startLogBox() {
        if (!logBox.isAlive()) {
            logBox.start();
            threadsController.addThread(logBox);
        }
    }
    
    public void closeApplication() {
        threadsController.killThreads();
        if (loginWindow != null)
            ((LoginWindowController) loginWindow.getController()).exit();
        
        Platform.exit();
    }
    
    private void waitForDBConnection() {
        try {
            databaseConnection = (Connection) loginWindow.getController().getResult();
            
            if (databaseConnection == null)
                throw new FailedToConnectToDatabase();
            
            addLog(Level.INFO, "Połączono z bazą danych.");
            connectedToDatabase = true;
            return;
        }
        catch (FailedToConnectToDatabase e) {
            addLog(Level.WARNING, "Nie udało się połączyć z bazą danych.");
        } catch (Exception e) {
            addLog(Level.SEVERE, "Błąd aplikacji.", e);
        }
        
        closeDBConnection(false);
        connectedToDatabase = false;
    }
    
    private void closeDBConnection(boolean verbose) {
        if (databaseConnection != null && connectedToDatabase) {
            try {
                databaseConnection.close();
                connectedToDatabase = false;
                if (verbose)
                    addLog(Level.INFO, "Odłączono od bazy danych.");
            }
            catch (SQLException e) {
                addLog(Level.WARNING, "Nie udało się zakończyć połączenia z bazą danych.", e);
            }
        } else if (verbose)
            addLog(Level.SEVERE,"Nie jesteś połączony do żadnej bazy danych.");
    }
    
    @Override
    Object getResult() {
        return null;
    }
}
