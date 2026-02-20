public class RunTask {
    private static final int CHECK_MODULUS = 5;
    private static final int[] N = {1000, 5000, 10000, 15000};
    private static final int[] THREAD_COUNTS = {2, 4, 8, 12, 24 };
    private static final Long SEED = 42L;
    private static final int MIN_ELEM = 0;
    private static final int MAX_ELEM = 100000;

    private Result countMultiplesSequentially(int[][] matrix){
        int count = 0;
        int maxMultiple = Integer.MIN_VALUE;
        for (int[] row : matrix) {
            for (int elem : row) {
                if (elem % CHECK_MODULUS == 0) {
                    count++;
                    if (elem > maxMultiple) {
                        maxMultiple = elem;
                    }
                }
            }
        }
        return new Result(count, maxMultiple);
    }

    public void run(){
        for (int n : N) {
            int[][] matrix = MatrixUtils.generateTestMatrix(n, n, MIN_ELEM, MAX_ELEM, SEED);
            long startTime = System.currentTimeMillis();
            Result result = countMultiplesSequentially(matrix);
            long endTime = System.currentTimeMillis();
            System.out.println("N: " + n + ", " + result + ", Time: " + (endTime - startTime) + " ms");
        }
    }
}
class Result {
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