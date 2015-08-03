/*
 *   =====================================================================
 *   TAGVIZ 1.2
 *   =====================================================================
 *   Authors Neea Rusch and CJ White
 *   Georgia Regents University 
 *   CSCI 4712 Senior Capstone Project, Spring 2014
 *   for Dr. Choi, GRU Cancer Research Center
 *
 */
package tagviz;

import Controls.MessageBox;
import Objects.Feature;
import Objects.Group;
import Objects.IDConvention;
import Objects.Sample;
import Objects.SelectedItem;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TagViz extends Application {
    public final static int MAX_INFO_COLUMN = 5;

    /* Controllers and data collections */
    public String[] header;
    public HashMap<String, Object> groupMap = new HashMap<>();
    public HashMap<String, Object> samMap = new HashMap<>();
    public HashMap<String, Object> featMap = new HashMap<>();
    public SelectedItem selectedItems = new SelectedItem(this);
    public HashMap<String, IDConvention> idMap = new HashMap<>();
    private final ReadOnlyIntegerWrapper sampleProperty = new ReadOnlyIntegerWrapper();
    private final ReadOnlyIntegerWrapper groupProperty = new ReadOnlyIntegerWrapper();
    public Stage stage;
    
    private static MainFormController main;
    
    public static String msigUrl = "http://www.broadinstitute.org/gsea/msigdb/";
    public static final String app_name = "iTagPlot";
    public static final String icon_path = "/images/icon.png";
    public static String settings_file;
    public static final String NA = "NA";
    public static final String version = "1.0";	// MASTER
    private static final String fxml = "MainForm.fxml";
    public static final String batchScript = "/bin/tag_density";
    public static final String featureScript = "/bin/feature.pl";

    // serializable objects
    public static File visDirectory;
    public static File msigDirectory;
    public static File compDirectory;
    public static File annDirectory;
    public static int maxSeries = 100;
    public static String ruleDelimeter = "|";
    public static String ruleStartIndex = "0";
    public static String ruleEndIndex = "0";
    public static String gridCmd = "qsub -cwd -V -j y -b y -sync y -N itagplot";
    public static String cores = "4";
    public static String perl = "";
    public static String samtools = "";
    
    // parameters for ClusteredFeature
    public static class ClusteredFeatureParameter implements Serializable {
        ClusteredFeatureParameter() {
            cfNumClusters  = 2;
            cfDistance     = "Euclidean";
            cfSampleInARow = false;
            cfPrefix       = "cluster";
        }
        
        public int     cfNumClusters;
        public String  cfDistance;
        public boolean cfSampleInARow;
        public String  cfPrefix;
    }
    
    public static ClusteredFeatureParameter clusteredFeatureParameter = new ClusteredFeatureParameter();
    
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
	try {
	    settings_file = System.getProperty("user.home") + File.separatorChar + "." + app_name.toLowerCase();
	} catch (AccessControlException ex) {
	}

        /* load main form */
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(TagViz.class.getResource(fxml));
            AnchorPane page = (AnchorPane) fxmlLoader.load();
	    Rectangle2D scr = Screen.getPrimary().getVisualBounds();
            Scene scene = (scr.getWidth() > 1500) ? new Scene(page, 1400, 1000) : new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(icon_path));
            primaryStage.setTitle(String.format("%s %s", app_name, version));
            stage = primaryStage;
            main = (MainFormController) fxmlLoader.getController();
	    main.postInitialization(this);
	    if (visDirectory == null) visDirectory = new File(System.getProperty("user.home"));
            primaryStage.show();
	    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		@Override
		public void handle(WindowEvent e) {
		    main.ExitApplication();
		}
	    });
        } catch (IOException ex) {
            MessageBox.show(null, "Error", "loding error of " + fxml);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Support methods">
/*    public void UploadSample() {
        CloseExistingControllers();
        uCtrl = new Controls.UploadControl(Objects.DataItem.TYPE.SAMPLE, this, main);
    }

    public void UploadGroup() {
        CloseExistingControllers();
        uCtrl = new Controls.UploadControl(Objects.DataItem.TYPE.GROUP, this, main);
    }

    public void DeleteSample() {
        CloseExistingControllers();
        dCtrl = new Controls.DeleteControl(Objects.DataItem.TYPE.SAMPLE, main);
    }

    public void DeleteGroup() {
        CloseExistingControllers();
        dCtrl = new Controls.DeleteControl(Objects.DataItem.TYPE.GROUP, main);
    }

    private static void CloseExistingControllers() {
        if (uCtrl != null) {
            uCtrl.CloseForm();
        }
        if (dCtrl != null) {
            dCtrl.CloseForm();
        }
    }
*/
    public static void ExitApplication() {
        Platform.exit();
    }
    // </editor-fold>
    public static File ChooseFile(File def) {
        Stage stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
	try { if (def == null) def = new File(System.getProperty("user.home")); }
	catch (AccessControlException ex) {}
        fileChooser.setInitialDirectory(def);
        fileChooser.setTitle("Choose a File");

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) def = file.getParentFile();
        return file;
    }

    public static List<File> ChooseFiles(File def) {
        Stage stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        try { if (def == null) def = new File(System.getProperty("user.home")); }
	catch (AccessControlException ex) { }
        
        try { if ( !def.exists() ) {
                def = new File( System.getProperty("user.dir") );
            }
        } catch (AccessControlException ex ) { }
        
        fileChooser.setInitialDirectory(def);
        fileChooser.setTitle("Choose Files");
        
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null) def = files.get(0).getParentFile();
        return files;
    }

    public static File ChooseDirectory(File def) {
        Stage stage = new Stage();
        final DirectoryChooser fileChooser = new DirectoryChooser();
        try { if (def == null) def = new File(System.getProperty("user.home")); }
	catch (AccessControlException ex) {}
        fileChooser.setInitialDirectory(def);
        fileChooser.setTitle("Choose a Directory");

        File file = fileChooser.showDialog(stage);
        if (file != null) def = file;
        return file;
    }

    public void addFeature(Feature feat)
    {
	if (!featMap.containsKey(feat.getName())) {
            synchronized (featMap) {
                featMap.put(feat.getName(), feat);
            }
        }
    }
        
    public void addFeatureToGroup(Group grp, String sam, String feat) {
        IDConvention rule = idMap.get(grp.getName());
        if (rule == null) {
            grp.add(sam, feat);
        } else {
            ArrayList<String> fs = rule.get(feat);
            if (fs != null) grp.addAll(sam, fs);
        }
    }
    
    public void addSample(Sample sam) throws IOException {
        addSample( sam, true );
    }
    
    public void addSample(Sample sam, boolean initialize) throws IOException
    {
        synchronized (samMap) {
            samMap.put(sam.getName(), sam);
            sampleProperty.set(samMap.size());
        }
        
        if( initialize ) {
            for (Object obj : groupMap.values())
                sam.computeTags((Group) obj);
        }
    }
    
    public void removeSample(String name ) {
        removeSample( name, true );
    }
    
    public void removeSample(String name, boolean terminate )
    {
        samMap.remove(name);
        sampleProperty.set(samMap.size());
        
        if( terminate ) {
            //nothing to do yet.
        }
    }

    public int getSampleProperty() {
        return sampleProperty.get();
    }

    public ReadOnlyIntegerProperty sampleProperty() {
        return sampleProperty.getReadOnlyProperty();
    }

    public void addGroup(Group grp) throws IOException {
        addGroup( grp, true );
    }
    
    public void addGroup(Group grp, boolean initialize) throws IOException {
	synchronized (groupMap) {
            groupMap.put(grp.getName(), grp);
            groupProperty.set(groupMap.size());
        }
        
        if( initialize ) {
            for (String sam : samMap.keySet()) {
                if (((Sample) samMap.get(sam)).computeTags(grp) == null)
                    main.log.append(sam + " has no features in " + grp.getName() + "\n");
            }
        }
    }
    
    public void removeGroup(String name) {
        removeGroup( name, true );
    }

    public void removeGroup(String name, boolean terminate)
    {
        groupMap.remove(name);
        groupProperty.set(groupMap.size());
        
        if( terminate ) {
            //nothing to do yet.
        }
    }

    public int getGroupProperty() {
        return groupProperty.get();
    }

    public ReadOnlyIntegerProperty groupProperty() {
        return groupProperty.getReadOnlyProperty();
    }

    public void updateTitle()
    {
        String title;
        if (samMap.isEmpty() && groupMap.isEmpty()) 
            title = String.format("%s %s", app_name, version);
        else if (samMap.isEmpty())                    
            title = String.format("%s %s (%d groups)", app_name, version, groupMap.size());
        else if (groupMap.isEmpty())                  
            title = String.format("%s %s (%d samples %d features)", app_name, version, samMap.size(), featMap.size());
        else                                            
            title = String.format("%s %s (%d samples %d groups %d features)", app_name, version, samMap.size(), groupMap.size(), featMap.size());
        stage.setTitle(title);
    }

    void removeObject(boolean isSample, String name) {
        if (isSample) removeSample(name);
        else          removeGroup(name);
    }
}
