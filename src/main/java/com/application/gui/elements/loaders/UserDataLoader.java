package com.application.gui.elements.loaders;

import com.application.gui.abstracts.consts.values.ConstValues;
import com.application.gui.abstracts.factories.LoggerFactory;
import org.apache.maven.model.Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDataLoader {
    
    private final static String SAVED_HOSTS_NAME, SAVED_USERS_NAME;
    
    private static DataLoader<Set<String>> hostsLoader, usersLoader;
    private static Set<String> savedHosts, savedUsers;
    private static boolean dataLoaded = false;
    
    static {
        SAVED_HOSTS_NAME = "hostsNamesSet.dat";
        SAVED_USERS_NAME = "usersNamesSet.dat";
    
        hostsLoader = new DataLoader<>(SAVED_HOSTS_NAME);
        usersLoader = new DataLoader<>(SAVED_USERS_NAME);
    
        savedHosts = new LinkedHashSet<>();
        savedUsers = new LinkedHashSet<>();
    }
    
    public void loadData() {
        if (dataLoaded)
            return;
    
        savedHosts = hostsLoader.loadData();
        savedUsers = usersLoader.loadData();
        
        dataLoaded = true;
    }
    
    public void addNewData(String newHost, String newUser) {
        if (newHost != null)
            savedHosts.add(newHost);
        
        if (newUser != null)
            savedUsers.add(newUser);
    }
    
    public void saveData() {
        hostsLoader.saveData(savedHosts);
        hostsLoader.saveData(savedUsers);
    }
    
    public Set<String> getSavedHosts() {
        return savedHosts;
    }
    
    public Set<String> getSavedUsers() {
        return savedUsers;
    }
}
