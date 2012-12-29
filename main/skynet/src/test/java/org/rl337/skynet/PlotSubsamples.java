package org.rl337.skynet;

import java.io.File;

import org.rl337.math.Sketchpad;
import org.rl337.math.Sketchpad.Shape;
import org.rl337.skynet.datasets.DelimitedTextFileDataSet;
import org.rl337.skynet.types.Log;
import org.rl337.math.types.Matrix;

public class PlotSubsamples {
    public static void main(String[] args) throws Exception {
        Sketchpad pad = new Sketchpad("Influence Plots", "Global24 vs Influence-20111102-20121031", 640, 480);
        
        DataSet global24 = new DelimitedTextFileDataSet(new File("/tmp/subsample-influence.influence__global24_twitterid.tsv.val"), "\t");
        Matrix global24_y = global24.getAll();
        Matrix global24_x = Matrix.zeros(global24_y.getRows(), 1);
        for(int i = 0; i < global24_x.getRows(); i++) {
            global24_x.setValue(i, 0, i);
        }
        
        DataSet newinfl = new DelimitedTextFileDataSet(new File("/tmp/subsample-influence-20111102-20121031-i49.val"), "\t");
        Matrix newinfl_y = newinfl.getAll();
        Matrix newinfl_x = Matrix.zeros(newinfl_y.getRows(), 1);
        for(int i = 0; i < newinfl_x.getRows(); i++) {
            newinfl_x.setValue(i, 0, i);
        }
        
        pad.plotScatterChart("Global24 vs Influence-20111102-20121031", Shape.X, newinfl_x, Log.RealLog.evaluate(newinfl_y));
        pad.plotScatterChart("Global24 vs Influence-20111102-20121031", Shape.X, global24_x, Log.RealLog.evaluate(global24_y));
    }

}
