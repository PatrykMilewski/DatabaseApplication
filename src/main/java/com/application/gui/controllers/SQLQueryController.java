package com.application.gui.controllers;

import com.application.database.sql.DataProcessor;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.controllers.ThreadsController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLQueryController extends Controller {
    
    private static final double ACTION_BUTTON_WIDTH = 30;
    private static final double ACTION_BUTTON_HEIGHT = 30;
    private static Logger log = LoggerFactory.getLogger(MainWindowController.class.getCanonicalName());
    
    private Connection connection = null;
    private int tabsCounter = 1;
    private Image loadingGifImage = new Image("images/loading.gif");
    
    private ThreadsController threadsController = new ThreadsController();
    private DataProcessor dataProcessor = null;
    
    @FXML
    private TextArea queryTextArea;
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private ArrayList<Button> actionButtons;
    
    @FXML
    private ImageView loadingGif;
    
    @FXML
    public void initialize() {
        String imagesPaths[] = { "images/run.png", "images/delete.png", "images/configuration.png",
                "images/backup.png"};
    
        int i = 0;
        for (Button button : actionButtons) {
            setImageOnButton(button, imagesPaths[i], ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);
            i++;
            if (i >= imagesPaths.length)
                break;
        }
        loadingGif.setImage(null);
    }
    
    @FXML
    public void menuBarProcessQueryClicked() {
        startProcessQueryClicked();
    }
    
    @FXML
    public void menuBarCloseTabClicked() {
        clearTab();
    }
    
    private void startProcessQueryClicked() {
        Platform.runLater(() -> loadingGif.setImage(loadingGifImage));
        String sqlQuery = queryTextArea.getText();
        Thread queryThread = new Thread(() -> processQuery(sqlQuery));
        threadsController.addThread(queryThread);
        queryThread.start();
    }
    
    synchronized private void processQuery(String queryAreaText) {
        //todo remove old tabs from pane mechanism to avoid memory leak
        
        for (String sqlCommand : queryAreaText.split(";")) {
            CachedRowSet cachedRowSet = dataProcessor.processSQLCommand(sqlCommand).getKey();
            Tab tab = new Tab("Tabela " + tabsCounter++);
            Platform.runLater(() -> displayResults(new TableView<>(), tab, cachedRowSet));
        }
        Platform.runLater(() -> loadingGif.setImage(null));
    }
    
    private void displayResults(TableView<ObservableList> tableView, Tab tab, CachedRowSet cachedRowSet) {
        tabPane.getTabs().add(tab);
        tab.setContent(tableView);
    
        dataProcessor.handleSingleResultSet(tableView, cachedRowSet);
    }
    
    @Override
    synchronized public void closeWindow() {
        threadsController.killThreads();
        resultsReady = true;
        notifyAll();
        stage.close();
    }

    public void setConnection(Connection connection) {
        if (connection != null) {
            this.connection = connection;
            dataProcessor = new DataProcessor(connection);
        }
    }
    
    @Override
    synchronized Object getResult() {
        while(!resultsReady) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.log(Level.INFO, "Waiting for results in SQL Query interrupted.");
            }
        }
        
        return !resultsReady;
    }
    
    public void firstActionButtonClicked() {
        startProcessQueryClicked();
    }
    
    public void secondActionButtonClicked(ActionEvent actionEvent) {
        clearTab();
    }
    
    private void clearTab() {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        if (!tabPane.getTabs().isEmpty())
            tabPane.getTabs().remove(selectionModel.getSelectedItem());
    }
    
    public void thirdActionButtonClicked(ActionEvent actionEvent) {
        for (Tab tab : tabPane.getTabs()) {
            System.out.println(tab.getText());
        }
    }
    
    public void fourthActionButtonClicked(ActionEvent actionEvent) {
    
    }
}
