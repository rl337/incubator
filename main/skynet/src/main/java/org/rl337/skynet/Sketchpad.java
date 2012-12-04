package org.rl337.skynet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.rl337.skynet.types.Matrix;


public class Sketchpad {
    private JFrame mFrame;
    
    private DrawPanel mPanel;
    
    public Sketchpad(String title, String firstPageName, int width, int height) {
        DrawPanel panel = new DrawPanel(firstPageName, width, height);
        init(title, panel, width, height);
    }
    
    public Sketchpad(String title, String firstPageName, int width, int height, double minX, double maxX, double minY, double maxY) {
        DrawPanel panel = new DrawPanel(firstPageName, width, height, minX, maxX, minY, maxY);
        init(title, panel, width, height);
    }
    
    private void init(String title, DrawPanel panel, int width, int height) {
        mPanel = panel;

        mFrame = new JFrame(title);
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mFrame.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
                mPanel.nextImage();
                refresh();
            }

            public void mouseEntered(MouseEvent arg0) { }
            public void mouseExited(MouseEvent arg0) { }
            public void mousePressed(MouseEvent arg0) { }
            public void mouseReleased(MouseEvent arg0) { }
        });
        
        mFrame.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent arg0) {
                switch(arg0.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        mPanel.prevImage();
                        break;
                    case KeyEvent.VK_RIGHT:
                        mPanel.nextImage();
                        break;
                    default:
                }
                refresh();
            }

            public void keyReleased(KeyEvent arg0) {
            }

            public void keyTyped(KeyEvent arg0) {

            }
            
        });
        
        mFrame.getContentPane().add(mPanel, BorderLayout.CENTER);
        mFrame.pack();
        mFrame.setVisible(true);
    }

    public void plotScatterChart(String name, Shape shape, Matrix x, Matrix y) {
        mPanel.plotScatterChart(name, shape, x, y);
        refresh();
    }
    
    public void plotScatterChart(String name, Shape shape, Matrix x, Matrix y, ConditionalPlot condition) {
        mPanel.plotScatterChart(name, shape, x, y, condition);
        refresh();
    }
    
    public void plotAsBitmaps(String name, int bmWidth, int bmHeight, int columns, int rows, Matrix m) {
        mPanel.plotAsBitmaps(name, bmWidth, bmHeight, columns, rows, m);
        refresh();
    }
    
    public void refresh() {
        mFrame.repaint();
        mFrame.invalidate();
    }

    private static class DrawPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private HashMap<String, SketchpadImage> mImageMap;
        private String mActiveImageName;
        private int mWidth;
        private int mHeight;
        
        private DrawPanel(String firstPageName, int width, int height, double minX, double maxX, double minY, double maxY) {
            init(new SketchpadImage(width, height, minX, maxX, minY, maxY), firstPageName, width, height);
        }
        
        private DrawPanel(String firstPageName, int width, int height) {
            init(new SketchpadImage(width, height), firstPageName, width, height);
        }
        
        public void plotScatterChart(String name, Shape shape, Matrix x, Matrix y) {
            setActiveImage(name);
            SketchpadImage image = mImageMap.get(name);
            image.plotScatterChart(shape, x, y);
            invalidate();
        }
        
        public void plotScatterChart(String name, Shape shape, Matrix x, Matrix y, ConditionalPlot condition) {
            setActiveImage(name);
            SketchpadImage image = mImageMap.get(name);
            image.plotScatterChart(shape, x, y, condition);
            invalidate();
        }
        
        public void plotAsBitmaps(String name, int bmWidth, int bmHeight, int columns, int rows, Matrix m) {
            setActiveImage(name);
            SketchpadImage image = mImageMap.get(name);
            image.plotBitmaps(bmWidth, bmHeight, columns, rows, m);
            invalidate();
        }
        
        private void init(SketchpadImage firstPage, String firstPageName, int width, int height) {
            mImageMap = new HashMap<String, Sketchpad.SketchpadImage>();
            mImageMap.put(firstPageName, firstPage);
            mActiveImageName = firstPageName;
            mWidth = width;
            mHeight = height;
            
            setPreferredSize(new Dimension(width, height));
        }
        
        public void setActiveImage(String imageName) {
            if (!mImageMap.containsKey(imageName)) {
                SketchpadImage image = new SketchpadImage(mWidth, mHeight);
                mImageMap.put(imageName, image);
            }
            
            mActiveImageName = imageName;
            invalidate();
        }
        
        public void nextImage() {
            String[] names = mImageMap.keySet().toArray(new String[]{});
            Arrays.sort(names);
            
            int index = 0;
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(mActiveImageName)) {
                    index = i;
                    break;
                }
            }
            
            index = (index + 1) % names.length;
            setActiveImage(names[index]);
        }
        
        public void prevImage() {
            String[] names = mImageMap.keySet().toArray(new String[]{});
            Arrays.sort(names);
            
            int index = 0;
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(mActiveImageName)) {
                    index = i;
                    break;
                }
            }
            
            index = (index + names.length - 1) % names.length;
            setActiveImage(names[index]);
        }

        public void paint(Graphics g) {
            BufferedImage img = mImageMap.get(mActiveImageName).getImage();
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), 0, 0, img.getWidth(), img.getHeight(), null);
            g.setColor(Color.YELLOW);
            g.drawString(mActiveImageName, 20, 20);
        }

    }
    
    public static class SketchpadImage {
        private static final Color[] smColors = new Color[] {
            new Color(0xFF, 0x40, 0x40), 
            new Color(0x60, 0x60, 0xFF), 
            new Color(0xFF, 0xFF, 0x10), 
            Color.MAGENTA, 
            new Color(0x10, 0xFF, 0x10), 
            Color.ORANGE, 
            Color.CYAN, 
            Color.LIGHT_GRAY
        };
        
        private static final Color[] sm256Monochrome;
        static {
            sm256Monochrome = new Color[256];
            for(int i = 0; i < sm256Monochrome.length; i++) {
                sm256Monochrome[i] = new Color(i, i, i);
            }
        }

        private BufferedImage mImage;
        private int mColorIndex;
        
        private int mWidth;
        private int mHeight;
        
        private double mMinX;
        private double mMaxX;
        private double mMinY;
        private double mMaxY;
        
        private boolean mAutoRange;
        
        public SketchpadImage(int width, int height, double minX, double maxX, double minY, double maxY) {
            mMinX = minX;
            mMaxX = maxX;
            mMinY = minY;
            mMaxY = maxY;
            mAutoRange = false;
            
            mHeight = height;
            mWidth = width;
            
            mImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = mImage.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }
        
        public SketchpadImage(int width, int height) {
            this(width, height, 0.0, (double) width, 0.0, (double) height);
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
        
        public void plotScatterChart(Shape shape, Matrix x, Matrix y, ConditionalPlot condition) {
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
            
            Graphics2D g = getGraphics();
            for(int j = 0; j < x.getColumns(); j++) {
                g.setColor(nextColor());
                for(int i = 0; i < x.getRows(); i++) {
                    double xi = x.getValue(i, j);
                    double yi = y.getValue(i, 0);
                    
                    if (!condition.valid(i, j, xi, yi)) {
                        continue;
                    }
                    
                    int xcoord = padding + (int) ((xi - mMinX) / xrange * (mWidth - 2*padding));
                    int ycoord = padding + (mHeight - 2*padding) - (int) ((yi - mMinY) / yrange * (mHeight - 2*padding));
                    
                    switch(shape) {
                        case X:
                            g.drawLine(xcoord - 2, ycoord - 2, xcoord + 2, ycoord + 2);
                            g.drawLine(xcoord + 2, ycoord - 2, xcoord - 2, ycoord + 2);
                            break;
                        case Square:
                            g.drawRect(xcoord - 2, ycoord - 2, 4, 4);
                            break;
                        case Circle:
                        default:
                            g.drawOval(xcoord - 2, ycoord - 2, 4, 4);
                            break;
                    }
                }
            }
            mImage.flush();
        }
        
        public void plotBitmaps(int bmWidth, int bmHeight, int columns, int rows, Matrix matrix) {
            int matrixColumns = bmWidth * bmHeight;
            if (matrix.getColumns() != matrixColumns) {
                throw new IllegalArgumentException("bitmap width x height should equal matrix columns");
            }
            
            if (rows > matrix.getRows()) {
                rows = matrix.getRows();
            }
            
            Graphics2D g = getGraphics();
            int m = 0;
            int nOffset = 0;
            for(int n = 0; n < rows; n++) {
                if (m++ > columns) {
                    m = 0;
                    nOffset++;
                }
                
                int xoffset = m * bmWidth + 2;
                int yoffset = nOffset * bmHeight + 2;
                
                for(int x = 0; x < bmWidth; x++) {
                    for(int y = 0; y < bmHeight; y++) {
                        double val = matrix.getValue(n, y * bmWidth + x);
                        if (val < 0 || val > 1.0) {
                            throw new IllegalArgumentException("bitmap values must be between 0 and 1");
                        }
                        
                        int colorIndex = (int) (val * 255);
                        g.setColor(sm256Monochrome[colorIndex]);
                        g.drawRect(xoffset + x, yoffset + y, 1, 1);
                    }
                }
            }
            
        }

        public void plotScatterChart(Shape shape, Matrix x, Matrix y) {
            plotScatterChart(shape, x, y, PLOT_ALWAYS);
        }
        
        public Graphics2D getGraphics() {
            return (Graphics2D) mImage.getGraphics();
        }

        public BufferedImage getImage() {
            return mImage;
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
    
    public static enum Shape {
        Circle,
        Square,
        X
    }

}
