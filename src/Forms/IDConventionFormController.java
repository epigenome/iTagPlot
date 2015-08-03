/*
 *  TagViz
 *  2014
 */

package Forms;

import Objects.IDConvention;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import tagviz.TagViz;

/**
 * FXML Controller class
 *
 * @author jechoi
 */
public class IDConventionFormController implements Initializable {
    private boolean ok = false;
    @FXML
    private TextField delText;
    @FXML
    private TextField endText;
    @FXML
    private TextField startText;
    @FXML
    private Label samLabel;
    @FXML
    private Label conLabel;
    @FXML
    private Label grpLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	delText.setText(TagViz.ruleDelimeter);
	startText.setText(TagViz.ruleStartIndex);
	endText.setText(TagViz.ruleEndIndex);
	startText.addEventFilter(KeyEvent.KEY_TYPED , integer_Validation(2));
	endText.addEventFilter(KeyEvent.KEY_TYPED , integer_Validation(2));
    }    

    @FXML
    private void okAction(ActionEvent event) {
	if (!validate()) return;
	ok = true;
	TagViz.ruleDelimeter = getDelimeter();
	TagViz.ruleStartIndex = startText.getText();
	TagViz.ruleEndIndex = endText.getText();
	((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    private void cancelAction(ActionEvent event) {
	ok = false;
  	((Node)(event.getSource())).getScene().getWindow().hide();
    }
    
    @FXML
    private void onKeyReleased(KeyEvent event) {
	updateExample();
    }
    
    public boolean isOk() {
	return ok;
    }

    public void initLabels(String samFeat, String grpFeat) {
	samLabel.setText(samFeat);
	grpLabel.setText(grpFeat);
	updateExample();
    }
    private boolean validate() {
	return !delText.getText().isEmpty() && getIndex(true) != -1 && getIndex(false) != -1;
    }

    public String getDelimeter() {
	return delText.getText();
    }

    public int getIndex(boolean flag) {
	try {
	    int n = Integer.parseInt(flag ? startText.getText() : endText.getText());
	    return n;
	} catch (NumberFormatException ex) {
	    return -1;
	}
    }

    void updateExample() {
	if (!samLabel.getText().isEmpty() && validate()) conLabel.setText(IDConvention.create(getDelimeter(), getIndex(true), getIndex(false)).convert(samLabel.getText()));
    }
    
    /* Numeric Validation Limit the  characters to maxLengh AND to ONLY DigitS *************************************/
    public EventHandler<KeyEvent> integer_Validation(final Integer max_Lengh) {
	return new EventHandler<KeyEvent>() {
	    @Override
	    public void handle(KeyEvent e) {
		TextField txt_TextField = (TextField) e.getSource();                
		if (txt_TextField.getText().length() >= max_Lengh) e.consume();
		else if (!e.getCharacter().matches("[0-9]")) e.consume();
	    }
	};
    }    
 
    /* Numeric Validation Limit the  characters to maxLengh AND to ONLY DigitS *************************************/
    public EventHandler<KeyEvent> numeric_Validation(final Integer max_Lengh) {
	return new EventHandler<KeyEvent>() {
	    @Override
	    public void handle(KeyEvent e) {
		TextField txt_TextField = (TextField) e.getSource();                
		if (txt_TextField.getText().length() >= max_Lengh) {                    
		    e.consume();
		}
		if(e.getCharacter().matches("[0-9.]")){ 
		    if(txt_TextField.getText().contains(".") && e.getCharacter().matches("[.]")){
			e.consume();
		    }else if(txt_TextField.getText().length() == 0 && e.getCharacter().matches("[.]")){
			e.consume(); 
		    }
		}else{
		    e.consume();
		}
	    }
	};
    }    
 
    /* Letters Validation Limit the  characters to maxLengh AND to ONLY Letters *************************************/
    public EventHandler<KeyEvent> letter_Validation(final Integer max_Lengh) {
	return new EventHandler<KeyEvent>() {
	    @Override
	    public void handle(KeyEvent e) {
		TextField txt_TextField = (TextField) e.getSource();                
		if (txt_TextField.getText().length() >= max_Lengh) {                    
		    e.consume();
		}
		if(e.getCharacter().matches("[A-Za-z]")){ 
		}else{
		    e.consume();
		}
	    }
	};
    }    
}