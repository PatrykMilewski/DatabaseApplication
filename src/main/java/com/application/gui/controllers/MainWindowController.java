package com.application.gui.controllers;

import com.application.database.sql.DataProcess;
import com.application.database.sql.DatabaseFilter;
import com.application.gui.abstracts.consts.enums.SpotStances;
import com.application.gui.abstracts.consts.values.ConstValues;
import com.application.gui.abstracts.exceptions.FailedToConnectToDatabase;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.containers.IconSpotsContainer;
import com.application.gui.elements.contextmenus.Contextable;
import com.application.gui.elements.controllers.ThreadsController;
import com.application.gui.elements.contextmenus.DataTableContextMenu;
import com.application.gui.elements.contextmenus.FiltersContextMenu;
import com.application.gui.elements.infobox.LogBox;
import com.application.gui.windows.FilterConstructorWindow;
import com.application.gui.windows.LoginWindow;
import com.application.gui.windows.SQLQueryWindow;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import org.codehaus.plexus.util.FileUtils;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindowController extends Controller {
    
    private static final double FILTER_BUTTON_HEIGHT = 12;
    private static final double FILTER_BUTTON_WIDTH = 12;
    private static final int ICONS_AMOUNT = 1;
    
    private static Logger log = LoggerFactory.getLogger(MainWindowController.class.getCanonicalName());
    
    private static boolean isSQLQueryWindowOpen = false, isLoginWindowOpen = false;
    private static boolean isFilterConstructorWindowOpen = false;
    
    private ThreadsController threadsController;
    private LoginWindow loginWindow;
    private SQLQueryWindow sqlQueryWindow;
    private FilterConstructorWindow filterConstructorWindow;
    private LogBox logBox;
    private IconSpotsContainer iconSpotsContainer;
    
    private static Set<String> filtersNames = new HashSet<>();
    
    private Connection databaseConnection = null;
    private Contextable dataTableContextMenu, filtersContextMenu;
    
    private boolean connectedToDatabase = false;
    private int tabsCounter = 1;

    @FXML
    private TabPane tabPane;
    
    @FXML
    private ListView<DatabaseFilter> filtersList;
    
    @FXML
    private Label logLabel;
    
    @FXML
    private ArrayList<ImageView> statusIcons;
    
    @FXML
    private ArrayList<Button> filterButtons;
    
    //////////////////////////
    //                      //
    //   PUBLIC METHODS     //
    //                      //
    //////////////////////////
    
    //*****************************************************************************************************************/
    //  JavaFX business logic
    //*****************************************************************************************************************/
    
    @FXML
    public void initialize() {
        resultsReady = true;
        logBox = new LogBox(logLabel);
        threadsController = new ThreadsController();
        dataTableContextMenu = new DataTableContextMenu();
        filtersContextMenu = new FiltersContextMenu();
        iconSpotsContainer = new IconSpotsContainer();
        
        String imagesPaths[] = { "images/plus.png", "images/minus.png" };
        
        int i = 0;
        for (Button filterButton : filterButtons) {
            setImageOnButton(filterButton, imagesPaths[i], FILTER_BUTTON_WIDTH, FILTER_BUTTON_HEIGHT);
            
            i++;
            if (i >= imagesPaths.length)
                break;
        }
    
        filtersList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                DatabaseFilter databaseFilter = filtersList.getSelectionModel().getSelectedItem();
                executeFilterQuery(databaseFilter);
            }
        });
        
        initializeIcons();
    }
    
    //*****************************************************************************************************************/
    //  Interface connected methods
    //*****************************************************************************************************************/
    
    @FXML
    public void menuBarActionOpenLoginWindow() {
        if (isLoginWindowOpen) {
            addLog(Level.SEVERE, "Okno logowania jest już otwarte.");
            return;
        }
        
        try {
            loginWindow = new LoginWindow();
            Thread connectionWorker = new Thread(this::waitForDBConnection);
            connectionWorker.start();
            threadsController.addThread(connectionWorker);
            isLoginWindowOpen = true;
        } catch (IOException e) {
            addLog(Level.SEVERE, "Nie udało się otworzyć okna logowania.", e);
        }
    }
    
    @FXML
    public void menuBarActionOpenSQLQuery() {
        if (isSQLQueryWindowOpen) {
            addLog(Level.SEVERE, "Okno SQL Query jest już otwarte.");
            return;
        }
        
        try {
            sqlQueryWindow = new SQLQueryWindow(databaseConnection);
            Thread queryWindowThread = new Thread(this::waitForDBQueryWindow);
            queryWindowThread.start();
            threadsController.addThread(queryWindowThread);
            isSQLQueryWindowOpen = true;
        } catch (IOException e) {
            addLog(Level.SEVERE, "Nie udało się otworzyć okna SQL Query.", e);
        }
    }
    
    @FXML
    public void menuBarActionDisconnectFromDB() {
        closeDBConnection(true);
    }
    
    @FXML
    public void menuBarActionResetSettings() {
        String appData = System.getenv("APPDATA");
        String dataPath = Paths.get(appData, ConstValues.getArtifactId(), ConstValues.getVersion()).toString();
        
        try {
            FileUtils.deleteDirectory(new File(dataPath));
            addLog(Level.INFO, "Usunięto dane aplikacji.");
        } catch (IOException e) {
            addLog(Level.SEVERE, "Nie udało się usunąć danych aplikacji.", e);
        }
    }
    
    @FXML
    public void menuBarActionCloseApplication() {
        closeApplication();
    }
    
    @FXML
    public void filterButtonActionAddNewFilter() {
        if (isFilterConstructorWindowOpen) {
            addLog(Level.WARNING, "Okno konstruktora filtrów jest już otwarte.");
            return;
        }
        
        try {
            filterConstructorWindow = new FilterConstructorWindow(databaseConnection);
            Thread filterConstructorWorker = new Thread(this::waitForFilterConstruction);
            threadsController.addThread(filterConstructorWorker);
            filterConstructorWorker.start();
            
            isFilterConstructorWindowOpen = true;
        }
        catch (IOException e) {
            addLog(Level.SEVERE, "Nie udało się otworzyć okna konstruktora filtrów.", e);
        }
    }
    
    @FXML
    public void filterButtonActionDeleteFilter() {
        DatabaseFilter selectedFilter = filtersList.getSelectionModel().getSelectedItem();
        if (selectedFilter != null) {
            filtersList.getItems().remove(selectedFilter);
            addLog(Level.INFO, "Usunięto filtr o nazwie: " + selectedFilter.getName() + ".");
        }
        else {
            addLog(Level.SEVERE, "Najpierw wybierz filtr do usunięcia.");
        }
    }
    
    @FXML
    public void showFiltersContextMenu(ContextMenuEvent contextMenuEvent) {
    
    }
    
    // todo make context menus for single tables and rows

//    @FXML
//    public void showDataTableContextMenu(ActionEvent event) {
//        dataPane.setContextMenu(dataTableContextMenu.getContextMenu(event));
//    }
//
//    @FXML
//    public void showFiltersContextMenu(ActionEvent event) {
//        filtersList.setContextMenu(filtersContextMenu.getContextMenu(event));
//    }
    
    
    
    //////////////////////////
    //                      //
    //   PROTECTED METHODS  //
    //                      //
    //////////////////////////
    
    //*****************************************************************************************************************/
    //  Overridden
    //*****************************************************************************************************************/
    
    @Override
    Object getResult() {
        return null;
    }
    
    @Override
    public void closeWindow() {
        threadsController.killThreads();
        stage.close();
    }
    
    //*****************************************************************************************************************/
    //  Communication with other windows
    //*****************************************************************************************************************/
    
    static void sqlQueryWindowClosed() {
        MainWindowController.isSQLQueryWindowOpen = false;
    }
    
    static void loginWindowClosed() {
        MainWindowController.isLoginWindowOpen = false;
    }
    
    static void filterConstructorWindowClosed() {
        isFilterConstructorWindowOpen = false;
    }
    
    static boolean validateFiltersName(String name) {
        return !filtersNames.contains(name);
    }
    
    
    //////////////////////////
    //                      //
    //   PRIVATE METHODS    //
    //                      //
    //////////////////////////
    
    //*****************************************************************************************************************/
    //  Logging
    //*****************************************************************************************************************/
    
    private void addLog(Level level, String message) {
        startLogBox();
        
        logBox.addLog(message, level);
    }
    
    private void addLog(Level level, String message, Exception e) {
        startLogBox();
        
        logBox.addLog(message, level);
        log.log(level, e.getMessage(), e);
    }
    
    private void startLogBox() {
        if (!logBox.isAlive()) {
            logBox.start();
            threadsController.addThread(logBox);
        }
    }
    
    //*****************************************************************************************************************/
    //  Operations on application as JavaFX Application
    //*****************************************************************************************************************/
    
    public void closeApplication() {
        threadsController.killThreads();
        if (loginWindow != null)
            loginWindow.getController().closeWindow();
        
        if (sqlQueryWindow != null)
            sqlQueryWindow.getController().closeWindow();
        
        Platform.exit();
    }
    
    //*****************************************************************************************************************/
    //  Business logic - DB login and connection
    //*****************************************************************************************************************/
    
    private void connectedToDatabase(boolean result) {
        connectedToDatabase = result;
        
        if (result)
            addLog(Level.INFO, "Połączono z bazą danych.");
        
        else
            addLog(Level.INFO, "Nie połączono z bazą danych.");
        
        setIcon(SpotStances.CONNECTION, result);
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
    
    private void waitForDBQueryWindow() {
        sqlQueryWindow.getController().getResult();
        addLog(Level.INFO, "Zamknięto okno SQL Query.");
    }
    
    private void waitForDBConnection() {
        try {
            databaseConnection = (Connection) loginWindow.getController().getResult();
            
            if (databaseConnection == null)
                throw new FailedToConnectToDatabase();
            
            connectedToDatabase(true);
            return;
        }
        catch (FailedToConnectToDatabase e) {
            addLog(Level.WARNING, "Nie udało się połączyć z bazą danych.");
        } catch (Exception e) {
            addLog(Level.SEVERE, "Błąd aplikacji.", e);
        }
        
        closeDBConnection(false);
        connectedToDatabase(false);
    }
    
    private void waitForFilterConstruction() {
        DatabaseFilter databaseFilter = (DatabaseFilter)filterConstructorWindow.getController().getResult();
        if (databaseFilter != null) {
            filtersList.getItems().add(databaseFilter);
            addLog(Level.INFO, "Dodano nowy filtr o nazwie: " + databaseFilter.getName() + ".");
        }
        else {
            addLog(Level.SEVERE, "Nie udało się dodać nowego filtra.");
        }
    }
    
    private void executeFilterQuery(DatabaseFilter databaseFilter) {
        CachedRowSet cachedRowSet = DataProcess.processSQLCommand(databaseConnection, databaseFilter.getSqlQuery());
        Tab tab = new Tab("Tabela " + tabsCounter++);
        Platform.runLater(() -> displayResults(new TableView<>(), tab, cachedRowSet));
    }
    
    private void displayResults(TableView<ObservableList> tableView, Tab tab, CachedRowSet cachedRowSet) {
        tabPane.getTabs().add(tab);
        tab.setContent(tableView);
        
        DataProcess.handleSingleResultSet(tableView, cachedRowSet);
    }
    
    //*****************************************************************************************************************/
    //  JavaFX business logic
    //*****************************************************************************************************************/
    
    private void initializeIcons() {
        int i = 0;
        for (ImageView icon : statusIcons) {
            icon.setImage(
                    new Image(getClass().getClassLoader().getResourceAsStream("images/icons/disconnected.png")));
            i++;
            if (i >= ICONS_AMOUNT)
                break;
        }
    }
    
    private void setIcon(SpotStances spot, boolean stance) {
        int index = iconSpotsContainer.changeSpotStance(spot, stance);
        Image image = iconSpotsContainer.getIconImage(spot);
        
        statusIcons.get(index).setImage(image);
    }
}
