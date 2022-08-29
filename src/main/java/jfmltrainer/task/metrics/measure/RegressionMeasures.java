package jfmltrainer.task.metrics.measure;

import lombok.Value;

@Value
public class RegressionMeasures extends Measures {
    Float MSE;
    Float RMSE;
    Float MAE;
    Float GMSE;

    public RegressionMeasures(String varName, Float MSE, Float RMSE, Float MAE, Float GMSE) {
        this.varName = varName;
        this.MSE = MSE;
        this.MAE = MAE;
        this.RMSE = RMSE;
        this.GMSE = GMSE;
    }
}
