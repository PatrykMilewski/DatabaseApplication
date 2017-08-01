package com.application.database.sql;


import com.application.database.tables.TableInfo;
import com.application.database.tables.TableMetaData;
import com.application.gui.abstracts.exceptions.NoChangesInRowException;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.alerts.MyAlerts;
import com.sun.rowset.CachedRowSetImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataProcessor {
    
    private static final double COLUMN_WIDTH = 500;
    
    private static Logger log = LoggerFactory.getLogger(DataProcessor.class.getCanonicalName());
    
    private static final Comparator<String> STRING_COMPARATOR;
    
    private Connection connection;
    
    static {
        STRING_COMPARATOR = (s1, s2) -> {
            if (s1.length() != s2.length() && StringUtils.isNumeric(s1) && StringUtils.isNumeric(s2))
                return Integer.parseInt(s1) - Integer.parseInt(s2);
        
            else
                return s1.compareTo(s2);
        };
    }
    
    public DataProcessor(Connection connection) {
        this.connection = connection;
    }
    
    public Pair<CachedRowSet, TableInfo> processSQLCommand(String sqlCommand) {
        ResultSet resultSet;
        CachedRowSet cachedRowSet;
        ResultSetMetaData resultSetMetaData;
        TableMetaData tableMetaData;
        String tableName;
        
        try (PreparedStatement statement = connection.prepareStatement(sqlCommand)) {
            statement.execute();
            resultSet = statement.getResultSet();
            cachedRowSet = new CachedRowSetImpl();
            cachedRowSet.populate(resultSet);
            resultSetMetaData = cachedRowSet.getMetaData();
            tableMetaData = new TableMetaData(resultSetMetaData);
            tableName = resultSetMetaData.getTableName(1);
            
            if (statement.getUpdateCount() != -1) {
                MyAlerts.showInfoAlert("Wykonanie SQL Query",
                        "Pomyślnie wykonano aktualizację.",
                        "Zaktualizowano " + statement.getUpdateCount() + " rekordów.", false);
                
                return null;
            }
            
            
        } catch (SQLException e) {
            MyAlerts.showExceptionAlert("Wyjątek podczas wykonywania SQL query.", e, false);
            return null;
        }
    
        TableInfo tableInfo = new TableInfo(tableMetaData, sqlCommand, tableName);
        return new Pair<>(cachedRowSet, tableInfo);
    }
    
    public boolean processSqlRowUpdate(DatabaseRow databaseRow) {
        String sqlQuery;
        try {
            sqlQuery = prepareSqlUpdateQuery(databaseRow);
        } catch (NoChangesInRowException e) {
            log.log(Level.WARNING, "No changes found but edit committed.");
            return false;
        }
        Object result = processSQLCommand(sqlQuery);
        if (result != null) {
            log.log(Level.SEVERE, "Unexpected sql process result.");
            return false;
        }
        else
            return true;
    }
    
    public void handleSingleResultSet(TableView<ObservableList> tableView, CachedRowSet cachedRowSet) {
        try {
            ResultSetMetaData meta = cachedRowSet.getMetaData();
            TableMetaData tableMetaData = new TableMetaData(meta);
            constructColumns(tableView, tableMetaData);
            ObservableList<ObservableList> data = handleResultsSet(cachedRowSet, tableMetaData);
            tableView.setItems(data);
            cachedRowSet.close();
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            MyAlerts.showExceptionAlert("Wyjątek podczas wykonywania SQL query.", e, true);
        }
    }
    
    private ObservableList<ObservableList> handleResultsSet(CachedRowSet cachedRowSet, TableMetaData tableMetaData)
            throws SQLException {
        
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        while (cachedRowSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < tableMetaData.columnsCount; i++)
                row.add(cachedRowSet.getString(i + 1));
            
            data.add(row);
        }
        return data;
    }
    
    private void constructColumns(TableView<ObservableList> tableView, TableMetaData tableMetaData) {
        for (int i = 0; i < tableMetaData.columnsCount; i++) {
            final int finalIterator = i;
            TableColumn<ObservableList, String> tableColumn = new TableColumn<>(tableMetaData.columnNames.get(i));
            tableColumn.setMaxWidth(COLUMN_WIDTH);
            tableColumn.setComparator(STRING_COMPARATOR);
            Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>> callback =
                    param -> new SimpleStringProperty((param.getValue().get(finalIterator).toString()));
    
            tableColumn.setCellValueFactory(callback);
            
            tableColumn.setCellFactory(column -> new TableCell<ObservableList, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                    if (this.getText() != null)
                        setTooltip(new Tooltip(this.getText()));
                }
            });

            tableView.getColumns().add(tableColumn);
        }
    }
    
    private String prepareSqlUpdateQuery(DatabaseRow databaseRow) throws NoChangesInRowException {
        ObservableList<String> oldValues = databaseRow.columnValuesOld;
        ObservableList<String> newValues = databaseRow.columnValues;
        List<String> columnNames = databaseRow.tableInfo.getTableMetaData().columnNames;
        String tableName = databaseRow.tableInfo.getTableName();
        
        StringBuilder sqlBuilder = new StringBuilder();
    
        final int size = databaseRow.size;
        boolean changedSomething = false;
        sqlBuilder.append("update ").append(tableName).append(" set ");
        for (int i = 0; i < size; i++) {
            if (!oldValues.get(i).equals(newValues.get(i))) {
                sqlBuilder.append(columnNames.get(i));
                sqlBuilder.append(" = \"");
                sqlBuilder.append(newValues.get(i));
                sqlBuilder.append("\"");
                changedSomething = true;
                if ((i + 1) < size)
                    sqlBuilder.append(", ");
            }
        }
        if (!changedSomething)
            throw new NoChangesInRowException();
    
        sqlBuilder.append("where ");
    
        for (int i = 0; i < size; i++) {
            sqlBuilder.append(columnNames.get(i));
            sqlBuilder.append(" = \"");
            sqlBuilder.append(oldValues.get(i));
            sqlBuilder.append("\"");
            if ((i + 1) < size)
                sqlBuilder.append(" and ");
        }
    
        sqlBuilder.append(";");
        return sqlBuilder.toString();
    }
}
