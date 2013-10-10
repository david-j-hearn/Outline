/*
 * ImageInfo.java
 *
 * Created on September 6, 2007, 8:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package outline;

/**
 *
 * @author projects
 */
/*

!!!!!For thresholding, you might consider using the int pixel value (see under renderImage()) rather than 
breaking into rgb values first.


(1) decoding int rgba = ...; BufferedImage.getRGB etc. 
int red = (rgba >> 16) & 0xff; 
int green = (rgba >> 8) & 0xff; 
int blue = rgba & 0xff; 
int alpha = (rgba >> 24) & 0xff; 
(2) now modify red, green, blue and alpha as you like; 
make sure that each of the four values stays in the interval 0 to 255 ... 
(3) and encode back to an int, e.g. to give it to 
BufferedImage.setRGB rgba = (alpha << 24) | (red << 16) | (green << 8) | blue;

*/

import java.lang.*;
import java.io.*;
import java.sql.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;
import javax.imageio.stream.*;
import java.util.*;



public class ImageInfo
{
int i;


public ImageInfo()
	throws Exception
	{
	}


public void init()
	throws Exception
	{
	} 

public static int imageWidth(InputStream in)
{
    try {
            BufferedImage bi = ImageIO.read(in);
            
            return ImageInfo.imageWidth(bi);
            
        } catch(Exception e) {}
    return -1;
}

public static int imageWidth(String file)
	{
	File f = new File(file);
	try
		{
		BufferedImage bi = ImageIO.read(f);
		return bi.getWidth();
		}
	catch(Exception e){}
	return -1;
	}

public static int imageWidth(BufferedImage image)
	{
	try { return image.getWidth(); }
	catch(Exception e){}
	return -1;
	}

public static int imageHeight(InputStream in)
{
    try {
            BufferedImage bi = ImageIO.read(in);
            
            return ImageInfo.imageHeight(bi);
            
        } catch(Exception e) {}
    return -1;
}


public static int imageHeight(BufferedImage image)
	{
	try { return image.getHeight(); }
	catch(Exception e){}
	return -1;
	}

public static int imageHeight(String file)
	{
	try
		{
		File f = new File(file);
		BufferedImage bi = ImageIO.read(f);
		return bi.getHeight();
		}
	catch(Exception e){}
	return -1;
	}

public static int[][][] readImage(BufferedImage bi, int x, int y, int h, int w) 
	{  
	int[][][] Pix = new int[3][w][h];
	try{
      for(int i = 0; i<w; i++)
			for(int j = 0; j<h; j++)	
         {
			int rgba = bi.getRGB(x+i, y+j) ;

         Pix[0][i][j] = (rgba >> 16) & 0xff;
         Pix[1][i][j] = (rgba >> 8) & 0xff;
         Pix[2][i][j] = (rgba) & 0xff;
			//System.out.println(Pix[0][i][j]+","+Pix[1][i][j]+","+Pix[2][i][j]);
         }
		//System.out.println("Done Extracting pixels");	

			return Pix;	
		}
	catch(Exception e){}//System.out.println("Catching error: " + e.getMessage()); e.printStackTrace();}
	return null;
	}
public static int[][] grayScale(int pix[][][])
	{
	return grayScale(pix, .3,.59,.11);
	}

public static int[][] grayScale(int pix[][][], double r, double g, double b)
	{
	double tot = r+g+b;
	r/=tot;
	g/=tot;
	b/=tot;
	int pix1[][] = new int[pix[0].length][pix[0][0].length];
	for(int i=0;i<pix[0].length;i++)
		for(int j=0;j<pix[0][0].length;j++)
			pix1[i][j] = (int)((double)pix[0][i][j]*r + (double)pix[1][i][j]*g + (double)pix[2][i][j]*b);
	return pix1;
	}
}
 
