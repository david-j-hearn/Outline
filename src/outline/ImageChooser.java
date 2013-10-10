package outline;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

/*
 * FileChooserDemo2.java requires these files:
 *   ImageFileView.java
 *   ImageFilter.java
 *   ImagePreview.java
 *   Utils.java
 *   images/jpgIcon.gif (required by ImageFileView.java)
 *   images/gifIcon.gif (required by ImageFileView.java)
 *   images/tiffIcon.gif (required by ImageFileView.java)
 *   images/pngIcon.png (required by ImageFileView.java)
 */
public class ImageChooser extends JPanel
        implements ActionListener {
    static private String newline = "\n";
    private outlineLogger ol;
    private outlineMain om;
    private JFileChooser fc;
    public File file;
    
    public ImageChooser(outlineMain oM, outlineLogger oL) {
        super(new BorderLayout());
        
        //Create the log first, because the action listener
        //needs to refer to it.
        ol = oL;
        om = oM;
        
        //Set up the file chooser.
        if (fc == null) {
            fc = new JFileChooser();
            
            //Add a custom file filter and disable the default
            //(Accept All) file filter.
            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);
            
            //Add custom icons for file types.
            fc.setFileView(new ImageFileView());
            
            //Add the preview pane.
            fc.setAccessory(new ImagePreview(fc));
        }
        
        //Show it.
        int returnVal = fc.showDialog(ImageChooser.this,
                "Open");
        
        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            om.cif = file;
            om.openImage(file);
        } else {
            ol.loggerTextArea.append("Opening file cancelled by user." + newline);
        }
        //Reset the file chooser for the next time it's shown.
        fc.setSelectedFile(null);
        fc.setVisible(true);
        //Create and set up the window.
        //JFrame frame = new JFrame("ImageSelector");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add content to the window.
        //frame.add(this);
        
        //Display the window.
        //frame.pack();
        //frame.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
    }
    
    
    
     
    
    
    
}
