package com.application.gui.elements.loaders;

import com.application.gui.abstracts.consts.values.ConstValues;
import com.application.gui.abstracts.factories.LoggerFactory;
import org.apache.maven.model.Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

class DataLoader <E> {
    
    private static Logger log = LoggerFactory.getLogger(DataLoader.class.getCanonicalName());
    
    private E data = null;
    private String filePath;
    
    DataLoader(String filePath) {
        this.filePath = filePath;
    }

    synchronized void loadData() {
        
        String appData = System.getenv("APPDATA");
        String artifactId = ConstValues.getArtifactId(), version = ConstValues.getVersion();
    
        String dataPath = Paths.get(appData, artifactId, version).toString();
    
        String finalDataPath = Paths.get(dataPath, filePath).toString();
    
        
        
        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(new FileInputStream(finalDataPath))) {
            //noinspection unchecked
            data = (E) objectInputStream.readObject();
        }
        catch (FileNotFoundException e) {
            log.log(Level.INFO, "Not found saved data for path: " + dataPath + ".");
        }
        catch (IOException | ClassNotFoundException | ClassCastException e) {
            log.log(Level.WARNING, "Failed to load saved data for path: " + dataPath + "a   .");
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }
    
    synchronized void saveData(final E data) {
        
        Model model = ConstValues.getModel();
        String appData = System.getenv("APPDATA");
        String dataPath = Paths.get(appData, model.getArtifactId(), model.getVersion()).toString();
    
        String finalDataPath = Paths.get(dataPath, filePath).toString();
    
        try {
            Files.createDirectories(Paths.get(dataPath));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create directory in APPDATA for program files.");
            log.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
    
        if (data != null) {
            try (ObjectOutputStream objectOutputStream =
                         new ObjectOutputStream(new FileOutputStream(finalDataPath))) {
                objectOutputStream.writeObject(data);
                log.log(Level.INFO, "Successfully saved data.");
            }
            catch (IOException e) {
                log.log(Level.WARNING, "Failed to save data.");
                log.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    E getData() {
        return data;
    }
}
