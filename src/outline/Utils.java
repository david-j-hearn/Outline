package outline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.ImageIcon;
import java.util.*;


/* Utils.java is used by FileChooserDemo2.java. */
public class Utils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String odb = "odb";
    public final static String otl = "otl";
    
    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = new String();
        String s = f.getName();
        int i = s.lastIndexOf('.');
        
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public static int getLineCount(BufferedReader in) {
        int tot=0;
        try{
            tot=0;
            String l = new String();
            while(l!=null) {
                l = in.readLine();
                tot++;
            }
            in.close();
        } catch(Exception e) {e.printStackTrace(); }
        return tot;
    }
    
    
    public static int getLineCount(String file) {
        int tot=0;
        try{
            BufferedReader in = new BufferedReader(new FileReader(file));
            tot=0;
            String l = new String();
            while(l!=null) {
                l = in.readLine();
                tot++;
            }
            in.close();
        } catch(Exception e) {e.printStackTrace(); }
        return tot;
    }
    
    public static String getDate() {
        Calendar cDate = Calendar.getInstance();
        String[] months = {"jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
        String date = cDate.get(Calendar.DATE) + "-" + months[cDate.get(Calendar.MONTH)] + "-" + cDate.get(Calendar.YEAR);
        return date;
    }
    
    public String getGif() {
        return gif;
    }
    
    public static double getAverage(String tabString) {
        //System.out.println(tabString);
        String[] temp = tabString.split(" ");
        double tot = 0;
        for(int i=0; i<temp.length; i++) {
            tot += Double.parseDouble(temp[i]);
        }
        return tot/temp.length;
    }
    
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
