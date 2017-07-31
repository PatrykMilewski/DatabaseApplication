package com.application.gui.elements.loaders;

import com.application.database.sql.DatabaseFilter;
import javafx.scene.control.ListView;

import java.util.LinkedHashSet;
import java.util.Set;

public class FiltersDataLoader {
    
    private final static String SAVED_FILTERS_NAME;
    
    private static DataLoader<Set<DatabaseFilter>> filtersLoader;
    private static Set<DatabaseFilter> savedFilters;
    private static boolean dataLoaded = false;
    
    static {
        SAVED_FILTERS_NAME = "filtersSet.dat";
        filtersLoader = new DataLoader<>(SAVED_FILTERS_NAME);
        savedFilters = new LinkedHashSet<>();
    }
    
    public void loadData() {
        if (dataLoaded)
            return;
        
        savedFilters = filtersLoader.loadData();
        
        dataLoaded = true;
    }
    
    public void addNewData(DatabaseFilter newFilter) {
        if (newFilter != null)
            savedFilters.add(newFilter);
    }
    
    public void saveData() {
        filtersLoader.saveData(savedFilters);
    }
    
    public Set<DatabaseFilter> getSavedHosts() {
        return savedFilters;
    }
}