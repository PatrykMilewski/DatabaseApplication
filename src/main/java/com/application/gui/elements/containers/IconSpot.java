package com.application.gui.elements.containers;


import com.application.gui.abstracts.consts.enums.SpotStances;
import javafx.scene.image.Image;

import java.io.InputStream;


class IconSpot {

    SpotStances stance;
    final Image[] images = new Image[2];
    boolean imageSwitch = false;
    
    IconSpot(SpotStances stance, final String paths[]) {
        this.stance = stance;
        if (paths != null) {
            try {
                images[0] = new Image(getClass().getClassLoader().getResourceAsStream(paths[0]));
            } catch (Exception e) {
                images[0] = null;
            }
            try {
                images[1] = new Image(getClass().getClassLoader().getResourceAsStream(paths[1]));
            } catch (Exception e) {
                images[1] = null;
            }
        }
        else {
            images[0] = null;
            images[1] = null;
        }
    }
    
    Image getImage() {
        return imageSwitch ? images[0] : images[1];
    }
}
