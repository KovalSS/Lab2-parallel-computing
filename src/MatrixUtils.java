import java.util.Random;

public class MatrixUtils {
    public static int[][] generateTestMatrix(int rows, int cols, int min, int max, Long seed) {
        Random random = (seed != null)? new Random(seed) : new Random();
        return generate(rows, cols, min, max, random);
    }

    private static int[][] generate(int rows, int cols, int min, int max, Random random) {
        int[][] matrix = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(max - min) + min + 1;
            }
        }

        return matrix;
    }
}