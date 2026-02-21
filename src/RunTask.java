import java.util.ArrayList;

public class RunTask {
    private static final int CHECK_MODULUS = 5;
    private static final int[] N = {1000, 5000, 10000, 15000};
    private static final int[] THREAD_COUNTS = {4, 8, 12};
    private static final Long SEED = 42L;
    private static final int MIN_ELEM = 0;
    private static final int MAX_ELEM = 10000000;
    private static final int RUNS = 5;
    public RunTask() {
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
    private void warmUp() {
        System.out.println("--- JVM warm-up start ---");
        int[][] dummyMatrix = MatrixUtils.generateTestMatrix(5000, 5000, MIN_ELEM, MAX_ELEM, SEED);
        for (int i = 0; i < 5; i++) {
            multiplesSequentially(dummyMatrix);
            multiplesWithThreads(dummyMatrix, 4, new SynchronizedAccumulator());
            multiplesWithThreads(dummyMatrix, 4, new AtomicAccumulator());
        }
        dummyMatrix = null;
        System.gc();
        System.out.println("--- Warming up is complete. JVM optimized. ---\n");
    }
    public void run() {
        warmUp();
        for (int n : N) {
            System.out.println("===========================================");
            System.out.println("Matrix size: " + n + "x" + n);

            int[][] matrix = MatrixUtils.generateTestMatrix(n, n, MIN_ELEM, MAX_ELEM, SEED);
            long totalTime = 0;
            long startTime, endTime;
            Result resultSeq = null;
            for (int i = 0; i < RUNS; i++) {
                startTime = System.nanoTime();
                resultSeq = multiplesSequentially(matrix);
                endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }
            System.out.println("[Sequential] " + resultSeq + ", Time: " + totalTime/1_000_000/RUNS + " ms");


            for (int threads : THREAD_COUNTS) {
                System.out.println("--- Threads: " + threads + " ---");

                Accumulator syncAcc = null;
                totalTime = 0;
                for (int i = 0; i < RUNS; i++) {
                    syncAcc = new SynchronizedAccumulator();
                    startTime = System.nanoTime();
                    multiplesWithThreads(matrix, threads, syncAcc);
                    endTime = System.nanoTime();
                    totalTime += (endTime - startTime);
                }
                System.out.println("[Synchronized] " + syncAcc.getFinalResult() + ", Time: " + totalTime/1_000_000/RUNS + " ms");

                Accumulator atomicAcc = null;
                totalTime = 0;
                for (int i = 0; i < RUNS; i++) {
                    atomicAcc = new AtomicAccumulator();
                    startTime = System.nanoTime();
                    multiplesWithThreads(matrix, threads, atomicAcc);
                    endTime = System.nanoTime();
                    totalTime += (endTime - startTime);
                }
                System.out.println("[Atomic CAS]   " + atomicAcc.getFinalResult() + ", Time: " + totalTime/1_000_000/RUNS + " ms");
            }

            matrix = null;
            System.gc();
        }
    }
}
