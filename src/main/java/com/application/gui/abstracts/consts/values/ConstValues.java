package com.application.gui.abstracts.consts.values;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

public final class ConstValues {
    private static final MavenXpp3Reader reader = new MavenXpp3Reader();
    
    private ConstValues() { }
    
    public static final String USERDATAFILE = "data/users/";
    private static Model model;
    
    static {
        model = null;
        try {
            model = reader.read(new FileReader("pom.xml"));
        } catch (IOException | XmlPullParserException e) {
            model = null;
            e.printStackTrace();
        }
    }
    
    
    public static Model getModel() {
        return model;
    }
}
