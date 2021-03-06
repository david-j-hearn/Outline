/*
 * textDisplayFrame.java
 *
 * Created on September 8, 2007, 11:33 PM
 */

package outline;

/**
 *
 * @author  projects
 */
public class textDisplayFrame extends javax.swing.JInternalFrame {
    
    /** Creates new form textDisplayFrame */
    public textDisplayFrame(String title, String filler) {
        super(title, 
              false, //resizable
              true, //closable
              false, //maximizable
              true);//iconifiablesuper(title);
        initComponents();
        focalText.setText(filler);
        focalText.setEditable(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        focalTextScrollPane = new javax.swing.JScrollPane();
        focalText = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        focalText.setColumns(20);
        focalText.setRows(5);
        focalTextScrollPane.setViewportView(focalText);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(focalTextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(focalTextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea focalText;
    private javax.swing.JScrollPane focalTextScrollPane;
    // End of variables declaration//GEN-END:variables
    
}
