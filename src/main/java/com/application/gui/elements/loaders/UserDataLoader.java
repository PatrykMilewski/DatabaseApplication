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
    
    private static Logger log = LoggerFactory.getLogger(UserDataLoader.class.getCanonicalName());
    
    private final static String SAVEDHOSTSNAME, SAVEDUSERSNAME;
    
    private static Set<String> savedHosts;
    private static Set<String> savedUsers;
    private static boolean dataLoaded = false;
    private static boolean lockLoadingData = false;
    
    static {
        SAVEDHOSTSNAME = "hostsNamesSet.dat";
        SAVEDUSERSNAME = "usersNamesSet.dat";
        
        savedHosts = new LinkedHashSet<>();
        savedUsers = new LinkedHashSet<>();
    }
    
    synchronized public void loadData() {
        if (dataLoaded)
            return;
    
        while (lockLoadingData) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        String appData = System.getenv("APPDATA");
        String artifactId = ConstValues.getArtifactId(), version = ConstValues.getVersion();

        String dataPath = Paths.get(appData, artifactId, version).toString();
        System.out.println(dataPath);
        
        String hostsDataPath = Paths.get(dataPath, SAVEDHOSTSNAME).toString();
        String usersDataPath = Paths.get(dataPath, SAVEDUSERSNAME).toString();
        
        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(new FileInputStream(hostsDataPath))) {
            //noinspection unchecked
            savedHosts = (LinkedHashSet<String>) objectInputStream.readObject();
        }
        catch (FileNotFoundException e) {
            log.log(Level.INFO, "Not found saved hosts file.");
        }
        catch (IOException | ClassNotFoundException | ClassCastException e) {
            log.log(Level.WARNING, "Failed to load saved hosts.");
            log.log(Level.WARNING, e.getMessage(), e);
        }
        
        try(ObjectInputStream objectInputStream =
                    new ObjectInputStream(new FileInputStream(usersDataPath))) {
            //noinspection unchecked
            savedUsers = (LinkedHashSet<String>) objectInputStream.readObject();
        }
        catch (FileNotFoundException e) {
            log.log(Level.INFO, "Not found saved users file.");
        }
        catch ( IOException | ClassNotFoundException | ClassCastException e) {
            log.log(Level.WARNING, "Failed to load saved users.");
            log.log(Level.WARNING, e.getMessage(), e);
        }
        
        dataLoaded = true;
    }
    
    public void addNewData(String newHost, String newUser) {
        if (newHost != null)
            savedHosts.add(newHost);
        
        if (newUser != null)
            savedUsers.add(newUser);
    }
    
    synchronized public void saveData() {
        lockLoadingData = true;
    
        Model model = ConstValues.getModel();
        String appData = System.getenv("APPDATA");
        String dataPath = Paths.get(appData, model.getArtifactId(), model.getVersion()).toString();
        
        String hostsDataPath = Paths.get(dataPath, SAVEDHOSTSNAME).toString();
        String usersDataPath = Paths.get(dataPath, SAVEDUSERSNAME).toString();
        
        try {
            Files.createDirectories(Paths.get(dataPath));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create directory in APPDATA for program files.");
            log.log(Level.SEVERE, e.getMessage(), e);
            lockLoadingData = false;
            notifyAll();
            return;
        }
        
        if (savedHosts != null && !savedHosts.isEmpty()) {
            try (ObjectOutputStream objectOutputStream =
                         new ObjectOutputStream(new FileOutputStream(hostsDataPath))) {
                objectOutputStream.writeObject(savedHosts);
                log.log(Level.INFO, "Successfully saved hosts data set.");
            }
            catch (IOException e) {
                log.log(Level.WARNING, "Failed to save hosts data set.");
                log.log(Level.WARNING, e.getMessage(), e);
            }
        }
        
        if (savedUsers != null && !savedUsers.isEmpty()) {
            try (ObjectOutputStream objectOutputStream =
                         new ObjectOutputStream(new FileOutputStream(usersDataPath))) {
                objectOutputStream.writeObject(savedUsers);
                log.log(Level.INFO, "Successfully saved users data set.");
            }
            catch (IOException e) {
                log.log(Level.WARNING, "Failed to save users data set.");
                log.log(Level.WARNING, e.getMessage(), e);
            }
        }
        
        lockLoadingData = false;
        notifyAll();
    }
    
    public Set<String> getSavedHosts() {
        return savedHosts;
    }
    
    public Set<String> getSavedUsers() {
        return savedUsers;
    }
}
