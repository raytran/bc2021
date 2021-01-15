package dlmoreram011521_02.eccontrollers;

public class PIDDelta {
    private double kP;
    private double kI;
    private double kD;
    private final double discount = 0.9;
    private double target;
    private double prevError;
    private double sigE;
    private double value;

    public PIDDelta(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public void update(double newValue) {
        double error = target - newValue;
        sigE = discount * sigE + error;
        double deltaE = error - prevError;
        prevError = error;
        value = kP * error + kI * sigE + kD * deltaE;
    }

    public void setTarget(double target) { this.target = target; }

    public double getValue() { return value; }
}
