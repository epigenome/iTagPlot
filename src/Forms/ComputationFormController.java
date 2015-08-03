/*
 *  iTagPlot
 *  2014
 */
package Forms;

import Controls.MessageBox;
import Objects.ProcessExecutor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import tagviz.TagViz;

/**
 * FXML Controller class
 *
 * @author jechoi
 */
public class ComputationFormController implements Initializable {
    @FXML
    private ListView<String> samList;
    @FXML
    private TextField confText;
    @FXML
    private ToggleGroup runGroup;
    @FXML
    private TextField threadText;
    @FXML
    private TextField gridText;
    @FXML
    private ToggleGroup dataGroup;
    @FXML
    private TextField fragmentText;
    @FXML
    private TextField fragFileText;
    @FXML
    private Button fragFileButton;
    @FXML
    private TextField columnText;
    @FXML
    private TextField dirText;
    @FXML
    private TextField outText;
    @FXML
    private TextField perlText;
    @FXML
    private TextField samtoolsText;
    @FXML
    private TextArea logText;
    @FXML
    private Button computeButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<?> formatCombo;

    Stage parent;
    Task task;
    Process child;
    int status = 0; // 0 - normal, 1 - running, 2 - done
    final ObservableList<String> listItems = FXCollections.observableArrayList();
    int runMode = -1;
    int dataType = -1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	samList.setItems(listItems);
	gridText.setText(TagViz.gridCmd);
	threadText.setText(TagViz.cores);
	perlText.setText(TagViz.perl);
	samtoolsText.setText(TagViz.samtools);
    }

    @FXML
    private void chooseSamples(ActionEvent event) {
        final List<File> paths = TagViz.ChooseFiles(TagViz.compDirectory);
	if (paths == null || paths.isEmpty()) return;
	TagViz.compDirectory = paths.get(0).getParentFile();

        try {
            int j = paths.get(0).getCanonicalPath().lastIndexOf('.');
            if (j != -1) {
                j = formatCombo.getItems().indexOf(paths.get(0).getCanonicalPath().substring(j+1).toUpperCase());
                if (j != -1) formatCombo.getSelectionModel().select(j);
            }
        } catch (IOException ex) {
        }

	for (File f : paths) {
	    try {
		listItems.addAll(f.getCanonicalPath());
	    } catch (IOException ex) {
	    }
	}
    }

    @FXML
    private void chooseConfFile(ActionEvent event) {
        final File path = TagViz.ChooseFile(TagViz.annDirectory);
	if (path == null) return;
	TagViz.annDirectory = path.getParentFile();
	try {
	    confText.setText(path.getCanonicalPath());
	} catch (IOException ex) {
	}
    }

    @FXML
    private void chooseBaseDir(ActionEvent event) {
        final File path = TagViz.ChooseDirectory(TagViz.annDirectory);
	if (path == null) return;
	try {
	    dirText.setText(path.getCanonicalPath());
	} catch (IOException ex) {
	}
    }

    @FXML
    private void chooseOutDir(ActionEvent event) {
        final File path = TagViz.ChooseDirectory(TagViz.compDirectory);
	if (path == null) return;
	try {
	    outText.setText(path.getCanonicalPath());
	} catch (IOException ex) {
	}
    }

    @FXML
    private void choosePerl(ActionEvent event) {
        final File path = TagViz.ChooseFile(null);
	if (path == null) return;
	try {
	    perlText.setText(path.getCanonicalPath());
	} catch (IOException ex) {
	}
    }

    @FXML
    private void chooseSamtools(ActionEvent event) {
        final File path = TagViz.ChooseFile(null);
	if (path == null) return;
	try {
	    samtoolsText.setText(path.getCanonicalPath());
	} catch (IOException ex) {
	}
    }

    @FXML
    private void chooseFragFile(ActionEvent event) {
        final File path = TagViz.ChooseFile(TagViz.compDirectory);
	if (path == null) return;
	try {
	    fragFileText.setText(path.getCanonicalPath());
	} catch (IOException ex) {
	}
    }

    @FXML
    private void serialSelected(ActionEvent event) {
	threadText.setDisable(true);
	gridText.setDisable(true);
	runMode = 0;
    }

    @FXML
    private void coreSelected(ActionEvent event) {
	threadText.setDisable(false);
	gridText.setDisable(true);
	runMode = 1;
    }

    @FXML
    private void gridSelected(ActionEvent event) {
	threadText.setDisable(false);
	gridText.setDisable(false);
	runMode = 2;
    }

    @FXML
    private void enrichmentSelected(ActionEvent event) {
	fragmentText.setDisable(false);
	fragFileText.setDisable(false);
	fragFileButton.setDisable(false);
	columnText.setDisable(true);
	dataType = 0;
    }

    @FXML
    private void betaSelected(ActionEvent event) {
	fragmentText.setDisable(true);
	fragFileText.setDisable(true);
	fragFileButton.setDisable(true);
	columnText.setDisable(false);
	dataType = 1;
    }

    @FXML
    private void compute(ActionEvent event) {
	if (computeButton.getText().equalsIgnoreCase("done")) {
	    TagViz.gridCmd = gridText.getText();
	    TagViz.cores = threadText.getText();
	    TagViz.perl = perlText.getText();
	    TagViz.samtools = samtoolsText.getText();
	    ((Node)(event.getSource())).getScene().getWindow().hide();
	    return;
	}
	
	String msg = checkInputs();
	if (msg != null) {
	    MessageBox.show(parent, "Error", msg);
	    return;
	}
	
	String dir;
        try {
            dir = ProcessExecutor.deploy(TagViz.featureScript, TagViz.batchScript);
        } catch (IOException ex) {
	    MessageBox.show(parent, "Error", ex.getMessage());
            return;
        }
        
	final String cmd = buildCommand(dir);
	status = 1;
	setButtonState();
	logText.setText(cmd + "\n");

        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
		execute(cmd);
                return null;
            }

            /* action to take after read is complete */
            @Override
            protected void done() {
                super.done();
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
			setButtonState();
		    }
		});
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void cancel(ActionEvent event) {
	if (computeButton.isDisabled()) {
	    child.destroy();
	    task.cancel(true);
	    status = 0;
	} else {
	    ((Node)(event.getSource())).getScene().getWindow().hide();
	}
    }
    
    public void setStage(Stage parent) {
	this.parent = parent;
    }
    
    void setButtonState() {
	switch (status) {
	    case 0: // Normal
		computeButton.setText("Compute");
		computeButton.setDisable(false);
		cancelButton.setDisable(false);
		break;
	    case 1: // Running
		computeButton.setDisable(true);
		cancelButton.setDisable(false);
		break;
	    case 2: // Done
		computeButton.setText("Done");
		computeButton.setDisable(false);
		cancelButton.setDisable(true);
	}
    }

    final String checkInputs() {
	if (listItems.size() == 0) return "Please choose sample files";
	if (confText.getText().isEmpty()) return "Please choose an annotation configuration file";
	if (outText.getText().isEmpty()) return "Please choose an output directory";
	if (runMode < 0 || runMode > 2) {
	    return "Please select Run Mode";
	} else if (runMode == 1) {
	    if (threadText.getText().isEmpty()) return "Please enter the number of threads";
	} else if (runMode == 2) {
	    if (threadText.getText().isEmpty()) return "Please enter the number of threads";
	    if (gridText.getText().isEmpty()) return "Please enter the command line for grid engine";
	}
	if (dataType < 0 || dataType > 1) {
	    return "Please select Data Type";
	} else if (dataType == 1 && columnText.getText().isEmpty()) {
	    return "Please enther column number for beta scores";
	}
	if (formatCombo.getSelectionModel().getSelectedItem() == null) return "Please select the type of input files";
	
	return null;
    }

    String buildCommand(String dir) {
	StringBuilder sb = new StringBuilder();
	if (!perlText.getText().isEmpty()) sb.append(perlText.getText()).append(' ');
        sb.append(dir).append(File.separator).append(TagViz.batchScript);
        sb.append(" -o ").append(outText.getText());
        sb.append(" -conf ").append(confText.getText());
        if (!dirText.getText().isEmpty()) sb.append(" -base ").append(dirText.getText());
        if (runMode == 1 || runMode == 2) sb.append(" -thread ").append(threadText.getText());
        if (runMode == 2) sb.append(" -grid ").append(gridText.getText());
        if (dataType == 0) {
            if (!fragmentText.getText().isEmpty()) sb.append(" -fragment ").append(fragmentText.getText());
            if (!fragFileText.getText().isEmpty()) sb.append(" -size ").append(fragFileText.getText());
        } else if (dataType == 1) {
            sb.append(" -m -score ").append(columnText.getText());
        }
        sb.append(" -out ").append(outText.getText());
        sb.append(" -type ").append(formatCombo.getSelectionModel().getSelectedItem().toString().toLowerCase());
        if (!samtoolsText.getText().isEmpty()) sb.append(" -samtools ").append(samtoolsText.getText());
        for (String s : listItems) sb.append(' ').append(s);
	return sb.toString();
    }
    
    private boolean execute(String cmd) {
	try {
	    child = Runtime.getRuntime().exec(cmd);
	    
	    BufferedReader bo_stream = new BufferedReader(new InputStreamReader(child.getInputStream()));
	    Thread thread_stdout = new Thread(new CommandRunThread(bo_stream, false));
	    thread_stdout.start();
	    
	    BufferedReader be_stream = new BufferedReader(new InputStreamReader(child.getErrorStream()));
	    Thread thread_error = new Thread(new CommandRunThread(be_stream, true));
	    thread_error.start();
	    
	    child.waitFor();
	    
	    thread_stdout.join(100);
	    thread_error.join(100);
	    if (thread_stdout.isAlive())
		thread_stdout.interrupt();
	    if (thread_error.isAlive())
		thread_error.interrupt();
	    
	    if(child.exitValue() != 0) {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
			logText.appendText("Exit code: " + child.exitValue() + "\n");
		    }
		});
		status = 0;
		return false;
	    }
	} catch (IOException | InterruptedException ex) {
	    Platform.runLater(new Runnable() {
		@Override
		public void run() {
		    logText.appendText("\n[ERROR] " + (ex.getMessage().isEmpty() ? "" : ex.getMessage()) + "\n");
		}
	    });
	}
	
	status = 2;
	return true;
    }

    class CommandRunThread implements Runnable{
	BufferedReader bufferReader;
	boolean flag;
	
	public CommandRunThread(BufferedReader br, boolean b) {
	    bufferReader = br;
	    flag = b;
	}
	
	public void run()
	{	
	    try
	    {
		while (true) {
		    final String str = bufferReader.readLine();
		    if (str == null) break;
		    Platform.runLater(new Runnable() {
			@Override
			public void run() {
			    if (flag) logText.appendText("[ERROR] ");
			    logText.appendText(str + "\n");
			}
		    });
		} 
	    }
	    catch(final IOException ex) { 
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
			logText.appendText("\n[ERROR] " + ex.getMessage() + "\n");
		    }
		});
	    }
	}		
    }
}
