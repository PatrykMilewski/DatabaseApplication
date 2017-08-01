package com.application.gui.controllers;

import com.application.database.sql.DataProcessor;
import com.application.database.sql.DatabaseFilter;
import com.application.database.sql.DatabaseRow;
import com.application.database.tables.TableInfo;
import com.application.database.tables.TableMetaData;
import com.application.gui.abstracts.consts.enums.SpotStances;
import com.application.gui.abstracts.consts.values.ConstValues;
import com.application.gui.abstracts.exceptions.FailedToConnectToDatabase;
import com.application.gui.abstracts.exceptions.IllegalOperationException;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.containers.IconSpotsContainer;
import com.application.gui.elements.contextmenus.Contextable;
import com.application.gui.elements.controllers.ThreadsController;
import com.application.gui.elements.contextmenus.DataTableContextMenu;
import com.application.gui.elements.contextmenus.FiltersContextMenu;
import com.application.gui.elements.infobox.LogBox;
import com.application.gui.elements.loaders.FiltersDataLoader;
import com.application.gui.windows.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.util.Pair;
import org.codehaus.plexus.util.FileUtils;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindowController extends Controller {
    
    private static final double FILTER_BUTTON_HEIGHT = 12;
    private static final double FILTER_BUTTON_WIDTH = 12;
    private static final int ICONS_AMOUNT = 1;
    
    private static Logger log = LoggerFactory.getLogger(MainWindowController.class.getCanonicalName());
    
    private ThreadsController threadsController;
    private LoginWindow loginWindow;
    private SQLQueryWindow sqlQueryWindow;
    private FilterConstructorWindow filterConstructorWindow;
    private FiltersDataLoader filtersDataLoader;
    private RowEditorWindow rowEditorWindow;
    private DataProcessor dataProcessor;
    private LogBox logBox;
    private IconSpotsContainer iconSpotsContainer;
    private Map<Class, Window> windowsList = new HashMap<>();
    private Map<String, TableInfo> tablesNames = new ConcurrentHashMap<>();
    
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
        filtersDataLoader = new FiltersDataLoader();
        
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
        
        filtersDataLoader.loadData();
        Set<DatabaseFilter> filtersSet = filtersDataLoader.getSavedFilters();
        if (filtersSet != null && !filtersSet.isEmpty())
            filtersList.getItems().addAll(filtersSet);
        
        initializeIcons();
    }
    
    //*****************************************************************************************************************/
    //  Interface connected methods
    //*****************************************************************************************************************/
    
    @FXML
    public void menuBarActionOpenLoginWindow() {
        openWindow(LoginWindow.class, this::waitForDBConnection);
    }
    
    @FXML
    public void menuBarActionOpenSQLQuery() {
        openWindow(SQLQueryWindow.class, this::waitForDBQueryWindow);
    }
    
    @FXML
    public void filterButtonActionAddNewFilter() {
        openWindow(FilterConstructorWindow.class, this::waitForFilterConstruction);
    }
    
    @FXML
    public void filterButtonActionDeleteFilter() {
        DatabaseFilter selectedFilter = filtersList.getSelectionModel().getSelectedItem();
        if (selectedFilter != null) {
            filtersList.getItems().remove(selectedFilter);
            filtersDataLoader.removeData(selectedFilter);
            addLog(Level.INFO, "Usunięto filtr o nazwie: " + selectedFilter.getName() + ".");
        }
        else {
            addLog(Level.SEVERE, "Najpierw wybierz filtr do usunięcia.");
        }
    }
    
    @FXML
    public void menuBarActionDisconnectFromDB() {
        closeDBConnection(true);
    }
    
    @FXML
    public void menuBarActionResetSettings() {
        try {
            FileUtils.deleteDirectory(new File(ConstValues.PROGRAM_DATA));
            addLog(Level.INFO, "Usunięto dane aplikacji.");
        } catch (IOException e) {
            addLog(Level.SEVERE, "Nie udało się usunąć danych aplikacji.", e);
        }
    }
    
    @FXML
    public void menuBarActionCloseApplication() {
        closeApplication();
    }
    
    
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
        log.log(Level.SEVERE, "Illegal method call (close window in class MainWindowController).");
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
        windowsList.forEach((singleClass, singleWindow) -> singleWindow.getController().closeWindow());
        filtersDataLoader.saveData();
        closeDBConnection(false);
        threadsController.killThreads();
        Platform.exit();
    }
    
    //*****************************************************************************************************************/
    //  Operations on JavaFX Windows
    //*****************************************************************************************************************/
    
    private void openWindow(Class classType, Runnable jobForWorker) {
        if (windowsList.containsKey(classType)) {
            addLog(Level.SEVERE, "Okno jest jest już otwarte.");
            return;
        }
        
        try {
            Window window = independentWindowsFactory(classType);
            Thread worker = new Thread(jobForWorker);
            worker.start();
            threadsController.addThread(worker);
            windowsList.put(classType, window);
        } catch (IOException e) {
            addLog(Level.SEVERE, "Nie udało się otworzyć okna.", e);
        } catch (IllegalOperationException e) {
            addLog(Level.SEVERE, e.getMessage());
        }
    }
    
    private Window independentWindowsFactory(Class classType) throws IOException, IllegalOperationException {
        if (classType == LoginWindow.class) {
            loginWindow = new LoginWindow();
            return loginWindow;
        }
        else if (classType == SQLQueryWindow.class) {
            if (connectedToDatabase) {
                sqlQueryWindow = new SQLQueryWindow(databaseConnection);
                return sqlQueryWindow;
            }
            else
                throw new IllegalOperationException("Najpierw połącz się z bazą danych.");
        }
        else if (classType == FilterConstructorWindow.class) {
            filterConstructorWindow = new FilterConstructorWindow();
            return filterConstructorWindow;
        }
        else
            throw new IllegalArgumentException("Illegal class type passed to method.");
    }
    
    //*****************************************************************************************************************/
    //  Business logic
    //*****************************************************************************************************************/
    
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
        windowsList.remove(SQLQueryWindow.class);
        threadsController.removeThread(Thread.currentThread());
        addLog(Level.INFO, "Zamknięto okno SQL Query.");
    }
    
    private void waitForDBConnection() {
        try {
            databaseConnection = (Connection) loginWindow.getController().getResult();
            
            if (databaseConnection == null)
                throw new FailedToConnectToDatabase();
            
            connectedToDatabase(true);
            windowsList.remove(LoginWindow.class);
            dataProcessor = new DataProcessor(databaseConnection);
            return;
        }
        catch (FailedToConnectToDatabase e) {
            addLog(Level.WARNING, "Nie udało się połączyć z bazą danych.");
        } catch (Exception e) {
            addLog(Level.SEVERE, "Błąd aplikacji.", e);
        }
        
        closeDBConnection(false);
        connectedToDatabase(false);
        windowsList.remove(LoginWindow.class);
        threadsController.removeThread(Thread.currentThread());
    }
    
    private void connectedToDatabase(boolean result) {
        connectedToDatabase = result;
        
        if (result)
            addLog(Level.INFO, "Połączono z bazą danych.");
        
        else
            addLog(Level.INFO, "Nie połączono z bazą danych.");
        
        setIcon(SpotStances.CONNECTION, result);
    }
    
    private void waitForFilterConstruction() {
        DatabaseFilter databaseFilter = (DatabaseFilter)filterConstructorWindow.getController().getResult();
        if (databaseFilter != null) {
            Platform.runLater(() -> filtersList.getItems().add(databaseFilter));
            filtersDataLoader.addNewData(databaseFilter);
            addLog(Level.INFO, "Dodano nowy filtr o nazwie: " + databaseFilter.getName() + ".");
        }
        else
            addLog(Level.SEVERE, "Nie udało się dodać nowego filtra.");
        
        windowsList.remove(FilterConstructorWindow.class);
        threadsController.removeThread(Thread.currentThread());
    }
    
    /**
     * Executes SQL code that is hidden in DatabaseFilter. Fails if there is no connection to DB or filter is invalid.
     * @param databaseFilter Filter that contains SQL code to execute.
     */
    
    private void executeFilterQuery(final DatabaseFilter databaseFilter) {
        if (connectedToDatabase) {
            Pair<CachedRowSet, TableInfo> resultsSetAndMetaData =
                    dataProcessor.processSQLCommand(databaseFilter.getSqlQuery());
            
            if (resultsSetAndMetaData != null)
                displayResults(resultsSetAndMetaData.getKey(), resultsSetAndMetaData.getValue());
            
            else
                addLog(Level.SEVERE, "Nie udało się wywołać filtru.");
        }
        else
            addLog(Level.SEVERE, "Brak połączenia z bazą danych.");
        
    }
    
    /**
     * Displays results in a new Tab in TabPane.
     * @param cachedRowSet Needed for construction of columns. Closed in one of this method calls.
     * @param tableInfo Needed only for putting it with Tab into global Map.
     */
    
    private void displayResults(CachedRowSet cachedRowSet, TableInfo tableInfo) {
        String tabName = "Tabela " + tabsCounter++;
        Tab tab = new Tab(tabName);
        tablesNames.put(tab.getText(), tableInfo);
        TableView<ObservableList> tableView = new TableView<>();
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2)
                tableRowDoubleClicked();
        });
        
        Platform.runLater(() ->  {
            tabPane.getTabs().add(tab);
            tab.setContent(tableView);
            dataProcessor.handleSingleResultSet(tableView, cachedRowSet);
        });
    }
    
    /**
     * Opens Editor Window, that is dependent on Main Window. Could not be done with independentWindowFactory
     * method due to parameters passing to this method.
     * @param databaseRow Row that may be edited.
     * @param selectedTab Tab with TableView that contains databaseRow.
     */
    
    private void openRowEditorWindow(DatabaseRow databaseRow, Tab selectedTab) {
        if (windowsList.containsKey(RowEditorWindow.class)) {
            addLog(Level.WARNING, "Okno edytora rekordów jest już otwarte.");
            return;
        }
        
        try {
            rowEditorWindow = new RowEditorWindow("Edycja tabeli " + databaseRow.tableName,
                    databaseConnection, databaseRow);
            
            Thread editorWorker = new Thread(this::waitForEditorToFinish);
            editorWorker.start();
            threadsController.addThread(editorWorker);
            windowsList.put(RowEditorWindow.class, rowEditorWindow);
        }
        catch (IOException e) {
            addLog(Level.SEVERE, "Nie udało się otworzyć okna edytora rekordów.", e);
            rowEditorWindow = null;
            if (windowsList.containsKey(RowEditorWindow.class))
                windowsList.remove(RowEditorWindow.class);
        }
    }
    
    /**
     * Method that is used by thread worker to wait for results from an Editor Window.
     */
    
    private void waitForEditorToFinish() {
        int updatedCells = (int)rowEditorWindow.getController().getResult();
        if (updatedCells != -1)
            addLog(Level.INFO, "Pomyślnie zaktualizowano wartości " + updatedCells + " komórek.");
        else
            addLog(Level.SEVERE, "Nie udało się zakualizować rekordu.");
    }
    
    //*****************************************************************************************************************/
    //  JavaFX business logic
    //*****************************************************************************************************************/
    
    /**
     * Initializing small icons, that notify user about actual application stance, by loading it's images.
     */
    
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
    
    /**
     * Sets single icon's image in it's spot.
     * @param spot Parameter that allows to identify type of a spot.
     * @param stance Determines stance of an icon, it may be for example Ok if it's true, and Failed if it's false.
     */
    
    private void setIcon(SpotStances spot, boolean stance) {
        int index = iconSpotsContainer.changeSpotStance(spot, stance);
        Image image = iconSpotsContainer.getIconImage(spot);
        
        statusIcons.get(index).setImage(image);
    }
    
    /**
     * Method that is called when double-click on row in table is detected. It handles this event by opening
     * an editor window and passing to it parameters that contains information about clicked row.
     */
    
    private void tableRowDoubleClicked() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        TableInfo tableInfo = tablesNames.get(selectedTab.getText());
    
        //noinspection unchecked
        TableView<ObservableList<String>> selectedTable = (TableView<ObservableList<String>>)selectedTab.getContent();
        
        ObservableList<String> row = selectedTable.getSelectionModel().getSelectedItem();
    
        openRowEditorWindow(new DatabaseRow(tableInfo, row), selectedTab);
    }
}
