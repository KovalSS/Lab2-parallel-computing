public class Result {
    private int count;
    private int maxMultiple;
    public Result(int count, int max_multiples){
        this.count = count;
        this.maxMultiple = max_multiples;
    }
    @Override
    public String toString() {
        return "Count: " + count + ", Max Multiple: " + maxMultiple;
    }
}