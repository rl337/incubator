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
    
    private double mMinX;
    private double mMaxX;
    private double mMinY;
    private double mMaxY;
    
    private boolean mAutoRange;
    
    private static final Color[] smColors = new Color[] {Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.GREEN, Color.ORANGE, Color.CYAN, Color.LIGHT_GRAY};
    
    public Sketchpad(String title, int width, int height, double minX, double maxX, double minY, double maxY) {
        mFrame = new JFrame(title);
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mWidth = width;
        mHeight = height;
        
        mPanel = new DrawPanel(width, height);
        mFrame.getContentPane().add(mPanel, BorderLayout.CENTER);
        mFrame.pack();
        mFrame.setVisible(true);
        mColorIndex = 0;
        
        mMinX = minX;
        mMaxX = maxX;
        mMinY = minY;
        mMaxY = maxY;
        
        mAutoRange = false;
    }
    
    public Sketchpad(String title, int width, int height) {
        this(title, width, height, 0.0, (double) width, 0.0, (double) height);
        mAutoRange = true;
    }
    
    private Color nextColor() {
        Color result = smColors[mColorIndex];
        mColorIndex = (mColorIndex + 1) % smColors.length;
        
        return result;
    }
    
    private void autoSetRanges(Matrix x, Matrix y) {
        
        mMinX = x.getValue(0, 0);
        mMaxX = mMinX;
        for(int j = 0; j < x.getColumns(); j++) {
            for(int i = 0; i < x.getRows(); i++) {
                double v = x.getValue(i, j);
                if (mMinX > v) {
                    mMinX = v;
                }
                
                if (mMaxX < v) {
                    mMaxX = v;
                }
            }
        }
        
        mMinY = y.getValue(0, 0);
        mMaxY = mMinY;
        for(int i = 1; i < y.getRows(); i++) {
            double v = y.getValue(i, 0);
            if (mMinY > v) {
                mMinY = v;
            }
            
            if (mMaxY < v) {
                mMaxY = v;
            }
        }
        
    }
    
    public void plotScatterChart(Matrix x, Matrix y, ConditionalPlot condition) {
        if (y.getColumns() > 1) {
            throw new IllegalArgumentException("the range of a plot must be only a single column wide");
        }
        
        if (x.getRows() != y.getRows()) {
            throw new IllegalArgumentException("Both x and y values should have the same rows");
        }
        
        if (mAutoRange) {
            autoSetRanges(x, y);
            // The firt plot determines the ranges.
            mAutoRange = false;
        }
        
        int padding = 10;
        
        double yrange = mMaxY - mMinY;
        double xrange = mMaxX - mMinX;
        
        for(int j = 0; j < x.getColumns(); j++) {
            Graphics2D g = mPanel.getGraphics();
            g.setColor(nextColor());
            for(int i = 0; i < x.getRows(); i++) {
                double xi = x.getValue(i, j);
                double yi = y.getValue(i, 0);
                
                if (!condition.valid(i, j, xi, yi)) {
                    continue;
                }
                
                int xcoord = padding + (int) ((xi - mMinX) / xrange * (mWidth - 2*padding));
                int ycoord = padding + (mHeight - 2*padding) - (int) ((yi - mMinY) / yrange * (mHeight - 2*padding));
                
                g.drawOval(xcoord, ycoord, 5, 5);
            }
        }
        mPanel.refresh();
        mFrame.repaint();
        mFrame.invalidate();
    }
    
    public void plotScatterChart(Matrix x, Matrix y) {
        plotScatterChart(x, y, PLOT_ALWAYS);
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
        
        public void refresh() {
            mImage.flush();
            invalidate();
        }
    }
    
    public interface ConditionalPlot {
        boolean valid(int row, int col, double x, double y);
    }
    
    public static final ConditionalPlot PLOT_ALWAYS = new ConditionalPlot() {
        public boolean valid(int row, int col, double x, double y) {
            return true;
        }
    };
}
