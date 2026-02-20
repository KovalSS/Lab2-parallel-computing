public interface Accumulator {
    void registerMultiple(int value);
    Result getFinalResult();
}
