package org.rl337.skynet;

import java.io.File;

import org.rl337.math.Sketchpad;
import org.rl337.math.Sketchpad.Shape;
import org.rl337.skynet.datasets.DelimitedTextFileDataSet;
import org.rl337.skynet.types.Log;
import org.rl337.math.types.Matrix;

public class PlotSubsamples {
    public static void main(String[] args) throws Exception {
        String plotName = "Global24 vs Influence-20111102-20121031-rescale";
        Sketchpad pad = new Sketchpad("Influence Plots", plotName, 640, 480);
        
        File inflDir = new File("/Users/rlee/infl");
        
        DataSet global24 = new DelimitedTextFileDataSet(new File(inflDir, "subsample-influence.influence__global24_twitterid.tsv.val"), "\t");
        Matrix global24_y = global24.getAll(); 
        Matrix global24_x = Matrix.zeros(global24_y.getRows(), 1);
        for(int i = 0; i < global24_x.getRows(); i++) {
            global24_x.setValue(i, 0, i);
        }
        
        DataSet newinfl = new DelimitedTextFileDataSet(new File(inflDir, "subsample-influence-20111102-20121031-i49.val"), "\t");
        Matrix newinfl_y = newinfl.getAll();
        Matrix newinfl_x = Matrix.zeros(newinfl_y.getRows(), 1);
        for(int i = 0; i < newinfl_x.getRows(); i++) {
            newinfl_x.setValue(i, 0, i);
        }
        
        DataSet newinfl_rescale = new DelimitedTextFileDataSet(new File(inflDir, "subsample-influence-20111102-20121031-i49-rescaled.val"), "\t");
        Matrix newinfl_y_rescale = newinfl_rescale.getAll();
        Matrix newinfl_x_rescale = Matrix.zeros(newinfl_y_rescale.getRows(), 1);
        for(int i = 0; i < newinfl_x_rescale.getRows(); i++) {
            newinfl_x_rescale.setValue(i, 0, i);
        }
        
        
        Matrix rescale_to = global24_y;
        Matrix oneoverx_x = Matrix.zeros(rescale_to.getRows(), 1);
        Matrix oneoverx_y = Matrix.zeros(rescale_to.getRows(), 1);
        double val_max = rescale_to.getValue(0, 0);
        double val_min = rescale_to.getValue(rescale_to.getRows() - 1, 0);
        double val_delta = val_max - val_min;
        int rows = oneoverx_x.getRows();
        for(int i = 0; i < rows; i++) {
            oneoverx_x.setValue(i, 0, i);
            double y = 1.0 / (i + 1);
            oneoverx_y.setValue(i, 0, y * val_delta + val_min);
        }
        
        //pad.plotScatterChart(plotName, Shape.X, newinfl_x, Log.RealLog.evaluate(newinfl_y));
        pad.plotScatterChart(plotName, Shape.X, oneoverx_x, Log.RealLog.evaluate(oneoverx_y));
        pad.plotScatterChart(plotName, Shape.X, global24_x, Log.RealLog.evaluate(global24_y));
        pad.plotScatterChart(plotName, Shape.X, newinfl_x_rescale, Log.RealLog.evaluate(newinfl_y_rescale));
    }

}
