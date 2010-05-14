package aeminiumruntime;

/* interface for base hint */
public interface Hint {}

/* task contains loops */
interface Loops extends Hint {}

/* task contains recursion */
interface Recursion extends Hint {}

/* estimated virtual execution steps */
interface Steps extends Hint {
    public long getStepCount();
}