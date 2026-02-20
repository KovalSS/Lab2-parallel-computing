public class SynchronizedAccumulator implements Accumulator{
    private int count = 0;
    private int maxMultiple = Integer.MIN_VALUE;

    @Override
    public synchronized void registerMultiple(int value) {
        count++;
        if (value > maxMultiple) {
            maxMultiple = value;
        }
    }

    @Override
    public synchronized Result getFinalResult() {
        return new Result(count, maxMultiple);
    }
}
