package org.rl337.skynet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.rl337.skynet.types.Matrix;


public class Sketchpad {
    private JFrame mFrame;
    private DrawPanel mPanel;
    private int mWidth;
    private int mHeight;
    private int mColorIndex;
    
    private static final Color[] smColors = new Color[] {Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.GREEN, Color.ORANGE, Color.CYAN, Color.LIGHT_GRAY};
    
    public Sketchpad(String title, int width, int height) {
        mFrame = new JFrame(title);
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mWidth = width;
        mHeight = height;
        
        mPanel = new DrawPanel(width, height);
        mFrame.getContentPane().add(mPanel, BorderLayout.CENTER);
        mFrame.pack();
        mFrame.setVisible(true);
        mColorIndex = 0;
    }


    
    
    private Color nextColor() {
        Color result = smColors[mColorIndex];
        mColorIndex = (mColorIndex + 1) % smColors.length;
        
        return result;
    }
    
    public void plotScatterChart(Matrix x, Matrix y) {
        plotScatterChart(x, y, null);
    }
    
    public void plotScatterChart(Matrix x, Matrix y, Matrix z) {
        if (y.getColumns() > 1) {
            throw new IllegalArgumentException("the range of a plot must be only a single column wide");
        }
        
        if (x.getRows() != y.getRows()) {
            throw new IllegalArgumentException("Both x and y values should have the same rows");
        }
        
        if (z != null && (z.getColumns() > 1 || z.getRows() != x.getRows())) {
            throw new IllegalArgumentException("z matrix must be " + x.getRows() + "x1");
        }
        
        int padding = 10;
        
        double miny = y.getValue(0, 0);
        double maxy = miny;
        for(int i = 1; i < y.getRows(); i++) {
            double v = y.getValue(i, 0);
            if (miny > v) {
                miny = v;
            }
            
            if (maxy < v) {
                maxy = v;
            }
        }
        double yrange = maxy - miny;
        
        
        double minz = 0.0;
        double maxz = 0.0;
        double zrange = 0.0;

        if (z != null) {
            minz = z.getValue(0, 0);
            maxz = minz;
            for(int i = 1; i < z.getRows(); i++) {
                double v = z.getValue(i, 0);
                if (minz > v) {
                    minz = v;
                }
                
                if (maxz < v) {
                    maxz = v;
                }
            }
            zrange = maxz - minz;
        }
        
        for(int j = 0; j < x.getColumns(); j++) {
            double minx = x.getValue(0, j);
            double maxx = miny;
            for(int i = 1; i < x.getRows(); i++) {
                double v = x.getValue(i, j);
                if (minx > v) {
                    minx = v;
                }
                
                if (maxx < v) {
                    maxx = v;
                }
            }
            double xrange = maxx - minx;
            
            Graphics2D g = mPanel.getGraphics();
            if (z == null) {
                g.setColor(nextColor());
            }
            for(int i = 0; i < x.getRows(); i++) {
                double xi = x.getValue(i, j);
                double yi = y.getValue(i, 0);
                
                if (z != null) {
                    double zi = z.getValue(i, 0);
                    int color = (int) ((zi - minz) / zrange) * (smColors.length - 1);
                    g.setColor(smColors[color]);
                }
                
                int xcoord = padding + (int) ((xi - minx) / xrange * (mWidth - 2*padding));
                int ycoord = padding + (mHeight - 2*padding) - (int) ((yi - miny) / yrange * (mHeight - 2*padding));
                
                g.drawOval(xcoord, ycoord, 5, 5);
            }
        }
        
        mPanel.invalidate();
    }

    private static class DrawPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private BufferedImage mImage;

        private DrawPanel(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            
            mImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = mImage.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }

        public void paint(Graphics g) {
            g.drawImage(mImage, 0, 0, mImage.getWidth(), mImage.getHeight(), 0, 0, mImage.getWidth(), mImage.getHeight(), null);
        }
        
        public Graphics2D getGraphics() {
            return (Graphics2D) mImage.getGraphics();
        }
    }
}
