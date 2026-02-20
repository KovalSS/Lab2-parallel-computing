import java.util.ArrayList;

public class RunTask {
    private static final int CHECK_MODULUS = 5;
    private static final int[] N = {1000, 5000, 10000, 15000};
    private static final int[] THREAD_COUNTS = {4, 8, 12};
    private static final Long SEED = 42L;
    private static final int MIN_ELEM = 0;
    private static final int MAX_ELEM = 10000000;
    private final ArrayList<int[][]> matrices = new ArrayList<>();
    public RunTask() {
        for (int n : N) {
            matrices.add(MatrixUtils.generateTestMatrix(n, n, MIN_ELEM, MAX_ELEM, SEED));
        }
    }
    private Result multiplesSequentially(int[][] matrix){
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

    private void multiplesWithThreads(int[][] matrix, int threadCount, Accumulator accumulator) {
        int n = matrix.length;
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            int threadIndex = i;
            threads[i] = new Thread(() -> createThreadTask(matrix, threadIndex, threadCount, accumulator));
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createThreadTask(int[][] matrix, int threadIndex, int threadCount, Accumulator accumulator) {
        int n = matrix.length;
        for (int i = threadIndex; i < n; i+=threadCount) {
            for (int elem : matrix[i]) {
                if (elem % CHECK_MODULUS == 0) {
                    accumulator.registerMultiple(elem);
                }
            }
        }
    }
    public void run() {
        for (int n : N) {
            System.out.println("===========================================");
            System.out.println("Розмір матриці: " + n + "x" + n);

            int[][] matrix = MatrixUtils.generateTestMatrix(n, n, MIN_ELEM, MAX_ELEM, SEED);

            long startTime = System.currentTimeMillis();
            Result resultSeq = multiplesSequentially(matrix);
            long endTime = System.currentTimeMillis();
            System.out.println("[Sequential] " + resultSeq + ", Time: " + (endTime - startTime) + " ms");

            for (int threads : THREAD_COUNTS) {
                System.out.println("--- Потоків: " + threads + " ---");

                Accumulator syncAcc = new SynchronizedAccumulator();
                startTime = System.currentTimeMillis();
                multiplesWithThreads(matrix, threads, syncAcc);
                endTime = System.currentTimeMillis();
                System.out.println("[Synchronized] " + syncAcc.getFinalResult() + ", Time: " + (endTime - startTime) + " ms");

                Accumulator atomicAcc = new AtomicAccumulator();
                startTime = System.currentTimeMillis();
                multiplesWithThreads(matrix, threads, atomicAcc);
                endTime = System.currentTimeMillis();
                System.out.println("[Atomic CAS]   " + atomicAcc.getFinalResult() + ", Time: " + (endTime - startTime) + " ms");
            }

            matrix = null;
            System.gc();
        }
    }
}
