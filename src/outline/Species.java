/*
 * Species.java
 *
 * Created on March 18, 2008, 2:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package outline;

import java.awt.Polygon;

/**
 *
 * @author projects
 */
public class Species {
    public String name;
    public int nPoints;
    public int nHarm;
    public OutlinesList outlines=null;
    public OutlinesList harmonics=null;
    public double[][] aveHarmonics=null;
    public double[][] aveCoords=null;
    public boolean sampledScaledTranslated=false;
    
    
    /** Creates a new instance of Species */
    
    public Species(String taxonName, double[][] coordinates, outlineMain o) //primary constructor
    {
        // System.out.println("The x,y for leaf 0 in species are: " + coordinates[0][0] + "," + coordinates[1][0]);
        init(taxonName, coordinates, o);
    }
    
    public Species(String taxonName, double[][] coordinates, int nPoints, int nHarm) {
        
        init(taxonName, coordinates, nPoints, nHarm);
    }
    
    
    public Species(String taxonName, Polygon p, outlineMain o) {
        
        //convert polygon to coordinates and initialize
        init(taxonName, Outlines.extractCoordinates(p), o);
        
        
    }
    
    
    private void init(String taxonName, double[][] coordinates, int nPoints, int nHarm) {
        //System.out.println("A");
        name=taxonName;
        this.nPoints=nPoints;
        //System.out.println("d");
        this.nHarm=nHarm;
        //System.out.println("b " + nHarm + " " + nPoints);
        this.outlines = new OutlinesList(taxonName, "unknown", 0.0, 0, 0.0, "unknown", coordinates[0].length, "unknown", 0, 2, coordinates);
        //scale, sample, and translate as needed
        scaleTranslateSampleDatabase();
        //System.out.println("c");
        
        //System.out.println("e " + nHarm + " " + nPoints);
        double[][] temp = Outlines.calculateFourierCoefficients(outlines.coordinatesSST,nHarm);
        //System.out.println("harms: " + temp[0][0] + "," + temp[1][0]);
        //System.out.println("harms: " + temp[0][1] + "," + temp[1][1]);
        //System.out.println("ea");
        harmonics = new OutlinesList(taxonName, "unknown", 0.0, 0, 0.0, "unknown", temp[0].length, "unknown", 0, 2, temp);
        //System.out.println("f");
        sampledScaledTranslated=false;
        //System.out.println("g");
    }
    
    private void init(String taxonName, double[][] coordinates, outlineMain o) {
        name=taxonName;
        nPoints=o.nPoints;
        nHarm=o.nHarm;
        System.out.println(nPoints + " " + nHarm);
        outlines = new OutlinesList(taxonName, "unknown", 0.0, 0, 0.0, "unknown", coordinates[0].length, "unknown", 0, 2, coordinates);
        //System.out.println("The x,y for leaf 0 in species init are: " + outlines.coordinates[0][0] + "," + outlines.coordinates[1][0]);
        //scale, sample, and translate as needed
        scaleTranslateSampleDatabase();
        
        //System.out.println("f");
        harmonics = new OutlinesList(taxonName, "unknown", 0.0, 0, 0.0, "unknown", nHarm, "unknown", 0, 2, Outlines.calculateFourierCoefficients(outlines.coordinatesSST,nHarm));
        sampledScaledTranslated=false;
    }
    
    
    public void appendCoordinates(String taxonName, double[][] coordinates) {
        //add unscaled, untranslated coordinates to outlines list
        if(outlines==null)
            outlines=new OutlinesList(taxonName, "unknown", 0.0, 0, 0.0, "unknown", coordinates[0].length, "unknown", 0, 2, coordinates);
        else {
            OutlinesList nosl = new OutlinesList(taxonName, "unknown", 0.0, 0, 0.0, "unknown", coordinates[0].length, "unknown", 0, 2, coordinates);
            outlines.next = nosl;
            nosl.previous=outlines;
            outlines=outlines.next;
        }
        //scale, sample, and translate as needed
        scaleTranslateSampleDatabase();
        //calculate Fourier coefficients and add them to harmonics list
        if(harmonics==null) {
            //System.out.println("g");
            harmonics = new OutlinesList(taxonName, "unknown", 0.0, 0, 0.0, "unknown", nHarm, "unknown", 0, 2, Outlines.calculateFourierCoefficients(outlines.coordinatesSST,nHarm));
        }
        
        else {
            //System.out.println("h");
            OutlinesList hosl = new OutlinesList(taxonName, "unknown", 0.0, 0, 0.0, "unknown", nHarm, "unknown", 0, 2, Outlines.calculateFourierCoefficients(outlines.coordinatesSST,nHarm));
            harmonics.next = hosl;
            hosl.previous=harmonics;
            harmonics=harmonics.next;
            
        }
        //set sampledScaledTranslated to false
        sampledScaledTranslated=true;
        
    }
    
    public void prepareCoordinates() throws Exception {
        //scale, sample, and translate as needed
        scaleTranslateSampleDatabase();
        //assume harmonics are calculated for each leaf
        //calculate average harmonics
        aveHarmonics = Outlines.calculateAverageHarmonics(harmonics,nHarm);
        //System.out.println("\tAverage harmonics.");
            //for(int i=0;i<nHarm;i++) {
            //System.out.println("\t" + outlines.taxon + "\t" + aveHarmonics[0][i] + "\t" + aveHarmonics[1][i]);
        //}
        //superimpose leaves
        optimallyRotateCoordinates();
        //while(outlines.previous!=null)
        //  outlines=outlines.previous;
        //while(outlines.next!=null)
        //{
//              for(int i=0;i<nPoints;i++) {
        //              System.out.println(outlines.taxon + "1\t" + outlines.coordinatesSST[0][i] + "\t" + outlines.coordinatesSST[1][i]);
        //        }
        //        outlines=outlines.next;
        //}
        //calculate average coordinates
        aveCoords = Outlines.calculateAverageCoordinates(outlines,nPoints);
        //System.out.println("\tAverage coordinates.");
            //for(int i=0;i<nPoints;i++) {
            //System.out.println(outlines.taxon + "\t" + aveCoords[0][i] + "\t" + aveCoords[1][i]);
        //}
        sampledScaledTranslated=true;
    }
    
    
    
    
    private void optimallyRotateCoordinates() throws Exception {
        while(outlines.previous!=null)
            outlines=outlines.previous;
        
        if(outlines.next==null) {
            return;
        }
        //set the leaf against which all others will be superimposed
        double[][] standard = new double[2][nPoints];
        System.arraycopy(outlines.coordinatesSST[0],0,standard[0],0,nPoints);
        System.arraycopy(outlines.coordinatesSST[1],0,standard[1],0,nPoints);
        outlines=outlines.next;
        
        while(outlines.next!=null) {
            
            if(outlines.coordinatesSST==null || outlines.centered_normalized!=2) throw new Exception("Coordinates should be sampled, scaled, and translated before being superimposed");
            outlines.coordinatesSST=Outlines.optimallyRotate(standard, outlines.coordinatesSST);
            //  for(int i=0;i<nPoints;i++) {
            //    System.out.println(outlines.taxon + "\t" + outlines.coordinatesSST[0][i] + "\t" + outlines.coordinatesSST[1][i]);
            //}
            outlines = outlines.next;
            
        }
        
        if(outlines.coordinatesSST==null || outlines.centered_normalized!=2) throw new Exception("Coordinates should be sampled, scaled, and translated before being superimposed");
        outlines.coordinatesSST=Outlines.optimallyRotate(standard, outlines.coordinatesSST);
        
        //for(int i=0;i<nPoints;i++) {
        //  System.out.println(outlines.taxon + "\t" + outlines.coordinatesSST[0][i] + "\t" + outlines.coordinatesSST[1][i]);
        //}
    }
    
    private void scaleTranslateSampleDatabase() {
        //System.out.println("Before scaling, the centroid distance is " + Outlines.findCentroidDistance(outlines.coordinates));
        while(outlines.previous!=null)
            outlines=outlines.previous;
        while(outlines.next!=null) {
            //System.out.println("In while");
            if(outlines.centered_normalized!=2 || outlines.coordinatesSST==null) {
                //if(outlines.coordinates==null)
                //System.out.println("The outline coordinates are null for " + outlines.taxon);
                //System.out.println("Woring on " + outlines.taxon);
                outlines.centroidDistance=Outlines.findCentroidDistance(outlines.coordinates);
                outlines.coordinatesSST = Outlines.scalePoints(outlines.centroidDistance,outlines.coordinates);
                System.out.println("After scaling, the centroid distance is " + Outlines.findCentroidDistance(outlines.coordinatesSST));
                outlines.coordinatesSST = Outlines.translateCoordinates(outlines.coordinatesSST,Outlines.findCentroid(outlines.coordinatesSST));
                outlines.coordinatesSST = Outlines.samplePoints(outlines.coordinatesSST, nPoints);
                //for(int i=0;i<outlines.coordinatesSST[0].length;i++) {
                //  System.out.println(outlines.coordinatesSST[0][i] + "\t" + outlines.coordinatesSST[1][i]);
                // }
                outlines.centered_normalized=2;
            }
            outlines = outlines.next;
        }
        if(outlines.centered_normalized!=2 || outlines.coordinatesSST==null) {
            //System.out.println("In if");
            outlines.centroidDistance=Outlines.findCentroidDistance(outlines.coordinates);
            //System.out.println(nPoints + " " + outlines.coordinates[0][0] + " " + outlines.coordinates[1][0]);
            outlines.coordinatesSST = Outlines.scalePoints(outlines.centroidDistance,outlines.coordinates);
            //System.out.println(nPoints + " " + outlines.coordinatesSST[0][0] + " " + outlines.coordinatesSST[1][0]);
            //System.out.println(nPoints + " ");
            //System.out.println("After scaling, the centroid distance is A" + Outlines.findCentroidDistance(outlines.coordinatesSST));
            outlines.coordinatesSST = Outlines.translateCoordinates(outlines.coordinatesSST,Outlines.findCentroid(outlines.coordinatesSST));
            outlines.coordinatesSST = Outlines.samplePoints(outlines.coordinatesSST, nPoints);
            //System.out.println(nPoints + " " + outlines.coordinatesSST[0][0] + " " + outlines.coordinatesSST[1][0]);
            outlines.centered_normalized=2;
            
        }
        //System.out.println("In out");
    }
    
}
