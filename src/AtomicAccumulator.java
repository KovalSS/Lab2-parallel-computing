import java.util.concurrent.atomic.AtomicInteger;

public class AtomicAccumulator implements Accumulator {
    AtomicInteger counter = new AtomicInteger(0);
    AtomicInteger maxMultiple = new AtomicInteger(Integer.MIN_VALUE);

    @Override
    public void registerMultiple(int value) {
        updateCounter();
        updateMax(value);
    }

    private void updateMax(int value){
        int currentMax;
        do {
            currentMax = maxMultiple.get();
            if (value <= currentMax) {
                break;
            }
        } while (!maxMultiple.compareAndSet(currentMax, value));
    }

    private void updateCounter(){
        int currentCounter;
        do {
            currentCounter = counter.get();
        } while (!counter.compareAndSet(currentCounter, currentCounter + 1));
    }

    @Override
    public Result getFinalResult() {
        return new Result(counter.get(), maxMultiple.get());
    }
}
