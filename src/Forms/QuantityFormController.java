/*
 *  TagViz
 *  2014
 */

package Forms;

import Controls.MessageBox;
import Controls.QuantityDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

/**
 * FXML Controller class
 *
 * @author jechoi
 */
public class QuantityFormController implements Initializable {
    @FXML
    private TableView<ObservableList<String>> fileTable;
    @FXML
    private TableView<ObservableList<StringProperty>> criteriaTable;
    @FXML
    private TableColumn<ObservableList<StringProperty>, String> criteriaNameCol;
    @FXML
    private TableColumn<ObservableList<StringProperty>, String> criteriaCutoffCol;
    @FXML
    private CheckBox gctCheck;
    
    private boolean ok = false;
    private Stage owner;
    private File path;
    public static final String[] COLUMN_CATEGORIES = {"Feat", "Info", "Sam"};

    private final HashSet<String>     m_SampleHeaders = new HashSet<>();
    private       Collection<String>  m_LoadedSamples = null;
    private final ArrayList<ComboBox> m_ComboBoxList  = new ArrayList<>();
    
    private static final String STRING_NONE = "==NONE==";
    
    class SampleMap {
        private final String loadedSample;
        private String nameInFile;
        
        SampleMap( String loadedSample, String nameInFile ) {
            this.loadedSample = loadedSample;
            this.nameInFile   = nameInFile;
        }
        
        public String getLoadedSample() {
            return loadedSample;
        }
        
        public String getNameInFile() {
            return nameInFile;
        }
        
        public void setNameInFile( String nameInFile ) {
            this.nameInFile = nameInFile;
        }
    }
    
    @FXML
    private TableView<SampleMap> mappingTable;
    @FXML
    private TableColumn colLoadedSample;
    @FXML
    private TableColumn colSampleInFile;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void CloseForm(ActionEvent event) {
	if (!validate() ) return;
	if( !m_LoadedSamples.isEmpty() ) ok = true;
	((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    private void CancelForm(ActionEvent event) {
	ok = false;
	((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    private void gctChanged(ActionEvent event) {
	initializeTable(gctCheck.isSelected());
    }
    
    public void initialize(Stage owner, QuantityDialog.Data data, File path, Collection<String> loadedSamples) {
	this.owner = owner;
	this.path = path;
	boolean b = path.getName().endsWith(".gct");
	gctCheck.setSelected(b);
        
        m_LoadedSamples = loadedSamples;
	initializeTable(b);
	initializeCriteria(data);
    }
    
    public void initializeCriteria(QuantityDialog.Data data) {
	criteriaNameCol.setCellValueFactory(new Callback<CellDataFeatures<ObservableList<StringProperty>,String>, ObservableValue<String>>(){                   
	    @Override
	    public ObservableValue<String> call(CellDataFeatures<ObservableList<StringProperty>, String> param) { 
		return param.getValue().get(0);
	    }                   
	});
	criteriaCutoffCol.setCellValueFactory(new Callback<CellDataFeatures<ObservableList<StringProperty>,String>, ObservableValue<String>>(){                   
	    @Override
	    public ObservableValue<String> call(CellDataFeatures<ObservableList<StringProperty>, String> param) {                                                                                             
		return param.getValue().get(1);                       
	    }                   
	});

//	criteriaNameCol.setCellFactory(TextFieldTableCell.<ObservableList<StringProperty>>forTableColumn());
//	criteriaCutoffCol.setCellFactory(TextFieldTableCell.<ObservableList<StringProperty>>forTableColumn());
	criteriaNameCol.setCellFactory(new CriteriaCellFactory(false));
	criteriaCutoffCol.setCellFactory(new CriteriaCellFactory(true));
/*
	criteriaCutoffCol.setOnEditCommit(new EventHandler<CellEditEvent<ObservableList<StringProperty>, String>>() {
		@Override
		public void handle(CellEditEvent<ObservableList<StringProperty>, String> t) {
//				    ((ObservableList) t.getTableView().getItems().get(t.getTablePosition().getRow())).set(t.getTableColumn().set.getNewValue());
		}
	});
*/	
	for (int i = 0; i < data.cutoffs.length; i++) {
	    final ObservableList<StringProperty> row = FXCollections.observableArrayList();
	    row.addAll(new SimpleStringProperty(i < data.prefix.length ? data.prefix[i] : ""), new SimpleStringProperty(data.cutoffs[i]));
	    criteriaTable.getItems().add(row);
	}
    }
    
    public void initializeTable(boolean isGCT) {
	fileTable.getColumns().clear();
	fileTable.getItems().clear();
        m_SampleHeaders.clear();
        
        String line;
	boolean first = true;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	    if (isGCT) {
		br.readLine(); // version
		br.readLine(); // dimension
	    }
	    
            for (int n = 0; (line = br.readLine()) != null; n++) {
		String[] sa = line.split("\t");
		
		if (first) {
                    final String[] header = sa;
		    for (int i = 0; i < sa.length; i++ ) {
			final int j = i;
			TableColumn col = new TableColumn();
			col.setPrefWidth(i == 0 ? 100 : 70);
			col.setSortable(false);
			col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList<String>,String>, ObservableValue<String>>(){                   
			    @Override
			    public ObservableValue<String> call(CellDataFeatures<ObservableList<String>, String> param) { 
				return new SimpleStringProperty(param.getValue().get(j));
			    }
			});
                        
			ObservableList<String> it = FXCollections.observableArrayList();
			it.addAll(COLUMN_CATEGORIES);
			ComboBox cb = new ComboBox(it);
                        if( i == 0 ) {
                            cb.getSelectionModel().select(0);
                        } else if( i == 1 && isGCT ) {
                            cb.getSelectionModel().select(1);
                        } else {
                            cb.getSelectionModel().select(2);
                            m_SampleHeaders.add(sa[i]);
                        }
                        
                        cb.setOnAction( new EventHandler() {
                            @Override
                            public void handle(Event t) {
                                boolean add = ((ComboBox)t.getTarget()).getValue().equals(COLUMN_CATEGORIES[2]);
                                updateSampleMapping(header[j], add);
                            }
                        });
                        
			col.setGraphic(cb);
	                fileTable.getColumns().addAll(col);
		    }
		    first = false;
		}

		ObservableList<String> row = FXCollections.observableArrayList();
//		row.addAll(Arrays.asList(sa)); // Why this don't work
		for (String v : sa) row.add(v);
		fileTable.getItems().add(row);
		if (n >= 7) break;
            }
            initializeMappingTable();
        } catch (FileNotFoundException ex) {
            MessageBox.show(owner, "Error", "File not found" + path);
        } catch (IOException ex) {
            MessageBox.show(owner, "Error", "Reading error" + path);
        }
    }

    public boolean isOk() {
	return ok;
    }

    public void setOk(boolean ok) {
	this.ok = ok;
    }
    
    public boolean isGCT() {
	return gctCheck.isSelected();
    }

    public Boolean getColumnCategoryAt(int n) {
	 String v = (String) ((ComboBox) fileTable.getColumns().get(n).getGraphic()).getSelectionModel().getSelectedItem();
	 if (v.equals(COLUMN_CATEGORIES[1])) return null;
	 return !v.equals(COLUMN_CATEGORIES[0]);
    }
    
    public Boolean[] getColumnCategory() {
	Boolean[] ba = new Boolean[fileTable.getColumns().size()];
	for (int i = 0; i < ba.length; i++) ba[i] = getColumnCategoryAt(i);
	return ba;
    }
    
    public String[] getCriteriaNames() {
	String[] sa = new String[criteriaTable.getItems().size()-1];
	for (int i = 0; i < sa.length; i++) sa[i] = criteriaNameCol.getCellData(i);
	return sa;
    }
    
    public Float[] getCriteriaValues() {
	Float[] fa = new Float[criteriaTable.getItems().size()-1];
	for (int i = 1; i <= fa.length; i++) {
	    String s = criteriaCutoffCol.getCellData(i);
	    fa[i-1] = s.equals("MAX") ? Float.MAX_VALUE : Float.parseFloat(s);
	}
	return fa;
    }
    
    public String getFeatureName() {
	return fileTable.getItems().get(1).get(0);
    }

    @SuppressWarnings("empty-statement")
    private Boolean validate() {
	int feat = 0; // #columns for the feature name should be 1
	int sam = 0; //#columns for the sample names should be equal to or greater than 1
	
	for (int i = 0; i < fileTable.getColumns().size(); i++) {
	    Boolean b = getColumnCategoryAt(i);
	    if      (b == null) ;
	    else if (b) sam++;
	    else        feat++;
	}
	
	if (feat != 1) {
	    MessageBox.show(null, "Error", "The number of columns for Feat should be one");
	    return false;
	}
	
	if (sam == 0) {
	    MessageBox.show(null, "Error", "The number of columns for Sam should be ast least one");
	    return false;
	}
	
	Float[] fa = getCriteriaValues();
	for (int i = 1; i < fa.length; i++) {
	    if (fa[i] < fa[i-1]) {
		MessageBox.show(null, "Error", "The values for criteria should be ordered ascendingly");
		return false;
	    }
	}

	return true;
    }
    
    class CriteriaCellFactory implements Callback<TableColumn<ObservableList<StringProperty>,String>, TableCell<ObservableList<StringProperty>, String>> {
	private final boolean cutoff;
	
	public CriteriaCellFactory(boolean cutoff) {
	    this.cutoff = cutoff;
	}
	@Override
	public TableCell<ObservableList<StringProperty>,String> call(TableColumn<ObservableList<StringProperty>,String> param) {
	    return new TextFieldTableCell<ObservableList<StringProperty>, String>(( new DefaultStringConverter() )) {
		@Override
		public void updateItem(String s, boolean b) {
		    super.updateItem(s, b);
		    TableRow row = getTableRow();
		    if (row != null && (row.getIndex()+1 == this.getTableView().getItems().size()) || (cutoff && row != null && row.getIndex() == 0)) {
			setDisable(true);
			setEditable(false);
			this.setStyle("-fx-text-fill: grey");
		    }
		}
	    };
	}
    }
        
    private void updateSampleMapping( String key, boolean add ) {
        
        mappingTable.getColumns().get(1).setVisible(false);
        if( add ) {
            if( !m_SampleHeaders.contains(key) ) {
                m_SampleHeaders.add(key);
                for( ComboBox cb : m_ComboBoxList ) {
                    cb.getItems().add(key);
                }
            }
        } else {
            
            for( ComboBox cb : m_ComboBoxList ) {
                int id = cb.getItems().indexOf(key);
                int selectedId = cb.getSelectionModel().getSelectedIndex();
                
                if( id == selectedId ) {
                    selectedId = 0;
                    cb.getSelectionModel().select( selectedId );
                } else if( id != -1 && id < selectedId ) {
                    selectedId--;
                }
                cb.getItems().remove(key);
                cb.getSelectionModel().select(selectedId);
            }
            
            m_SampleHeaders.remove( key );
        }
        
        mappingTable.getColumns().get(1).setVisible(true);
    }
    
    private String getPrefixMapping( String sample ) {
        String nameInFile = "";
        for( String header : m_SampleHeaders ) {
	    if (m_SampleHeaders.size() == 1) {
		return header;
	    }
            if( sample.startsWith(header) && header.length() > nameInFile.length() ) {
                nameInFile = header;
            }
        }
        if( nameInFile.length() == 0 ) {
            //set default name if no header matches.
            
            ////option 1
            ////default : NONE
            //nameInFile = STRING_NONE;
            
            ////option 2
            ////default: Any first item
            nameInFile = m_SampleHeaders.iterator().next();
        }
        return nameInFile;
    }
    
    public void initializeMappingTable() {
        if( m_LoadedSamples == null ) return;
        
        mappingTable.getItems().clear();
        m_ComboBoxList.clear();
        
        colLoadedSample.setCellValueFactory(
            new Callback<CellDataFeatures<SampleMap, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<SampleMap, String> p) {
                    return new SimpleStringProperty( p.getValue().getLoadedSample() );
                }
        });
                        
        colSampleInFile.setCellFactory( new Callback< TableColumn<SampleMap,String>, TableCell<SampleMap,String> >() {
            @Override
            public TableCell<SampleMap, String> call(TableColumn<SampleMap, String> p) {
                final TableCell<SampleMap,String> cell = new TableCell<>();
                
                cell.indexProperty().addListener( new ChangeListener<Number>() {

                    @Override
                    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                        final int cellIndex = t1.intValue();
                        ComboBox cbCurrent = (ComboBox)cell.getGraphic();
                        if( cbCurrent != null ) m_ComboBoxList.remove( cbCurrent );
                            
                        if( cellIndex == -1 || cellIndex >= mappingTable.getItems().size() ) {
                            cell.setGraphic(null);
                            return;
                        }
                        
                        final ObservableList<String> cbValues = FXCollections.observableArrayList(m_SampleHeaders);
                        cbValues.add(0, STRING_NONE );
                        
                        final ComboBox cb = new ComboBox(cbValues);
                
                        cb.setValue((String) mappingTable.getItems().get(cellIndex).getNameInFile());
                        cb.setUserData(cellIndex);
                        cb.setOnAction( new EventHandler() {

                            @Override
                            public void handle(Event t) {
                                mappingTable.getItems().get(cellIndex).setNameInFile( (String) cb.getValue() );
                            }
                        }) ;
                        
                        m_ComboBoxList.add(cb);
                        cell.setGraphic(cb);
                        
                        
                    }
                });
                
                return cell;
            }
            
        });
                
        mappingTable.getItems().clear();
        for( String sample : m_LoadedSamples ) {
            String nameInFile = getPrefixMapping( sample );
            mappingTable.getItems().add( new SampleMap( sample, nameInFile ));
        }
    }
    
    public HashMap<String, ArrayList<String>> getMap() {
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        for( SampleMap smap : mappingTable.getItems() ) {
            
            if( smap.getNameInFile().equals( STRING_NONE )) continue;
            ArrayList<String> list = hashMap.get(smap.getNameInFile());
            if( list == null ) {
                list = new ArrayList<>();
                list.add( smap.getLoadedSample() );
                hashMap.put( smap.getNameInFile(), list );
            } else {
                list.add( smap.getLoadedSample() );
            }
        }
        return hashMap;
    }
}
