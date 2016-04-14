package com.jainlabs;

/**
 * code
 */
public class OptimizationResultTuple {
    public double RHCResult;
    public double SAResult;
    public double GAResult;
    public double MIMICResult;
    public double RHCRuntime;
    public double SARuntime;
    public double GARuntime;
    public double MIMICRuntime;

    public OptimizationResultTuple(double RHCResult, double SAResult, double GAResult, double MIMICResult, double RHCRuntime, double SARuntime, double GARuntime, double MIMICRuntime) {
        this.RHCResult = RHCResult;
        this.SAResult = SAResult;
        this.GAResult = GAResult;
        this.MIMICResult = MIMICResult;
        this.RHCRuntime = RHCRuntime;
        this.SARuntime = SARuntime;
        this.GARuntime = GARuntime;
        this.MIMICRuntime = MIMICRuntime;
    }

    public OptimizationResultTuple(double RHCResult, double SAResult, double GAResult, double MIMICResult) {
        this(RHCResult, SAResult, GAResult, MIMICResult, 0, 0, 0, 0);
    }

    @Override
    public String toString() {
        return "OptimizationResultTuple{" +
                "RHCResult=" + RHCResult +
                ", SAResult=" + SAResult +
                ", GAResult=" + GAResult +
                ", MIMICResult=" + MIMICResult +
                ", RHCRuntime=" + RHCRuntime +
                ", SARuntime=" + SARuntime +
                ", GARuntime=" + GARuntime +
                ", MIMICRuntime=" + MIMICRuntime +
                '}';
    }

    public String[] toCSV(double var) {
        return String.format("%f#%f#%f#%f#%f#%f#%f#%f#%f", var, RHCResult, SAResult, GAResult, MIMICResult, RHCRuntime, SARuntime, GARuntime, MIMICRuntime).split("#");
    }
}
