package com.application.gui.elements.loaders;


import java.util.LinkedHashSet;
import java.util.Set;

public class UserDataLoader {
    
    private final static String SAVED_HOSTS_NAME, SAVED_USERS_NAME;
    
    private static DataLoader<Set<String>> hostsLoader, usersLoader;
    private static Set<String> savedHosts, savedUsers;
    
    static {
        SAVED_HOSTS_NAME = "hostsNamesSet.dat";
        SAVED_USERS_NAME = "usersNamesSet.dat";
    
        hostsLoader = new DataLoader<>(SAVED_HOSTS_NAME);
        usersLoader = new DataLoader<>(SAVED_USERS_NAME);
    
        savedHosts = new LinkedHashSet<>();
        savedUsers = new LinkedHashSet<>();
    }
    
    public void loadData() {
        hostsLoader.loadData();
        usersLoader.loadData();
        if (hostsLoader.getData() != null)
            savedHosts = hostsLoader.getData();
        
        if (usersLoader.getData() != null)
            savedUsers = usersLoader.getData();
    }
    
    public void addNewData(String newHost, String newUser) {
        if (newHost != null && !savedHosts.contains(newHost))
            savedHosts.add(newHost);
        
        if (newUser != null && !savedUsers.contains(newUser))
            savedUsers.add(newUser);
    }
    
    public void saveData() {
        hostsLoader.saveData(savedHosts);
        usersLoader.saveData(savedUsers);
    }
    
    public Set<String> getSavedHosts() {
        return savedHosts;
    }
    
    public Set<String> getSavedUsers() {
        return savedUsers;
    }
}
