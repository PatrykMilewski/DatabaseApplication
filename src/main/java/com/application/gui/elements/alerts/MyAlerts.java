package com.application.gui.elements.alerts;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Statement;

public abstract class MyAlerts {
    
    private MyAlerts() { }
    
    public static void showExceptionAlert(String headerText, Exception e, boolean wait) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Wystąpił wyjątek");
        alert.setHeaderText(headerText);
        alert.setContentText(e.getMessage());
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();
    
        Label label = new Label("The exception stacktrace was:");
    
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
    
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
    
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        
        alert.getDialogPane().setExpandableContent(expContent);
    
        showAndWait(alert, wait);
    }
    
    public static void showInfoAlert(String title, String header, String context, boolean wait) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
    
        showAndWait(alert, wait);
    }
    
    public static void showInfoAlertMultipleSets(String result, boolean wait) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wyświetlenie wyniku");
        alert.setHeaderText("Pomyślnie wykonano polecenie SQL.");
        alert.setContentText("Polecenie SQL jest nietypowe i jego wynik zostanie wyświetlony w tym oknie.");
    
        Label label = new Label("Wynik polecenia SQL został wyświetlony poniżej.");
    
        TextArea textArea = new TextArea(result);
        textArea.setEditable(false);
        textArea.setWrapText(true);
    
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
    
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
    
        showAndWait(alert, wait);
    }
    
    private static void showAndWait(Alert alert, boolean wait) {
        if (wait)
            Platform.runLater(alert::showAndWait);
        else
            Platform.runLater(alert::show);
    }
    
}
