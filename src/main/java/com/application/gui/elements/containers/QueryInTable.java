package com.application.gui.elements.containers;


import com.application.database.sql.Query;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class QueryInTable {
    
    public Tab tab;
    public TableView<ObservableList> tableView;
    public Query query;
    
    
    public QueryInTable(Tab tab, TableView<ObservableList> tableView, Query query) {
        this.tab = tab;
        this.tableView = tableView;
        this.query = query;
    }
    
    public void initialize() {
        tab.setContent(tableView);
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(tab)
                .append(tableView)
                .append(query)
                .toHashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof QueryInTable))
            return false;
        
        QueryInTable queryInTable = (QueryInTable) o;
        
        return new EqualsBuilder()
                .append(tab, queryInTable.tab)
                .append(tableView, queryInTable.tableView)
                .append(query, queryInTable.query)
                .isEquals();
    }
}
