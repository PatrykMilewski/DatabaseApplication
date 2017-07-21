package com.application.gui.abstracts.consts.values;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

public final class ConstValues {
    private static final MavenXpp3Reader READER = new MavenXpp3Reader();
    
    private ConstValues() { }
    
    public static final String USER_DATA_FILE = "data/users/";
    private static Model model;
    public static final String ARTIFACT_ID = "DatabaseApplication";
    public static final String VERSION = "0.1-ALFA";
    
    static {
        model = null;
        try {
            model = READER.read(new FileReader("pom.xml"));
        } catch (IOException | XmlPullParserException e) {
            model = null;
        }
    }

    public static Model getModel() {
        return model;
    }
    
    public static String getArtifactId() {
        if (model == null)
            return ARTIFACT_ID;
        else
            return model.getArtifactId();
    }
    
    public static String getVersion() {
        if (model == null)
            return VERSION;
        else
            return model.getVersion();
    }
}
