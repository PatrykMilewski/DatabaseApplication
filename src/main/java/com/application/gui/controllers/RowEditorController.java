package com.application.gui.controllers;

import com.application.database.sql.DataProcessor;
import com.application.database.sql.DatabaseCell;
import com.application.database.sql.DatabaseRow;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.controllers.ThreadsController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RowEditorController extends Controller {
    
    private static Logger log = LoggerFactory.getLogger(RowEditorController.class.getCanonicalName());

    private static KeyCombination commitCellEditShortcut =
            new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

    private ThreadsController threadsController = new ThreadsController();
    private Connection connection;
    private DataProcessor dataProcessor;
    private DatabaseRow databaseRow;
    private int editSuccessful = -1;

    @FXML
    private ListView<DatabaseCell> columnsList;

    @FXML
    private Label resultsLabel;

    @FXML
    private TextArea editorTextArea;
    
    @FXML
    public void initialize() {
        columnsList.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                int index = columnsList.getSelectionModel().getSelectedItem().getIndex();
                editorTextArea.setText(databaseRow.columnValues.get(index));
            }
        });
    }
    
    public void initializeWindow() {
        stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (commitCellEditShortcut.match(event)) {
                commitCellEdit();
                event.consume();
            }
        });
    }
    
    public void loadColumnsList() {
        Platform.runLater(() -> columnsList.getItems().addAll(databaseRow.cellsList));
    }

    @FXML
    public void commitEdit() {
        editSuccessful = dataProcessor.processSqlRowUpdate(databaseRow) ? databaseRow.getChangesCounter() : -1;
        Platform.runLater(this::closeWindow);
    }

    @FXML
    public void cancelEdit() {
        editSuccessful = -1;
        Platform.runLater(this::closeWindow);
    }

    @FXML
    public void commitCellEdit() {
        DatabaseCell selectedCell = columnsList.getSelectionModel().getSelectedItem();
        databaseRow.changeValue(editorTextArea.getText(), selectedCell.getIndex());
        log.log(Level.INFO, "Value changed for cell with index " + selectedCell.getIndex() + ".");
        resultsLabel.setText("Przygotowano do zapisu wartość komórki o nazwie "
                + databaseRow.columnNames.get(selectedCell.getIndex()));
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
        dataProcessor = new DataProcessor(connection);
    }

    public void setDatabaseRow(DatabaseRow databaseRow) {
        this.databaseRow = databaseRow;
    }

    @Override
    Object getResult() {
        try {
            waitForResults();
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, "Waiting for results interrupted.");
        }

        return editSuccessful;
    }

    @Override
    synchronized public void closeWindow() {
        threadsController.killThreads();
        resultsReady = true;
        notifyAll();
        stage.close();
    }
}
