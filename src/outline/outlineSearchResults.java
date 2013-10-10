/*
 * outlineSearchResults.java
 *
 * Created on September 9, 2007, 7:05 PM
 */

package outline;

import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.jnlp.*;

/**
 *
 * @author  projects
 */
public class outlineSearchResults extends javax.swing.JInternalFrame {
    public Hashtable taxa = new Hashtable();
    public String[] taxaSorted = null;
    double[] score = null;
    double[][][] unknowns=null;
    int cnt1=0;
    int cnt2=0;
    public Vector currentOutlines=null;
    double[][] currentOutline=null;
    
    /** Creates new form outlineSearchResults */
    public outlineSearchResults(String[] id, double[][][] Unknowns, Hashtable taxa) {
        super("Search Results",
                true, //resizable
                true, //closable
                true, //maximizable
                true);//iconifiable
        initComponents();
        this.taxa=taxa;
        unknowns = new double[Unknowns.length][2][Unknowns[0][0].length];
        
        for(int i=0; i<Unknowns.length; i++) {
            //System.out.println("Unknown Leaf " + i);
            //for(int j=0; j<Unknowns[0][0].length; j++) System.out.println(Unknowns[i][0][j] + "\t" + Unknowns[i][1][j]);
            System.arraycopy(Unknowns[i][0],0,unknowns[i][0],0,Unknowns[i][0].length);
            System.arraycopy(Unknowns[i][1],0,unknowns[i][1],0,Unknowns[i][1].length);
        }
        
        
        if(taxa==null) System.out.println("Taxa hash is null");
        taxaSorted=new String[id.length];
        System.arraycopy(id,0,taxaSorted,0,id.length);
        //System.out.println("There are " + taxaSorted.length + " taxa. The first on is " + taxaSorted[0]);
        score = new double[taxaSorted.length];
        for(int i=0; i<taxaSorted.length; i++) {
            String[] temp = taxaSorted[i].split("\t");
            taxaSorted[i] = temp[1];
            score[i] = Double.parseDouble(temp[0]);
        }
        
        
        
        
        
        //System.out.println("There are " + taxaSorted.length + " taxa. The first on is " + taxaSorted[0] + " and the first score is " + score[0]);
        //Make the list
        speciesList.removeAllItems();
        for(int i=0; i<taxaSorted.length; i++) {
            speciesList.addItem(taxaSorted[i]);
        }
        speciesList.setSelectedItem(taxaSorted[0]);
        
        initDisplay();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        outlineDisplay = new outline.outlineDrawer();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        searchResultsLabel = new javax.swing.JLabel();
        outlineComparisonLabel = new javax.swing.JLabel();
        speciesList = new javax.swing.JComboBox();
        nextButton1 = new javax.swing.JButton();
        previousButton1 = new javax.swing.JButton();
        unknownSpecies = new javax.swing.JLabel();
        knownSpecies = new javax.swing.JLabel();
        saveGraphics = new javax.swing.JButton();
        scoreLabel = new javax.swing.JLabel();
        linkButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        previousButton.setText("Previous");
        previousButton.setEnabled(false);
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next");
        nextButton.setEnabled(false);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        searchResultsLabel.setText("Search Results:");

        outlineComparisonLabel.setText("Outline Comparison:");

        speciesList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        speciesList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speciesListActionPerformed(evt);
            }
        });

        nextButton1.setText("Next");
        nextButton1.setEnabled(false);
        nextButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButton1ActionPerformed(evt);
            }
        });

        previousButton1.setText("Previous");
        previousButton1.setEnabled(false);
        previousButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButton1ActionPerformed(evt);
            }
        });

        unknownSpecies.setText("Unknown Species:");

        saveGraphics.setText("Save");
        saveGraphics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGraphicsActionPerformed(evt);
            }
        });

        scoreLabel.setText("Score:");

        linkButton.setText("Link");
        linkButton.setEnabled(false);
        linkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(outlineDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(searchResultsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                                .addComponent(linkButton))
                            .addComponent(speciesList, 0, 229, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(outlineComparisonLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveGraphics))
                            .addComponent(scoreLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(previousButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nextButton1))
                            .addComponent(unknownSpecies)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(previousButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nextButton))
                            .addComponent(knownSpecies))
                        .addGap(30, 30, 30))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nextButton, previousButton});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nextButton1, previousButton1});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {knownSpecies, unknownSpecies});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchResultsLabel)
                            .addComponent(linkButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(speciesList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scoreLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(saveGraphics)
                            .addComponent(outlineComparisonLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(unknownSpecies)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(previousButton1)
                            .addComponent(nextButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(knownSpecies)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(previousButton)
                            .addComponent(nextButton))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outlineDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {knownSpecies, unknownSpecies});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void linkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkButtonActionPerformed
    
     try {
           // Lookup the javax.jnlp.BasicService object
           BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
            try {
                // Invoke the showDocument method
                //if(!bs.showDocument(new URL("http://loco2.biosci.arizona.edu/imagedb/servlet/DBDisplayTaxon?tdisplay=0&taxon_name=" + ((String)this.speciesList.getSelectedItem()).replaceAll("\\s+","+"))))
                if(!bs.showDocument(new URL("http://google.com/search?q=" +((String)this.speciesList.getSelectedItem()).replaceAll("\\s+","+"))))
                        JOptionPane.showMessageDialog(this,"Web link could not be displayed.");
                System.out.println("http://google.com/search?q=" +((String)this.speciesList.getSelectedItem()).replaceAll("\\s+","+"));
            } catch (HeadlessException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
           return;
           
       } catch(UnavailableServiceException ue) {
           // Service is not supported
           return;
       }
    
    }//GEN-LAST:event_linkButtonActionPerformed
    
    private void saveGraphicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGraphicsActionPerformed
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File wFile = fc.getSelectedFile();
            
            String path = wFile.getPath();
            if(!Utils.getExtension(wFile).equals("gif"))
                        path += ".gif";
            
            int width = this.outlineDisplay.getWidth();
            int height = this.outlineDisplay.getHeight();
            
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bi.createGraphics();
            outlineDisplay.paint(g2);
            g2.dispose();
            
            if(!Outlines.renderBufferedImage(bi, "gif", path))
                JOptionPane.showMessageDialog(this,"Could not save image");
            else
                JOptionPane.showMessageDialog(this,"Image " + path + " saved.");
            
            
            /*
            int width = (int)Math.max(bluePanel.getWidth(), redPanel.getWidth());
        int height = bluePanel.getHeight() + redPanel.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                                                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        bluePanel.paint(g2);
        g2.translate(0, bluePanel.getHeight());
        redPanel.paint(g2);
        g2.dispose();
        try {
          ImageIO.write(image, "png", new File("twopanels.png"));
        }
             */
        }
        fc = null;
    }//GEN-LAST:event_saveGraphicsActionPerformed
    
    public void initDisplay() {
        String initTaxon = (String)speciesList.getSelectedItem();
        int index = speciesList.getSelectedIndex();
        if(initTaxon==null) {
            initTaxon = taxaSorted[0];
            index=0;
        }
        System.out.println("The taxon is '" + initTaxon + "' and the hash is null? " + (taxa==null));
        currentOutlines = (Vector)taxa.get(initTaxon);
        if(currentOutlines!=null) {
            currentOutline = (double[][])currentOutlines.elementAt(0);
            Outlines.superimposeCoordinates(unknowns[0], currentOutline);
            
            if(currentOutlines.size()>1) nextButton.setEnabled(true);
            if(unknowns.length>1) nextButton1.setEnabled(true);
            
            Polygon t1 = Outlines.toPolygon(unknowns[0],1000.00);
            Polygon t2 = Outlines.toPolygon(currentOutline,1000.00);
            
            outlineDisplay.setBorder(75);
            outlineDisplay.setFirstOutline(t1);
            outlineDisplay.setSecondOutline(t2);
            
            outlineDisplay.repaint();
            
            cnt1=0;
            cnt2=0;
            
            knownSpecies.setText(initTaxon);
            this.scoreLabel.setText("score: " + Double.toString(score[index]));
        }
           
        try {
           // Lookup the javax.jnlp.BasicService object
           BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
          linkButton.setEnabled(true);
       } catch(UnavailableServiceException ue) {
           // Service is not supported
           linkButton.setVisible(false);
        
       }
         
    
        
    }
    
    public void setOutlines() {
        System.out.println("The indices are (unknown,known): " + cnt1 + "," + cnt2);
        currentOutline = (double[][])currentOutlines.elementAt(cnt2);
        Outlines.superimposeCoordinates(unknowns[cnt1], currentOutline);
        Polygon t1 = Outlines.toPolygon(unknowns[cnt1],1000.00);
        Polygon t2 = Outlines.toPolygon(currentOutline,1000.00);
        
        outlineDisplay.setBorder(75);
        outlineDisplay.setFirstOutline(t1);
        outlineDisplay.setSecondOutline(t2);
        
        outlineDisplay.repaint();
    }
    
    private void setEnabled() {
        if(cnt1>0) previousButton1.setEnabled(true);
        if(cnt1<unknowns.length-1 && unknowns.length>1) nextButton1.setEnabled(true);
        if(cnt1<=0) previousButton1.setEnabled(false);
        if(cnt1>=unknowns.length-1) nextButton1.setEnabled(false);
        if(cnt2>0) previousButton.setEnabled(true);
        if(cnt2<currentOutlines.size()-1 && currentOutlines.size()>1) nextButton.setEnabled(true);
        if(cnt2<=0) previousButton.setEnabled(false);
        if(cnt2>=currentOutlines.size()-1) nextButton.setEnabled(false);
    }
    
    private void speciesListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speciesListActionPerformed
        initDisplay();
        System.out.println("Setting the species to " + (String)speciesList.getSelectedItem() );
    }//GEN-LAST:event_speciesListActionPerformed
    
    private void previousButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButton1ActionPerformed
        cnt1--; //unknown
        setOutlines();
        setEnabled();
    }//GEN-LAST:event_previousButton1ActionPerformed
    
    private void nextButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButton1ActionPerformed
        cnt1++; //unknown
        setOutlines();
        setEnabled();
        
    }//GEN-LAST:event_nextButton1ActionPerformed
    
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        
        cnt2++; //known
        setOutlines();
        setEnabled();
        
    }//GEN-LAST:event_nextButtonActionPerformed
    
    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        cnt2--; //known
        setOutlines();
        setEnabled();
    }//GEN-LAST:event_previousButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel knownSpecies;
    private javax.swing.JButton linkButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton nextButton1;
    private javax.swing.JLabel outlineComparisonLabel;
    public outline.outlineDrawer outlineDisplay;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton previousButton1;
    private javax.swing.JButton saveGraphics;
    private javax.swing.JLabel scoreLabel;
    private javax.swing.JLabel searchResultsLabel;
    private javax.swing.JComboBox speciesList;
    private javax.swing.JLabel unknownSpecies;
    // End of variables declaration//GEN-END:variables
    
}