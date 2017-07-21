package com.application.gui.controllers;

import com.application.database.sql.DataProcess;
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

public class SQLQueryWindowController extends Controller {
    
    private static final double ACTIONBUTTONWIDTH = 30;
    private static final double ACTIONBUTTONHEIGHT = 30;
    private static Logger log = LoggerFactory.getLogger(MainWindowController.class.getCanonicalName());
    
    private Connection connection;
    private int tabsCounter = 1;
    private Image loadingGifImage = new Image("images/loading.gif");
    
    private ThreadsController threadsController = new ThreadsController();
    
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
            ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(imagesPaths[i])));
            imageView.setFitWidth(ACTIONBUTTONWIDTH);
            imageView.setFitHeight(ACTIONBUTTONHEIGHT);
            button.setGraphic(imageView);
            i++;
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
            CachedRowSet cachedRowSet = DataProcess.processSQLCommand(connection, sqlCommand);
            Tab tab = new Tab("Tabela " + tabsCounter);
            tabsCounter++;
            Platform.runLater(() -> displayResults(new TableView<>(), tab, cachedRowSet));
        }
        Platform.runLater(() -> loadingGif.setImage(null));
    }
    
    private void displayResults(TableView<ObservableList> tableView, Tab tab, CachedRowSet cachedRowSet) {
        tabPane.getTabs().add(tab);
        tab.setContent(tableView);
    
        DataProcess.handleSingleResultSet(tableView, cachedRowSet);
    }
    
    @Override
    public void closeWindow() {
        MainWindowController.setIsSQLQueryWindowOpen(false);
        threadsController.killThreads();
        stage.close();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
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
    
    }
    
    public void fourthActionButtonClicked(ActionEvent actionEvent) {
    
    }
}
