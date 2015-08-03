/*
 *  Saves graph to PNG or PDF
 */
package Controls;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Rectangle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;
import tagviz.TagViz;

public class SaveControl extends Controller {

    static File dir = null;
    final WritableImage image;

    public static enum FORMAT {
        PDF, PNG, TABLE, EPS
    }

    public SaveControl(WritableImage image) {
        this.image = image;
    }

    public File Save(FORMAT format) {

        FileChooser fileChooser = new FileChooser();
        if (dir == null) dir = TagViz.visDirectory;
	fileChooser.setInitialDirectory(dir);
        if (format.equals(FORMAT.PNG)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        } else if (format.equals(FORMAT.PDF)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        } else if (format.equals(FORMAT.TABLE)){
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
        } else if (format.equals(FORMAT.EPS)){
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EPS", "*.eps"));
        }
        fileChooser.setTitle("Save Graph");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
	    dir = file.getParentFile();
            if      (format.equals(FORMAT.PNG)) SaveImage(file);
            else if (format.equals(FORMAT.PDF)) SavePDF(file);
            else if (format.equals(FORMAT.EPS)) SaveEPS(file);
        }
	
	return file;
    }

    public void SaveImage(File file) {

        if (!file.getName().endsWith(".png")) {
            file = new File(file.getAbsolutePath() + ".png");
        }
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException ex) {
            // could not write to file
        }
    }

    public void SavePDF(File file) {
        try {
            String filename = file.getAbsolutePath().replace("\\", "\\\\");
            filename += !filename.endsWith(".pdf") ? ".pdf" : "";

            final String RESOURCE = "tmp.png";
            File tmp_img = new File(RESOURCE);
            SaveImage(tmp_img);

            Image img = Image.getInstance(RESOURCE);
            /*Document document = new Document(img.getWidth() < img.getHeight()
                    ? PageSize.LETTER : PageSize.LETTER.rotate(), 20, 20, 20, 20);*/
            Document document = new Document( new Rectangle(0,0,(int)img.getWidth(),(int)img.getHeight()) );
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));

            document.open();
            float pgw = document.getPageSize().getWidth() - 40;
            float pgh = document.getPageSize().getHeight() - 40;
            if (img.getWidth() > pgw || img.getHeight() > pgh) {
                img.scaleToFit(pgw, pgh);
            }
            document.add(img);
            document.close();
            tmp_img.delete();

        } catch (BadElementException ex) {
            Logger.getLogger(SaveControl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException | IOException ex) {
            Logger.getLogger(SaveControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void SaveEPS(File file) {
        
        if (!file.getName().endsWith(".eps")) {
            file = new File(file.getAbsolutePath() + ".eps");
        }

        BufferedImage bufImage = SwingFXUtils.fromFXImage(image, null);
        Graphics2D g = new EpsGraphics2D();
        g.drawImage(bufImage, 0, 0, null);
        String epsString = g.toString();
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(epsString);
	} catch (IOException ex) {
	}
    }
}
