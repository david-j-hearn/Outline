/*
 * OutlinesList.java
 *
 * Created on September 6, 2007, 8:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package outline;

/**
 *
 * @author projects
 */
import java.lang.*;
import java.util.*;


public class OutlinesList extends Object
	{
	OutlinesList next=null;
	OutlinesList previous=null;
	int number=-1;
	String label=null;
	String part_name=null;
	String taxon=null;
        String imageFile=null;
        double centroidDistance;
        int pixelArea;
        double pixelPerCentimeter;
	int centered_normalized=0;
	int dimensionality=0;
	double[][] coordinates=null;
        double[][] coordinatesSST=null;     //coordinates that are scaled, sampled, and translated
        
	public OutlinesList()
		{
		}
        
        public OutlinesList(String Taxon, String imageFile, double centroidDistance, int pixelArea, double pixelPerCentimeter, String Label, int Number, String Part_name, int Centered_normalized, int Dimensionality, double[][] Coordinates)
		{
                //System.out.println("ea1 " + Number + " " + Dimensionality);
		next = null;
		previous = null;
		taxon = Taxon;
                //System.out.println("ea2");
                this.imageFile=imageFile;
                this.centroidDistance = centroidDistance;
                this.pixelArea=pixelArea;
                this.pixelPerCentimeter=pixelPerCentimeter;
                //System.out.println("ea3");
		label = Label;
		number = Number;
		part_name = Part_name;
		centered_normalized = Centered_normalized;
		dimensionality = Dimensionality;
                //System.out.println("ea4");
                coordinates = new double[dimensionality][number];
		for(int i=0;i<dimensionality;i++)
                {
                    //System.out.println("ea4.1" + i);
			System.arraycopy(Coordinates[i],0,coordinates[i],0,number);
                        //System.out.println("ea4.2" + i);
                }
                //System.out.println("ea5");
		}
        
	public OutlinesList(OutlinesList iv)
		{
		if(iv.next==null)
			next=null;
		else
			next = iv.next;
		if(iv.previous==null)
			previous=null;
		else
			previous = iv.previous;
		number = iv.number;
                
		label = iv.label;
		part_name = iv.part_name;
		taxon = iv.taxon;
                imageFile=iv.imageFile;
                centroidDistance=iv.centroidDistance;
                pixelArea=iv.pixelArea;
                pixelPerCentimeter=iv.pixelPerCentimeter;
		centered_normalized = iv.centered_normalized;
		dimensionality = iv.dimensionality;
		coordinates = new double[dimensionality][number];
		for(int i=0;i<dimensionality;i++)
			System.arraycopy(iv.coordinates[i],0,this.coordinates[i],0,number);
		}

	

	public OutlinesList addElement(String Taxon, String Label, double centroidDistance,  int pixelArea, double pixelPerCentimeter, int Number, String Part_name, int Centered_normalized, int Dimensionality, double[][] Coordinates) 
        throws Exception
		{
                if(next!=null) throw new Exception("Coordinates List is wrong place");
		next = new OutlinesList();
		next.taxon = Taxon;
		next.label = Label;
		next.number = Number;
		next.part_name = Part_name;
                next.centroidDistance=centroidDistance;
                next.pixelArea = pixelArea;
                next.pixelPerCentimeter = pixelPerCentimeter;
		next.centered_normalized = Centered_normalized;
		next.dimensionality = Dimensionality;
                next.coordinates = new double[dimensionality][Number];
		for(int i=0;i<dimensionality;i++)
			System.arraycopy(Coordinates[i],0,next.coordinates[i],0,Number);
		next.previous = this;
		return next;
		}
        
	}

