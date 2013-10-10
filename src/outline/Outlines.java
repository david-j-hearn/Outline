/*
 * Outlines.java
 *
 * Created on September 6, 2007, 8:46 PM
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



public class Outlines {
    int i;
    
    
    public Outlines()
    throws Exception {
        this.init();
    }
    
    
    public void init()
    throws Exception {
    }
    
//format is jpeg or tiff
    public static BufferedImage renderOutlineImage(Polygon[] shapes, int width,int height, boolean[] keep) {
        BufferedImage bi=null;
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            
            // Create an image that supports transparent pixels
            bi = gc.createCompatibleImage(width, height, Transparency.OPAQUE);
            if(bi ==null) {
                return null;
            }
            int size = shapes.length;
            for(int i=0; i<size; i++) if(keep[i]) {
                for(int j=0; j<shapes[i].npoints; j++) {
//hexadecimal representation of pixel integer value using DirectColorModel
//0xff000000 	alpha multiplier
//0x00ff0000	red value (8bit)
//0x0000ff00	green value (8 bit)
//0x000000ff	blue value (8 bit)
                    if(shapes[i].xpoints[j]>0 && shapes[i].xpoints[j]<width && shapes[i].ypoints[j]>0 && shapes[i].ypoints[j]<height)
                        bi.setRGB(shapes[i].xpoints[j], shapes[i].ypoints[j], 0xFF00FF00);
                }
            }
            
            Graphics g = bi.getGraphics();
            g.setColor(Color.red);
            for(int i=0; i<size; i++) if(keep[i]) {
                Point2D.Double cp = findCentroid(extractCoordinates((Polygon)shapes[i]));
                g.drawString(Integer.toString(i+1),(int)cp.x, (int)cp.y);
            }
            g.dispose();
            
            
        } catch(Exception e){e.printStackTrace(); return null;}
        return bi;
    }
    
    public static boolean renderBufferedImage(BufferedImage bi, String format, String outFileName) {
        try{
            Iterator writers = ImageIO.getImageWritersByFormatName(format);
            if(!writers.hasNext()) {
                //System.out.println("There were no image writers associated with the format " + format);
                String[] formats = ImageIO.getWriterFormatNames();
                //for(int i=0; i<formats.length; i++)
                //System.out.println(formats[i]);
                
            }
            ImageWriter writer = (ImageWriter)writers.next();
            File f1 = new File(outFileName);
            ImageOutputStream ios = ImageIO.createImageOutputStream(f1);
            writer.setOutput(ios);
            writer.write(bi);
            ios.close();
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static int[][] readImage(String file) {
        try {
            File f = new File(file);
            return Outlines.readImage(f);
        } catch(Exception e){}
        return null;
    }
    
    public static int[][] readImage(File f) {
        int[][] greyPix = null;
        String file = null;
        try{
            file = f.getName();
            //System.out.println("Opening " + file);
            boolean pgm=false;
            if(file.substring(file.indexOf('.')+1).compareTo("pgm")==0)
                pgm=true;
            if(!pgm) {
                BufferedImage bi = ImageIO.read(f);
                boolean jpg = false;
                boolean gif = false;
                //System.out.println("The image format type is " + bi.getType());
                if(bi.getType()==5 || bi.getType()==10)
                    jpg=true;
                else if(bi.getType()==13)
                    gif=true;
                else {
                    //System.out.println("Please end your image file name with an appropriate .format extension.");
                    //System.out.println("Current formats include jpeg (.jpg/.jpeg) and gif (.gif).");
                    return null;
                }
                
                ColorModel cm = bi.getColorModel();
                
                int width = bi.getWidth();
                int height = bi.getHeight();
                int x0 = bi.getMinX();
                int y0 = bi.getMinY();
                
                int[] red = new int[(width) * (height)];
                int[] green = new int[(width) * (height)];
                int[] blue = new int[(width) * (height)];
                greyPix = new int[width][height];
                int sample = 0;
                
                DataBuffer db = bi.getData().getDataBuffer();
                if(jpg) {
                    red = bi.getSampleModel().getSamples(x0,y0,width,height,0,red,db); //bi.getData().getDataBuffer());
                    green = bi.getSampleModel().getSamples(x0,y0,width,height,1,green,db); //bi.getData().getDataBuffer());
                    blue = bi.getSampleModel().getSamples(x0,y0,width,height,2,blue,db); //bi.getData().getDataBuffer());
                    for(int i = 0; i<(width) * (height); i++) {
                        int x = (int)(i % (width));
                        int y = (int)(i / (width));
                        greyPix[x][y] = (red[i] +  green[i] +  blue[i])/3;
                    }
                    return greyPix;
                } else if(gif)
                    for(int i = 0; i<width; i++)
                        for(int j=0; j<height; j++) {
                    sample = bi.getSampleModel().getSample(i,j,0,db);
                    greyPix[i][j]=(cm.getRed(sample)+cm.getBlue(sample)+cm.getGreen(sample))/3;
                        }
                return greyPix;
            } else {
                //System.out.println("This is a portable grey map image, .pgm.");
                BufferedReader in = new BufferedReader(new FileReader(file));
                boolean magic_n=false, comments=false, dim=false;
                int[] pix = new int[1];
                int w=0, h=0, cntr=0;
                String line = " ";
                while(line != null) {
                    
                    line = in.readLine();
                    if(line==null)
                        break;
                    line = line.trim();
                    if(line.matches("P2"))
                        magic_n = true;
                    else if(line.substring(0,1).compareTo("#")==0)
                        comments = true;
                    else if(magic_n && !dim) {
                        w = Integer.parseInt(line.substring(0,line.indexOf(' ')));
                        h = Integer.parseInt(line.substring(line.indexOf(' ') +1));
                        greyPix = new int[w][h];
                        pix = new int[w*h];
                        dim = true;
                    } else if(dim) {
                        while(line.length()>=0) {
                            if(line.indexOf(' ') >= 0) {
                                pix[cntr]=Integer.parseInt(line.substring(0,line.indexOf(' ')));
                                cntr++;
                                line = line.substring(line.indexOf(' ')+1);
                            } else if(cntr<=w*h)
                                pix[cntr]=Integer.parseInt(line);
                            else
                                break;
                        }
                    } else {
                        //System.out.println(file + " does not appear to be a valid .pgm file.");
                        return null;
                    }
                }
                for(int i=0; i<w*h;i++) {
                    int x = (int)(i % w);
                    int y = (int)(i / w);
                    greyPix[x][y] = pix[i];
                }
                return greyPix;
            }
        } catch(Exception e){}//System.out.println("Catching error: " + e.getMessage());}
        return null;
    }
    
    public static boolean isMargin(int i, int j, int[][] imagePix, int threshold) {
        boolean test = false;
        if(i<=0 || j<=0 || i>=imagePix.length || j>=imagePix[0].length)
            return false;
        if(imagePix[i][j]<threshold) {
            if(imagePix[i-1][j]<threshold)
                if(imagePix[i-1][j-1]<threshold || imagePix[i-1][j+1]<threshold)
                    test=true;
            
            if(imagePix[i][j+1]<threshold)
                if(imagePix[i-1][j+1]<threshold || imagePix[i+1][j+1]<threshold)
                    test=true;
            
            
            if(imagePix[i+1][j]<threshold)
                if(imagePix[i+1][j-1]<threshold || imagePix[i+1][j+1]<threshold)
                    test=true;
            
            if(imagePix[i][j-1]<threshold)
                if(imagePix[i-1][j-1]<threshold || imagePix[i+1][j-1]<threshold)
                    test=true;
        }
        if(imagePix[i-1][j-1]<threshold && imagePix[i-1][j]<threshold && imagePix[i-1][j+1]<threshold && imagePix[i][j-1]<threshold && imagePix[i][j]<threshold && imagePix[i][j+1]<threshold && imagePix[i+1][j-1]<threshold && imagePix[i+1][j]<threshold && imagePix[i+1][j+1]<threshold)
            test=false;
        return test;
    }
    
    public static boolean pointNearRegistered(int x, int y, int W, boolean[][] registered) {
        for(int i=-W;i<=W;i++)
            for(int j=-W;j<=W;j++)
                if(x+i>=0 && y+j>=0)
                    if(x+1<registered.length && y+j<registered[0].length)
                        if(registered[x+i][y+j])
                            return true;
        return false;
    }
    
    public static boolean isApex(int i, int j, int threshold, boolean marked, int[][] imagePix) {
        if(i==0 || j==0 || i>=imagePix.length || j>=imagePix[0].length)
            return false;
        if(marked)
            if(imagePix[i][j]==0 && imagePix[i][j-1]==0 && imagePix[i][j+1]==0 && imagePix[i-1][j]==0 && imagePix[i+1][j] ==0)
                return true;
        if(!marked)
            if(imagePix[i-1][j]>threshold && imagePix[i][j]<threshold && imagePix[i][j-1]>threshold && imagePix[i-1][j-1]>threshold && imagePix[i+1][j-1]>threshold )
                if(imagePix[i][j+1]<threshold || imagePix[i-1][j+1]<threshold || imagePix[i+1][j+1]<threshold)
                    return true;
        return false;
    }
    
    public static Polygon[] findCoordinates(InputStream in) {
        try {
            BufferedImage bi = ImageIO.read(in);
            int height = ImageInfo.imageHeight(bi);
            int width = ImageInfo.imageWidth(bi);
            return Outlines.findCoordinates(ImageInfo.grayScale(ImageInfo.readImage(bi, 0, 0, height, width)), 220, 5, false);
        } catch(Exception e) {}
        return null;
    }
    
    public static Polygon[] findCoordinates(File image) {
        try {
            BufferedImage bi = ImageIO.read(image);
            int height = ImageInfo.imageHeight(bi);
            int width = ImageInfo.imageWidth(bi);
            return Outlines.findCoordinates(ImageInfo.grayScale(ImageInfo.readImage(bi, 0, 0, height, width)), 220, 5, false);
        } catch(Exception e) {}
        return null;
    }
    
    public static Polygon[] findCoordinates(String image) {
        try {
            File f = new File(image);
            BufferedImage bi = ImageIO.read(f);
            int height = ImageInfo.imageHeight(bi);
            int width = ImageInfo.imageWidth(bi);
            return Outlines.findCoordinates(ImageInfo.grayScale(ImageInfo.readImage(bi, 0, 0, height, width)), 220, 5, false);
        } catch(Exception e) {}
        return null;
    }
    
//method returns a linked list of Polygons representing the shapes found in the image.
    public static Polygon[] findCoordinates(int[][] imagePix,int threshold,int spacing,boolean marked) {
        //imagePix is matrix of grwyscaled 8-bit pixel values.
        //threshold is boundary pixel intensity for edge detection.
        //spacing is minimum distance in pixels between two objects in image. It should be greater than 5.
        //marked is true if apex (point where midvein exits leaf at top) is marked with a black '+'.
        //registry is a boolean maxtrix of dim[imagePix] indicating whether element i,j is in a polygon.
        
        LinkedList coords = new LinkedList();
        boolean[][] registry = new boolean[imagePix.length][imagePix[0].length];
        for(int i=0;i<imagePix.length;i++)
            for(int j=0; j<imagePix[0].length;j++)
                registry[i][j]=false;
        for(int j=0;j<imagePix[0].length;j++)
            for(int i=0;i<imagePix.length;i++)
                if(Outlines.isApex(i,j,threshold,marked,imagePix))
                    if(Outlines.isMargin(i,j,imagePix,threshold))
                        if(!Outlines.pointNearRegistered(i,j,spacing,registry)) {
            //System.out.println("finding coordinates at " + i + "," + j);
            coords.add(Outlines.findOutline(new Point(i,j),imagePix,registry,threshold));
                        }
        Polygon[] out = new Polygon[coords.size()];
        for(int i =0; i<coords.size(); i++)
            out[i] = (Polygon)coords.get(i);
        return out;
    }
    
//traces the points of a single outline starting at point p.
    public static Polygon findOutline(Point p, int[][] imagePix, boolean[][] registry, int threshold) {
        int W = 0, passes =0, state=1, t,i;
        Polygon o = new Polygon() ;
        Point top = new Point(p.x,p.y), test = new Point(0,0);
        boolean yes = false;
        o.addPoint(p.x,p.y);
        while(W<=5) {
            while(passes <= 4) {
                yes=false;
                if(state ==1) {
                    for(t=0; t<=W-1; t++) {
                        for(i=0; i<=W-1; i++) {
                            if(!yes) {
                                test.x=p.x-i;
                                test.y=p.y+t-1;
                                if(test.x>0 && test.y>0)
                                    if(Outlines.isMargin(test.x,test.y,imagePix,threshold) && !registry[test.x][test.y]) {
                                    p.x=test.x;
                                    p.y=test.y;
                                    o.addPoint(p.x,p.y);
                                    registry[p.x][p.y]=true;
                                    passes=0;
                                    yes=true;
                                    W=1;
                                    }
                            }
                        }
                    }
                    if(!yes) {
                        state = 2;
                        passes++;
                    }
                } 	//end State 1
                if(state ==2) {
                    for(i=0; i<=W-1; i++) {
                        for(t=0; t<=W-1; t++) {
                            if(!yes) {
                                test.x=p.x+i-1;
                                test.y=p.y+t;
                                if(test.x>0 && test.y>0)
                                    if(Outlines.isMargin(test.x,test.y,imagePix,threshold) && !registry[test.x][test.y]) {
                                    p.x=test.x;
                                    p.y=test.y;
                                    o.addPoint(p.x,p.y);
                                    registry[p.x][p.y]=true;
                                    passes=0;
                                    yes=true;
                                    W=1;
                                    }
                            }
                        }
                    }
                    if(!yes) {
                        state = 3;
                        passes++;
                    }
                } 	//end State 2
                if(state==3) {
                    for(t=0; t<=W-1; t++) {
                        for(i=0; i<=W-1; i++) {
                            if(!yes) {
                                test.x=p.x+i;
                                test.y=p.y-t+1;
                                if(test.x>0 && test.y>0)
                                    if(Outlines.isMargin(test.x,test.y,imagePix,threshold) && !registry[test.x][test.y]) {
                                    p.x=test.x;
                                    p.y=test.y;
                                    o.addPoint(p.x,p.y);
                                    registry[p.x][p.y]=true;
                                    passes=0;
                                    yes=true;
                                    W=1;
                                    }
                            }
                        }
                    }
                    if(!yes) {
                        state = 4;
                        passes++;
                    }
                }	//end State 3
                if(state==4) {
                    for(i=0; i<=W-1; i++) {
                        for(t=0; t<=W-1; t++) {
                            if(!yes) {
                                test.x=p.x+i-1;
                                test.y=p.y-t;
                                if(test.x>0 && test.y>0)
                                    if(Outlines.isMargin(test.x,test.y,imagePix,threshold) && !registry[test.x][test.y]) {
                                    p.x=test.x;
                                    p.y=test.y;
                                    o.addPoint(p.x,p.y);
                                    registry[p.x][p.y]=true;
                                    passes=0;
                                    yes=true;
                                    W=1;
                                    }
                            }
                        }
                    }
                    if(!yes) {
                        state = 1;
                        passes++;
                    }
                }	//end State 4
                if(Math.abs(p.x-top.x)<=2 && Math.abs(p.y-top.y)<=2 && o.npoints >= 10) {
                    passes=5;
                    W=5;
                }
            }	//end passes loop
            passes=0;
            W++;
        }		//end W loop
        return o;
    }
    
    public static Polygon getPolygon(double[][] coordinates) {
        if(coordinates==null) return null;
        int[][] coords = new int[coordinates.length][coordinates[0].length];
        for(int i=0; i<coordinates.length; i++)
            for(int j=0;j<coordinates[0].length;j++)
                coords[i][j] = (int)coordinates[i][j];
        return new Polygon(coords[0], coords[1], coords[0].length);
    }
    
    public static Polygon getPolygon(int[][] coords) {
        return new Polygon(coords[0], coords[1], coords[0].length);
    }
//end method
    
    public static double[][] scalePoints(double centroidDistance, double[][] coordinates) {
        int npoints = coordinates[0].length;
        double[][] temp = new double[2][npoints];
        System.arraycopy(coordinates[0],0,temp[0],0,npoints);
        System.arraycopy(coordinates[1],0,temp[1],0,npoints);
        for(int i=0; i<npoints; i++) {
            temp[0][i] = coordinates[0][i] / centroidDistance;
            temp[1][i] = coordinates[1][i] / centroidDistance;
        }
        return temp;
    }
    
    //returns the coordinates of the lower right corner of all the polygons
    public static Point getPolygonsBox(Polygon[] p) {
        
        int maxX = 0;
        int maxY = 0;
        if(p==null) return null;
        for(int i=0; i<p.length;i++) {
            for(int j=0; j<p[i].npoints; j++) {
                
                int x = p[i].xpoints[j];
                int y = p[i].ypoints[j];
                if(x>maxX) maxX=x;
                if(y>maxY) maxY=y;
            }
        }
        return new Point(maxX,maxY);
    }
    
    
    
    public static int findArea(Polygon shape) {
        Rectangle r = shape.getBounds();
        int area = 0;
        for(int i=r.x;i<=r.x+r.width;i++)
            for(int j=r.y;j<r.y+r.height;j++)
                if(shape.contains(i,j))
                    area++;
        return area;
    }
    
    public static double[][] extractCoordinates(Polygon p) {
        if(p==null) return null;
        double[][] coordinates = new double[2][p.npoints];
        for(int i=0; i<p.npoints; i++) {
            coordinates[0][i]=(double)p.xpoints[i];
            coordinates[1][i]=(double)p.ypoints[i];
        }
        return coordinates;
    }
    
    public static double[][][] extractCoordinates(Polygon[] p, int nPoints, boolean[] keep) {
        int kn = 0;
        for(int i=0; i<keep.length; i++)
            if(keep[i]) kn++;
        
        if(p==null) return null;
        double[][][] coordinates = new double[kn][2][nPoints];
        
        int cnt=0;
        for(int j=0; j<p.length; j++) if(keep[j]) {
            int nPts = p[j].npoints;
            float interval = (float)nPts / (float)nPoints;
            for(int i=0; i<nPoints;i++) {
                //System.out.println("tot " + nPts + " new " + nPoints + " cur ind " + (Math.max(0,Math.round(interval*(float)i)-1)));
                coordinates[cnt][0][i] = p[j].xpoints[Math.max(0,Math.round(interval*(float)i)-1)];
                coordinates[cnt][1][i] = p[j].ypoints[Math.max(0,Math.round(interval*(float)i)-1)];
            }
            cnt++;
        }
        return coordinates;
    }
    
    public static double findCentroidDistance(double[][] coordinates) {
        Point2D.Double pd = new Point2D.Double();
        pd = Outlines.findCentroid(coordinates);
        int npoints = coordinates[0].length;
        double dist=0.0;
        for(int i = 0; i<npoints; i++)
            dist += Math.sqrt((coordinates[0][i]-pd.x)*(coordinates[0][i]-pd.x)+(coordinates[1][i]-pd.y)*(coordinates[1][i]-pd.y));
        dist = dist/(double)npoints;
        return dist;
    }
    
    
    public static Point2D.Double findCentroid(double coordinates[][]) {
        int npoints = coordinates[0].length;
        double xave = 0, yave = 0;
        
        for(int i=0; i<npoints ;i++) {
            xave += coordinates[0][i];
            yave += coordinates[1][i];
        }
        return (new Point2D.Double(xave / (double)npoints, yave / (double)npoints));
    }
    
    public static double[][] translateCoordinates(double coordinates[][], double x, double y) {
        int npoints = coordinates[0].length;
        double[][] temp= new double[2][npoints];
        System.arraycopy(coordinates[0],0,temp[0],0,npoints);
        System.arraycopy(coordinates[1],0,temp[1],0,npoints);
        for(int i = 0; i<npoints;i++) {
            temp[0][i]=coordinates[0][i]+x;
            temp[1][i]=coordinates[1][i]+y;
        }
        return temp;
    }
    
    public static double[][] translateCoordinates(double[][] coordinates, Point2D.Double p) {
        //System.out.println("The point is " + (int)p.x + "," + (int)p.y);
        //double[][] temp = Outlines.translateCoordinates(coordinates, -1*p.x, -1*p.y);
        //System.out.println("The point is now " + (int)Outlines.findCentroid(temp).x + "," + (int)Outlines.findCentroid(temp).y);
        return Outlines.translateCoordinates(coordinates, -1*p.x, -1*p.y);
    }
    
    public static double[][] samplePoints(double coordinates[][], int nPoints) {
        double[][] out = new double[2][nPoints];
        int nPts = coordinates[0].length;
        float interval = (float)nPts / (float)nPoints;
        for(int i=0; i<nPoints;i++) {
            out[0][i] = coordinates[0][Math.max(0,Math.round(interval*(float)i)-1)];
            out[1][i] = coordinates[1][Math.max(0,Math.round(interval*(float)i)-1)];
        }
        return out;
    }
    
//Requires input coordinates to be in a periodic or radial function
    public static double[][] getHarmonics(double[][] radialFunction, int harmnum) {
        int margnum = radialFunction[0].length;
        double[] a = new double[harmnum];
        double[] b = new double[harmnum];
        double[][] out = new double[2][harmnum];
        double lint = 0, hint = 0;
        for(int z=0; z<=harmnum-1; z++) {
            a[z]=0.0;
            b[z]=0.0;
            for(int t=0; t<=margnum-1; t++) {
                if(t==0) {
                    lint = -Math.PI;
                    hint = 2*Math.PI*(radialFunction[0][t]+.5*(radialFunction[0][t+1]-radialFunction[0][t]-radialFunction[0][0]))/radialFunction[0][margnum-1] - Math.PI;
                } else if(t!=margnum-1) {
                    lint = 2*Math.PI*(radialFunction[0][t]+.5*(radialFunction[0][t-1]-radialFunction[0][t]-radialFunction[0][0]))/radialFunction[0][margnum-1] - Math.PI;
                    hint = 2*Math.PI*(radialFunction[0][t]+.5*(radialFunction[0][t+1]-radialFunction[0][t]-radialFunction[0][0]))/radialFunction[0][margnum-1] - Math.PI;
                    
                }
                if(t==margnum-1) {
                    lint = 2*Math.PI*(radialFunction[0][t]+.5*(radialFunction[0][t-1]-radialFunction[0][t]-radialFunction[0][0]))/radialFunction[0][margnum-1] - Math.PI;
                    hint = Math.PI;
                }
                
                //System.out.println("The lower integrand is " + Double.toString(lint) + " and the upper integrand is " + Double.toString(hint));
                
                
                if(z==0) {
                    a[z]+=(1/Math.PI)*radialFunction[1][t]*(hint-lint);
                }
                if(z>0) {
                    a[z]+=(1/Math.PI)*(1/(double)z)*radialFunction[1][t]*(Math.sin((double)z*hint)-Math.sin((double)z*lint));
                    b[z]+=(1/Math.PI)*(1/(double)z)*radialFunction[1][t]*(Math.cos((double)z*lint)-Math.cos((double)z*hint));
                }
                
                
            }
            //System.out.println("The harmonics are a" + z + ": " + Double.toString(a[z]) + " and b" + z + ": " + Double.toString(b[z]));
        }
        out[0] = a;
        out[1] = b;
        return out;
    }
    
    public static Polygon toPolygon(double[][] coordinates, double scale) {
        if(coordinates==null) return null;
        
        int[] xpoints = new int[coordinates[0].length];
        int[] ypoints = new int[coordinates[0].length];
        for(int i=0; i<coordinates[0].length; i++) {
            xpoints[i] = (int)Math.round(coordinates[0][i]*scale);
            ypoints[i] = (int)Math.round(coordinates[1][i]*scale);
        }
        return new Polygon(xpoints, ypoints, coordinates[0].length);
    }
    
//optimally superimposes coords1 onto coords2
//Assumes coordinates already translated!!!
    public static double[][] superimposeCoordinates(double[][] coords1, double[][] coords2){
        try {
            //Point2D.Double c1 = Outlines.findCentroid(coords1);
            //Point2D.Double c2 = Outlines.findCentroid(coords2);
            //Outlines.translateCoordinates(coords1, -1*c1.x, -1*c1.y);
            //Outlines.translateCoordinates(coords2, -1*c2.x, -1*c2.y);
            coords2=Outlines.optimallyRotate(coords1, coords2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return coords2;
    }
    
    public static double[][] calculateAverageCoordinates(OutlinesList outlines, int nPoints) throws Exception {
        
        double[][] aveCoords = new double[2][nPoints];
        while(outlines.previous!=null)
            outlines=outlines.previous;
        
        if(outlines.next==null) {
            System.arraycopy(outlines.coordinatesSST[0],0,aveCoords[0],0,nPoints);
            System.arraycopy(outlines.coordinatesSST[1],0,aveCoords[1],0,nPoints);
            return aveCoords;
        }
        //set the leaf against which all others will be superimposed
        int cnt =0;
        while(outlines.next!=null) {
            
            if(outlines.coordinatesSST==null) throw new Exception("Coordinates should be sampled, scaled, and translated before being superimposed");
            for(int i=0;i<nPoints;i++) {
                if(cnt==0) {
                    aveCoords[0][i]=outlines.coordinatesSST[0][i];
                    aveCoords[1][i]=outlines.coordinatesSST[1][i];
                } else {
                    aveCoords[0][i]+=outlines.coordinatesSST[0][i];
                    aveCoords[1][i]+=outlines.coordinatesSST[1][i];
                    
                }
            }
            cnt++;
            outlines = outlines.next;
        }
        
        cnt++;
        //System.out.println(outlines.taxon);
        if(outlines.coordinatesSST==null) throw new Exception("Coordinates should be sampled, scaled, and translated before being superimposed");
        for(int i=0;i<nPoints;i++) {
            aveCoords[0][i]+=outlines.coordinatesSST[0][i];
            aveCoords[1][i]+=outlines.coordinatesSST[1][i];
            aveCoords[0][i]/=cnt;
            aveCoords[1][i]/=cnt;
            //System.out.println(aveCoords[0][i] + "\t" + aveCoords[1][i]);
        }
        return aveCoords;
    }
    
//assumes already superimposed coordinates if that is what is desired...
    public static double[][] calculateAverageCoordinates(double[][][] coordinates) {
        int margnum = coordinates[0][0].length;
        int numObs = coordinates.length;
        double[][] aveCoords = new double[2][margnum];
        for(int i=0;i<numObs;i++) {
            for(int t=0; t<margnum; t++) {
                if(i==0 && t==0) {
                    aveCoords[0][t]=coordinates[i][0][t];
                    aveCoords[1][t]=coordinates[i][1][t];
                } else {
                    aveCoords[0][t]+=coordinates[i][0][t];
                    aveCoords[1][t]+=coordinates[i][1][t];
                }
            }
        }
        for(int t=0; t<margnum; t++) {
            aveCoords[0][t] /= (double)margnum;
            aveCoords[1][t] /= (double)margnum;
        }
        return aveCoords;
    }
    
    
//convergence is a number [0,1] that tells how close the superimposition is to optimality.
//Closer to 1 is faster, lower accuracy
    
    //for now, simply superimposes over first leaf
    public static double[][][] superimposeCoordinates(double[][][] coordinates, double convergence) {
        int numObs = coordinates.length;
        int margnum = coordinates[0][0].length;
        double[][] ave1 = new double[2][margnum];
        //double[][] ave2 = new double[2][margnum];
        //int cntr=0;
        //double conv = 500,disto=1,distn=0;
        //if(conv<convergence)
        //return false;
        System.arraycopy(coordinates[0][0],0,ave1[0],0,margnum);
        System.arraycopy(coordinates[0][1],0,ave1[1],0,margnum);
        //while(conv >= convergence) {
        for(int i=0;i<numObs;i++)
            Outlines.superimposeCoordinates(ave1, coordinates[i]);
        //if(!Outlines.superimposeCoordinates(ave1, coordinates[i]))
        //return false;
            /*
            if(convergence==1)
                break;
            else {
                Outlines.calculateAverageCoordinates(coordinates, ave1);
                if(cntr>0) {
                    try {
                        distn = Math.abs(Outlines.calculateProcrustesDistance(ave1,ave2,true)-disto);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    conv = Math.abs(distn-disto)/disto;
                    disto=distn;
                }
                System.arraycopy(ave1[0],0,ave2[0],0,margnum);
                System.arraycopy(ave1[1],0,ave2[1],0,margnum);
                cntr++;
            }
             */
        //}
        
        //return true;
        return coordinates;
    }
    
    public static double calculateProcrustesDistance(double[][] coords1, double[][] coords2, boolean superimposed) throws Exception {
        double avedist = 0;
        int margnum = coords1[0].length;
        
        if(coords1[0].length!=coords2[0].length)
            return -1;
        if(!superimposed)
            coords2=Outlines.superimposeCoordinates(coords1, coords2);
        
        for(int t=0; t<=margnum-1; t++) {
            avedist+=(coords2[0][t]-coords1[0][t])*(coords2[0][t]-coords1[0][t]) + (coords2[1][t]-coords1[1][t])*(coords2[1][t]-coords1[1][t]);
        }
        avedist = Math.sqrt(avedist)/(double)margnum;   /*//avedist = sqrt(avedist/margnum);*/
        return avedist;
    }
    
//optimally rotates coords1 onto coords2
    public static double[][] optimallyRotate(double[][] coords1, double[][] coords2) throws Exception {
        double B;
        double C;
        double lamb1, lamb2;
        double v1,v2,v3,v4;
        double u1,u2,u3,u4;
        double a=0,b=0,c=0,d=0;
        double[][] A = new double[2][2];
        double[][] Q = new double[2][2];
        
        int margnum = coords1[0].length;
        if(margnum!=coords2[0].length) throw new Exception("Coordinate matrices differed in dimension during rotation");
        
        
        for(int t=0; t<=margnum-1; t++) {
            a += coords2[0][t]*coords1[0][t];
            b += coords2[0][t]*coords1[1][t];
            c += coords2[1][t]*coords1[0][t];
            d += coords2[1][t]*coords1[1][t];
        }
        
        
        /* from the characteristic polynomial in this case the quadritic is used */
        
        B = -(a*a + b*b + c*c + d*d);
        C = a*a*d*d + c*c*b*b - 2*a*b*c*d;
        
        /* calculated the eigenvalues for A^t A */
        
        lamb1 = (-B + Math.sqrt(B*B - 4*C)) / 2;
        lamb2 = (-B - Math.sqrt(B*B - 4*C)) / 2;
        
/*
printf("Here is the initial matrix A:\n[%f     %f]\n[%f     %f]\n\n", a,b,c,d);
printf("Here is the diagonal matrix:\n[%f    0]\n[0    %f]\n\n", sqrt(lamb1), sqrt(lamb2));
 */
        
/* V = [v1 v2]
       [v3 v4]
 
Start with v1 = v2 = 1; solve for v3, v4;
normalize v3, v4;
solve for v1, v2 using the normalized values.
 */
        
        v3 = -(a*a + a*b + c*c + c*d - lamb1) / (a*b + c*d + b*b + d*d - lamb1);
        v4 = -(a*a + a*b + c*c + c*d - lamb2) / (a*b + c*d + b*b + d*d - lamb2);
        
        v3 = v3 / (Math.sqrt(1 + v3*v3));
        v4 = v4 / (Math.sqrt(1 + v4*v4));
        
        v1 = -(a*b + c*d + b*b + d*d - lamb1) * v3 / (a*a + a*b + c*c + c*d - lamb1);
        v2 = -(a*b + c*d + b*b + d*d - lamb2) * v4 / (a*a + a*b + c*c + c*d - lamb2);
        
        /* Solve for U */
        
        u1 = (a*v1+b*v3)/Math.sqrt(lamb1);
        u2 = (a*v2+b*v4)/Math.sqrt(lamb2);
        u3 = (c*v1+d*v3)/Math.sqrt(lamb1);
        u4 = (c*v2+d*v4)/Math.sqrt(lamb2);
        
/*
printf("Here is your matrix V:\n[%f    %f]\n[%f    %f]\n\n", v1,v2,v3,v4);
 
printf("Here is your matrix U:\n[%f    %f]\n[%f    %f]\n\n", u1,u2,u3,u4);
 
 
printf("Here is to check UEV^T = X:\n[%f     %f]\n[%f    %f]\n\n", v1*u1*sqrt(lamb1)+v2*u2*sqrt(lamb2), v3*u1*sqrt(lamb1)+v4*u2*sqrt(lamb2), u3*v1*sqrt(lamb1)+u4*v2*sqrt(lamb2), u3*v3*sqrt(lamb1)+u4*v4*sqrt(lamb2));
 
printf("Here is your optimal rotation matrix R:\n[%f    %f]\n[%f    %f]\n\n", u1*v1+u2*v2, u1*v3+u2*v4, u3*v1+u4*v2, u3*v3+u4*v4);
 */
        
        Q[0][0] = u1*v1+u2*v2;
        Q[0][1] = u1*v3+u2*v4;
        Q[1][0] = u3*v1+u4*v2;
        Q[1][1] = u3*v3+u4*v4;
        double t1=0,t2=0;
        for(int t=0; t<=margnum-1; t++) {
            t1=coords2[0][t]*Q[0][0]+coords2[1][t]*Q[1][0];
            t2=coords2[0][t]*Q[0][1]+coords2[1][t]*Q[1][1];
            coords2[0][t]=t1;
            coords2[1][t]=t2;
        }
        return coords2;
    }
    
    public static double[][] calculateFourierCoefficients(double[][] coords1, int harmnum) {
        int margnum = coords1[0].length;
        //System.out.println("Harmnum is " + harmnum + " and margnum is " + margnum);
        //System.out.println("e1");
        double[][] rf1 = new double[2][margnum];
        //System.out.println("e2");
        Outlines.makeRadialFunction(coords1, rf1);
        //System.out.println("e3");
        double[][] out = Outlines.getHarmonics(rf1, harmnum);
        //for(int i=0; i<harmnum; i++)
        //{
        //System.out.print(out[0][i] + ",a " + out[1][i] + " " );
        //}
        //System.out.println();
        //System.out.println();
        //for(int i=0; i<margnum; i++)
        //{
        //System.out.print(coords1[0][i] + ",b " + coords1[1][i] + " " );
        //}
        //System.out.println();
        
        //System.out.println("e4");
        return out;
        
    }
    
    
    //requires outlines be size-normalized (centroid-scaled) and translated so that centroid is at origin
    public static double calculateFourierDistance(double[][] coords1, double[][] coords2, int harmnum) {
        int margnum = coords1[0].length;
        double[][] rf1 = new double[2][margnum], rf2 = new double[2][margnum];
        double[] a1=new double[harmnum],b1=new double[harmnum],a2=new double[harmnum],b2=new double[harmnum];
        
        
        Outlines.makeRadialFunction(coords1, rf1);
        Outlines.makeRadialFunction(coords2, rf2);
        
        double[][] tmp = Outlines.getHarmonics(rf1, harmnum);
        a1=tmp[0];
        b1=tmp[1];
        
        
        tmp = Outlines.getHarmonics(rf2, harmnum);
        a2=tmp[0];
        b2=tmp[1];
        
        return Outlines.calculateFourierDistance(a1,b1,a2,b2);
    }
    
    public static double calculateFourierDistance(double[] a1, double[] b1, double[] a2, double[] b2) {
        double dist=0.0;
        int harmnum = a1.length;
        for(int z=0; z<=harmnum-1; z++) {
            dist+=(a1[z]-a2[z])*(a1[z]-a2[z])+(b1[z]-b2[z])*(b1[z]-b2[z]);
        }
        dist=Math.sqrt(dist);                /*// dist=sqrt(dist/harmnum);*/
        return dist;
    }
    
    /* this code will not return the correct average avea and aveb - these need to be returned
    public static void calculateAverageFourierCoefficients(double[][][] coordinates,double[] avea,double[] aveb,int harmnum) {
        int numObs = coordinates.length;
        int margnum = coordinates[0][0].length;
        double[] a = new double[harmnum];
        double[] b = new double[harmnum];
        double[][] rf;
     
        for(int q=0;q<=numObs-1;q++) {
            rf = new double[2][margnum];
            a = new double[harmnum];
            b = new double[harmnum];
            Outlines.makeRadialFunction(coordinates[q], rf);
            Outlines.getHarmonics(rf, a,b,harmnum);
     
            for(int z=0;z<=harmnum-1;z++) {
                if(q==0) {
                    avea[z]=0.0;
                    aveb[z]=0.0;
                }
                avea[z]+=a[z];
                aveb[z]+=b[z];
            }
        }
        for(int z=0; z<=harmnum-1; z++) {
            avea[z]=avea[z]/(double)(numObs);
            aveb[z]=aveb[z]/(double)(numObs);
        }
    }
     */
    
    public static double calculateCombinedDistanceScore(double[][] coords1, double[][] coords2, int harmnum, boolean superimposed) throws Exception {
        return (Outlines.calculateFourierDistance(coords1,coords2,harmnum)+9.4*Outlines.calculateProcrustesDistance(coords1,coords2,superimposed))/2;
    }
    
    public static double calculateCombinedDistanceScore(double[] a1, double[] b1, double[] a2, double[] b2, double[][] coords1, double[][] coords2, int harmnum, boolean superimposed) throws Exception {
        return (Outlines.calculateFourierDistance(a1,b1,a2,b2)+9.4*Outlines.calculateProcrustesDistance(coords1,coords2,superimposed))/2;
    }
    
/*
public static boolean extractCoordinates(String taxon, double[][][] coordinates)
        {
        String sql ="select taxon_part_coordinates.coordinates, taxon_part_coordinates.coordinates_number, taxon_part_coordinates.dimensionality from taxon_part_coordinates, taxon where taxon.taxon_name = '"+taxon+"' and taxon.taxon_id = taxon_part_coordinates.taxon_id";
        int numCoords =0;
        int cntr=0;
        String c=null;
        int d = 0;
        char delimiter = ',';
        String temp = null;
        try{
                int numMatches = Utilities.getRowCount(sql,conn,user);
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(sql);
 
                while(rset.next())
                        {
                        numCoords = rset.getInt(2);
                        c = rset.getString(1);
                        d = rset.getInt(3);
 
                        if(cntr==0)
                                coordinates = new double[numMatches][d][numCoords];
                        for(int i=0;i<numCoords;i++)
                                {
                                for(int t=0; t<d; t++)
                                        {
                                        if(c.indexOf(delimiter)>=0)
                                                {
                                                temp = c.substring(0,c.indexOf(delimiter)).trim();
                                                if(!temp.matches("\\."))
                                                        temp += ".0";
                                                coordinates[cntr][t][i]=Double.valueOf(temp).doubleValue();
                                                c = c.substring(c.indexOf(delimiter)+1).trim();
                                                }
                                        else
                                                {
                                                c = c.trim();
                                                if(!c.matches("\\."))
                                                        c+=".0";
                                                coordinates[cntr][t][i]=Double.valueOf(c).doubleValue();
                                                }
                                        }
                                }
                        cntr++;
                        }
                }
        catch(Exception e){}//System.out.println(e.getMessage()); return false;}
        return true;
        }
 */
    
    public static double calculateAverageFourierDistance(double[] avea, double[] aveb, double[][][] coordinates, int harmnum) {
        double aveFD = 0;
        int numObs = coordinates.length;
        double[] a, b;
        for(int i=0; i<numObs; i++) {
            a = new double[harmnum];
            b = new double[harmnum];
            double[][] temp = new double[2][harmnum];
            temp = Outlines.getHarmonics(coordinates[i], harmnum);
            a = temp[0];
            b = temp[1];
            aveFD += Outlines.calculateFourierDistance(avea, aveb, a,b);
        }
        aveFD /= (double)numObs;
        return aveFD;
    }
    
    public static double calculateAverageProcrustesDistance(double[][] coords1, double[][][] coordinates, boolean superimposed) throws Exception {
        double avePD = 0;
        int numObs = coordinates.length;
        for(int i=0; i<numObs; i++)
            avePD += Outlines.calculateProcrustesDistance(coords1, coordinates[i], superimposed);
        avePD /= (double)numObs;
        return avePD;
    }
    
    public static double[][][] convertPolygons(Polygon[] p, int nPoints, boolean[] keep) {
        
        return Outlines.extractCoordinates(p,nPoints,keep);
    }
    
    //sample points of unknowns before proceeding
    //scale by centroid the points of unknowns before proceeding
    //translate the points of unknowns before proceeding
    /*
    public static String[] performIdentification(OutlinesList database, double[][][] unknown, int harmnum, int nPoints, double convergence, boolean superimposed) throws Exception {
        if(database==null || unknown==null || harmnum<=0 || convergence<0 || convergence>1) return null;
        
        Vector output = new Vector();
        Hashtable taxaPro = new Hashtable();
        Hashtable taxaFour = new Hashtable();
        Hashtable taxa = new Hashtable();
        double[][] aveCoords = new double[2][nPoints];
        double[][][] coordinates = new double[unknown.length][2][nPoints];
        double[] avea = new double[harmnum];
        double[] aveb = new double[harmnum];
        while(database.previous!=null)
            database = database.previous;
        
        
        
        //System.out.println("The coordinates of the first unknown leaf are: " );
        //for(int i=0; i<unknown[0][0].length; i++)
        //System.out.println(Double.toString(unknown[0][0][i]) + "\t" + Double.toString(unknown[0][1][i]));
        
        //int tcnt=0;
        //while(database.next!=null) {
        //System.out.println("The coordinates of the known leaf " + tcnt);
        //for(int i=0; i<database.coordinatesSST[0].length; i++)
        //System.out.println(Double.toString(database.coordinatesSST[0][i]) + "\t" + Double.toString(database.coordinatesSST[1][i]));
        //tcnt++;
        //database = database.next;
        //}
        
        //while(database.previous!=null)
        //database = database.previous;
        
        //System.out.println("The coordinates of the known leaf " + tcnt);
        //for(int i=0; i<database.coordinatesSST[0].length; i++)
        //System.out.println(Double.toString(database.coordinatesSST[0][i]) + "\t" + Double.toString(database.coordinatesSST[1][i]));
        
        
        
        //System.out.println("\tFor first coordinates, the centroid size is " + Double.toString(Outlines.findCentroidDistance(unknown[0])));
        //System.out.println("Superimposing coordinates of unknowns");
        //Outlines.superimposeCoordinates(coordinates,convergence);
        //System.out.println("\tAfter superimposition, the centroid size is " + Double.toString(Outlines.findCentroidDistance(unknown[0])));
        //System.out.println("Calculating average coordinates of unknowns.");
        //Outlines.calculateAverageCoordinates(coordinates,aveCoords);
        //System.out.println("\tFor average coordinates, the centroid size is " + Double.toString(Outlines.findCentroidDistance(aveCoords)));
        aveCoords=unknown[0];
        double[][] tempa = new double[2][nPoints];
        System.arraycopy(aveCoords[0],0,tempa[0],0,nPoints);
        System.arraycopy(aveCoords[1],0,tempa[1],0,nPoints);
        //System.out.println("The coordinates of the first unknown leaf are: " );
        //for(int i=0; i<unknown[0][0].length; i++)
        //System.out.println(Double.toString(unknown[0][0][i]) + "\t" + Double.toString(unknown[0][1][i]));
        
        //for(int i=0; i<unknown.length; i++) {
        //System.out.println("Unknown Leaf " + i);
        //for(int j=0; j<unknown[0][0].length; j++) System.out.println(unknown[i][0][j] + "\t" + unknown[i][1][j]);
        
        //}
        
        //System.out.println("Comparing leaves");
        //tcnt = 0;
        while(database.next!=null) {
            
            //System.out.println(database.taxon + " " + database.label);
            String species = database.taxon;
            double[][] temp = new double[2][nPoints];
            
            System.arraycopy(database.coordinatesSST[0],0,temp[0],0,nPoints);
            System.arraycopy(database.coordinatesSST[1],0,temp[1],0,nPoints);
            System.arraycopy(tempa[0],0,aveCoords[0],0,nPoints);
            System.arraycopy(tempa[1],0,aveCoords[1],0,nPoints);
            //System.out.println("\tBefore scaling, the centroid size is " + Double.toString(Outlines.findCentroidDistance(database.coordinatesSST)));
            //double[][] temp = Outlines.scalePoints(Outlines.findCentroidDistance(database.coordinatesSST), database.coordinatesSST);
            //System.out.println("\tAfter scaling, the centroid size is " + Double.toString(Outlines.findCentroidDistance(temp)));
            //temp = Outlines.samplePoints(temp,nPoints);
            //System.out.println("\tAfter sampling, the centroid size is " + Double.toString(Outlines.findCentroidDistance(temp)));
            //System.out.println("The centroids are " + Double.toString(Outlines.findCentroidDistance(temp)) + " and " + Double.toString(Outlines.findCentroidDistance(aveCoords)));
            
            
            
            double harmDist = Outlines.calculateFourierDistance(aveCoords, temp, harmnum);
            
            //System.arraycopy(database.coordinatesSST[0],0,temp[0],0,nPoints);
            //System.arraycopy(database.coordinatesSST[1],0,temp[1],0,nPoints);
            //System.arraycopy(tempa[0],0,aveCoords[0],0,nPoints);
            //System.arraycopy(tempa[1],0,aveCoords[1],0,nPoints);
            
            double proDist = Outlines.calculateProcrustesDistance(aveCoords,temp,false);
            
            //System.out.println("The coordinates of the known leaf " + tcnt);
            //for(int i=0; i<temp[0].length; i++)
            //System.out.println(Double.toString(temp[0][i]) + "\t" + Double.toString(temp[1][i]));
            //tcnt++;
            
            //System.out.println("The procrustes distance is " + Double.toString(proDist) + " whereas the Fourier distance is " + Double.toString(harmDist));
            
            String hd = (String)taxaFour.get(species);
            if(hd==null) hd=Double.toString(harmDist);
            else hd += " " + Double.toString(harmDist);
            taxaFour.put(species,hd);
            
            String pd = (String)taxaPro.get(species);
            if(pd==null) pd=Double.toString(proDist);
            else pd += " " + Double.toString(proDist);
            taxaPro.put(species,pd);
            
            Integer tt = (Integer)taxa.get(species);
            if(tt==null) tt=new Integer(1);
            else tt=new Integer(tt.intValue()+1);
            taxa.put(species,tt);
            
            database=database.next;
        }
        //do comparison on last leaf below
        //System.out.println(database.taxon + " " + database.label);
        String species = database.taxon;
        double[][] temp = new double[2][nPoints];
        System.arraycopy(database.coordinatesSST[0],0,temp[0],0,nPoints);
        System.arraycopy(database.coordinatesSST[1],0,temp[1],0,nPoints);
        System.arraycopy(tempa[0],0,aveCoords[0],0,nPoints);
        System.arraycopy(tempa[1],0,aveCoords[1],0,nPoints);
        //System.out.println("\tBefore scaling, the centroid size is " + Double.toString(Outlines.findCentroidDistance(database.coordinatesSST)));
        //double[][] temp = Outlines.scalePoints(Outlines.findCentroidDistance(database.coordinatesSST), database.coordinatesSST);
        //System.out.println("\tAfter scaling, the centroid size is " + Double.toString(Outlines.findCentroidDistance(temp)));
        //temp = Outlines.samplePoints(temp,nPoints);
        //System.out.println("\tAfter sampling, the centroid size is " + Double.toString(Outlines.findCentroidDistance(temp)));
        //System.out.println("The centroids are " + Double.toString(Outlines.findCentroidDistance(temp)) + " and " + Double.toString(Outlines.findCentroidDistance(aveCoords)));
        
        
        
        
        double harmDist = Outlines.calculateFourierDistance( temp, aveCoords, harmnum);
        
        //System.arraycopy(database.coordinatesSST[0],0,temp[0],0,nPoints);
        //System.arraycopy(database.coordinatesSST[1],0,temp[1],0,nPoints);
        //System.arraycopy(tempa[0],0,aveCoords[0],0,nPoints);
        //System.arraycopy(tempa[1],0,aveCoords[1],0,nPoints);
        
        double proDist = Outlines.calculateProcrustesDistance(aveCoords,temp,false);
        
        //System.out.println("The coordinates of the known leaf " + tcnt);
        //for(int i=0; i<nPoints; i++)
        //System.out.println(Double.toString(temp[0][i]) + "\t" + Double.toString(temp[1][i]));
        
        //System.out.println("The procrustes distance is " + Double.toString(proDist) + " whereas the Fourier distance is " + Double.toString(harmDist));
        
        String hd = (String)taxaFour.get(species);
        if(hd==null) hd=Double.toString(harmDist);
        else hd += " " + Double.toString(harmDist);
        taxaFour.put(species,hd);
        
        String pd = (String)taxaPro.get(species);
        if(pd==null) pd=Double.toString(proDist);
        else pd += " " + Double.toString(proDist);
        taxaPro.put(species,pd);
        
        Integer tt = (Integer)taxa.get(species);
        if(tt==null) tt=new Integer(1);
        else tt=new Integer(tt.intValue()+1);
        taxa.put(species,tt);
        
        
        //enumerate through taxa hash
        Set keys = taxa.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            double hD = Utils.getAverage((String)taxaFour.get((String)key));
            double pD = Utils.getAverage((String)taxaPro.get((String)key));
            
            double cd = hD+9.4*pD;
            
            output.add(cd + "\t" + (String)key);
        }
        
        
        String[] out = (String[]) output.toArray(new String[0]);
        Arrays.sort(out);
        
        //System.out.println("Returning the sorted species by identification accuracy.");
        return(out);
    }
     */
    
    public static Hashtable sortSSTOutlinesByTaxon(OutlinesList osl) {
        Hashtable out = new Hashtable();
        while(osl.previous!=null)
            osl = osl.previous;
        //System.out.println("Making taxon heap");
        while(osl.next!=null) {
            //System.out.println("Adding " + osl.taxon + " " + osl.label);
            Vector t = (Vector)out.get(osl.taxon);
            if(t==null)
                t = new Vector();
            double[][] temp = new double[2][osl.coordinatesSST[0].length];
            System.arraycopy(osl.coordinatesSST[0],0,temp[0],0,osl.coordinatesSST[0].length);
            System.arraycopy(osl.coordinatesSST[1],0,temp[1],0,osl.coordinatesSST[1].length);
            t.add(temp);
            out.put(osl.taxon,t);
            osl = osl.next;
        }
        //System.out.println("Adding " + osl.taxon + " " + osl.label);
        Vector t = (Vector)out.get(osl.taxon);
        if(t==null)
            t = new Vector();
        double[][] temp = new double[2][osl.coordinatesSST[0].length];
        System.arraycopy(osl.coordinatesSST[0],0,temp[0],0,osl.coordinatesSST[0].length);
        System.arraycopy(osl.coordinatesSST[1],0,temp[1],0,osl.coordinatesSST[1].length);
        t.add(temp);
        out.put(osl.taxon,t);
        
        return out;
    }
    
    public static Hashtable sortSSTOutlinesByTaxon(Hashtable speciesDatabase) {
        Hashtable out = new Hashtable();
        Vector t = new Vector();
        Set keys = speciesDatabase.keySet();
        Iterator iter = keys.iterator();
        Vector output = new Vector();
        while (iter.hasNext()) {
            t=new Vector();
            Species t1 = (Species)speciesDatabase.get(iter.next());
            while(t1.outlines.previous!=null)
                t1.outlines=t1.outlines.previous;
            while(t1.outlines.next!=null) {
                t.add(t1.outlines.coordinatesSST);
                t1.outlines=t1.outlines.next;
            }
            t.add(t1.outlines.coordinatesSST);
            out.put(t1.name,t);
        }
        
        return out;
    }
    
    
/*
public static LinkedList performIdentification(String[] taxa, double[][][] coordinates, int harmnum, double convergence, boolean superimposed)
        {
        LinkedList ll = new LinkedList();
        double score = 0;
        int numTaxa = taxa.length;
        int margnum = coordinates[0][0].length;
        double[][] aveCoords;
        double[][][] temp = new double[2][2][2];
        double[] avea, aveb;
        for(int i =0; i<numTaxa; i++)
                {
                aveCoords = new double[2][margnum];
                avea = new double[harmnum];
                aveb = new double[harmnum];
                temp = new double[2][2][2];
 
                Outlines.extractCoordinates(taxa[i], temp);
                Outlines.calculateAverageFourierCoefficients(temp,avea,aveb,harmnum);
                Outlines.superimposeCoordinates(coordinates,convergence);
                Outlines.calculateAverageCoordinates(coordinates,aveCoords);
 
                score =(9.4*Outlines.calculateAverageProcrustesDistance(aveCoords,coordinates,superimposed)+Outlines.calculateAverageFourierDistance(avea,aveb,coordinates,harmnum))/2;
                ll.add(score + ": " + taxa[i]);
                }
        return ll;
        }
 */
    public static String coordinateInformation(OutlinesList ol) {
        //System.out.println("Coordinate information");
        String output = new String();
        output += ol.taxon + "\t";
        output += ol.label + "\t";
        output += ol.part_name + "\t";
        output += ol.number + "\t";
        output += ol.dimensionality + "\t";
        output += ol.centered_normalized + "\t";
        output += ol.pixelArea + "\t";
        output += ol.pixelPerCentimeter + "\t";
        output += ol.imageFile + "\t";
        //System.out.println("The number is " + ol.number);
        //System.out.println("The dimension is " + ol.dimensionality);
        for(int j=0; j<ol.number; j++)
            for(int i=0; i<ol.dimensionality; i++)
                output += ol.coordinates[i][j] + "\t";
        return output + "\n";
    }
    
    
    
//file format as follows:
//<taxon name1>\t<unique label for coordinates>\t<part name>\t<number of coordinates>\t<dimension of coordinate>\t<centered and/or normalized>\tx1<delimiter>y1<delimiter>z1<delimiter>...<delimiter>x2<delimiter>y2<delimiter>...
//...
//routine returns a OutlinesList of coordinates
    
    public static OutlinesList inputCoordinatesFromFile(BufferedReader in, char delimiter) {
        OutlinesList ol = new OutlinesList();
        //System.out.println("Database opening");
        try{
            String line = in.readLine();
            if(line==null) return null;
            if(line.length()<=0) return null;
            String temp = null;
            while(line!=null) {
                ol.taxon = line.substring(0,line.indexOf('\t')).trim();
                line = line.substring(line.indexOf('\t')+1).trim();
                ol.label = line.substring(0,line.indexOf('\t')).trim();
                line = line.substring(line.indexOf('\t')+1).trim();
                //System.out.println("Label is " + ol.label);
                ol.part_name = line.substring(0,line.indexOf('\t')).trim();
                line = line.substring(line.indexOf('\t')+1).trim();
                ol.number = Integer.valueOf(line.substring(0,line.indexOf('\t')).trim()).intValue();
                line = line.substring(line.indexOf('\t')+1).trim();
                ol.dimensionality = Integer.valueOf(line.substring(0,line.indexOf('\t')).trim()).intValue();
                line = line.substring(line.indexOf('\t')+1).trim();
                ol.centered_normalized = Integer.valueOf(line.substring(0,line.indexOf('\t')).trim()).intValue();
                line = line.substring(line.indexOf('\t')+1).trim();
                ol.pixelArea = Integer.valueOf(line.substring(0,line.indexOf('\t')).trim()).intValue();
                line = line.substring(line.indexOf('\t')+1).trim();
                ol.pixelPerCentimeter = Double.valueOf(line.substring(0,line.indexOf('\t')).trim()).doubleValue();
                line = line.substring(line.indexOf('\t')+1).trim();
                ol.imageFile = line.substring(0,line.indexOf('\t')).trim();
                line = line.substring(line.indexOf('\t')+1).trim();
                ol.coordinates = new double[ol.dimensionality][ol.number];
                for(int i=0;i<ol.number;i++) {
                    for(int t=0; t<ol.dimensionality; t++) {
                        if(line.indexOf(delimiter)>=0) {
                            temp = line.substring(0,line.indexOf(delimiter)).trim();
                            //if(!temp.matches("\\."))
                            //  temp += ".0";
                            //System.out.println(temp);
                            ol.coordinates[t][i]=Double.valueOf(temp).doubleValue();
                            line = line.substring(line.indexOf(delimiter)+1).trim();
                        } else {
                            line = line.trim();
                            //if(!line.matches("\\."))
                            //  line+=".0";
                            //System.out.println(line);
                            ol.coordinates[t][i]=Double.valueOf(line).doubleValue();
                        }
                    }
                }
                
                line = in.readLine();
                if(line!=null) {
                    ol.next = new OutlinesList();
                    ol.next.previous = ol;
                    ol = ol.next;
                }
            }
        } catch(Exception e){
            //System.out.println("Error reading outline database");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return ol;
    }
    
    public static OutlinesList inputCoordinatesFromFile(String file, char delimiter) {
        OutlinesList ol = new OutlinesList();
        //System.out.println("Database opening");
        try{
            //System.out.println("File is set to " + file);
            BufferedReader in = new BufferedReader(new FileReader(file));
            ol = inputCoordinatesFromFile(in,delimiter);
        } catch(Exception e){
            //System.out.println("Error reading outline database");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return ol;
    }
    
    
    
    
//requires translation of the centroid to the origin before calling this function
    public static void makeRadialFunction(double[][] coords, double radialFunction[][]) {
        int margnum = coords[0].length;
        for(int t=0;t<=margnum-1;t++) {
            if(t==0) {
                radialFunction[0][t]=Math.sqrt((coords[0][margnum-1]-coords[0][t])*(coords[0][margnum-1]-coords[0][t]) + (coords[1][margnum-1]-coords[1][t])*(coords[1][margnum-1]-coords[1][t]));
            }
            if(t>0) {
                radialFunction[0][t] = radialFunction[0][t-1] + Math.sqrt((coords[0][t-1] -coords[0][t])*(coords[0][t-1]-coords[0][t]) +(coords[1][t-1]-coords[1][t])*(coords[1][t-1]-coords[1][t]));
            }
            radialFunction[1][t] = Math.sqrt(coords[0][t]*coords[0][t] + coords[1][t]*coords[1][t]);
        }
    }
    
    public static double[][] calculateAverageHarmonics(OutlinesList harmonics, int nHarm) {
        double[][] aveH = new double[2][nHarm];
        while(harmonics.previous!=null)
            harmonics=harmonics.previous;
        if(harmonics.next==null)
            return harmonics.coordinates;
        int cnt=0;
        while(harmonics.next!=null) {
            for(int i=0;i<nHarm;i++) {
                if(cnt==0) {
                    aveH[0][i]=harmonics.coordinates[0][i];
                    aveH[1][i]=harmonics.coordinates[1][i];
                    
                } else {
                    aveH[0][i]+=harmonics.coordinates[0][i];
                    aveH[1][i]+=harmonics.coordinates[1][i];
                    
                }
            }
            cnt++;
            harmonics=harmonics.next;
        }
        cnt++;
        for(int i=0;i<nHarm;i++) {
            aveH[0][i]+=harmonics.coordinates[0][i];
            aveH[1][i]+=harmonics.coordinates[1][i];
            aveH[0][i]/=cnt;
            aveH[1][i]/=cnt;
        }
        return aveH;
    }
    
    
    
}

