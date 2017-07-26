package com.application.gui.elements.containers;


import com.application.gui.abstracts.consts.enums.SpotStances;
import javafx.scene.image.Image;

import java.nio.file.Paths;
import java.util.ArrayList;

public class IconSpotsContainer {
    
    private static final String IMAGES_PATH;
    private static final String IMAGES[][];
    
    static {
        IMAGES_PATH = Paths.get("images", "icons").toString();
        
        int size = SpotStances.values().length;
        IMAGES = new String[size][2];
        for (int i = 1; i < size; i++)
            IMAGES[i] = new String[2];
        
        IMAGES[0][0] = Paths.get(IMAGES_PATH, "connected.png").toString();
        IMAGES[0][1] = Paths.get(IMAGES_PATH, "disconnected.png").toString();
        //IMAGES[1][0] = Paths.get(IMAGES_PATH, "working.png").toString();
        //IMAGES[1][1] = Paths.get(IMAGES_PATH, "notWorking.png").toString();
    }
    
    private ArrayList<IconSpot> spots = new ArrayList<>(SpotStances.values().length);
    
    {
        int i = 0;
        for (SpotStances stance : SpotStances.values()) {
            spots.add(new IconSpot(stance, IMAGES[i]));
            i++;
        }
    }
    
    public int changeSpotStance(SpotStances spotStance, boolean newStance) {
        int i = 0;
        for (IconSpot spot : spots) {
            if (spot.stance == spotStance) {
                spot.imageSwitch = newStance;
                return i;
            }
            i++;
        }
        return -1;
    }
    
    public Image getIconImage(SpotStances spotStance) {
        for (IconSpot spot : spots) {
            if (spot.stance == spotStance)
                return spot.getImage();
        }
        return null;
    }
}
