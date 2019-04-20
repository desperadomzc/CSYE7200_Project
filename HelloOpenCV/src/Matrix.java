import java.util.Arrays;

public class Matrix {
    private final int StartPoint = 2;
    private int rowNum;
    private int colNum;
    private int[][] matrix;

    public Matrix(int rowNum, int colNum) throws Exception {
        if (rowNum <= 0 || colNum <= 0) throw new Exception("ZeroColOrRowNumberException");
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    private void writeIntoCsv(String path) {

    }

    private void setNode(int x, int y, int val) throws Exception {
        if (x >= this.rowNum || x < 0)
            throw new Exception("RowNumberOutOfRangeException:" + "[" + 0 + "," + rowNum + "-1]");
        if (y >= this.rowNum || y < 0)
            throw new Exception("RowNumberOutOfRangeException:" + "[" + 0 + "," + colNum + "-1]");
        if (val == 2)
            this.matrix[x][y] = val;
    }

    private int getNode(int x, int y) throws Exception {
        if (x >= this.rowNum || x < 0)
            throw new Exception("RowNumberOutOfRangeException:" + "[" + 0 + "," + rowNum + "-1]");
        if (y >= this.rowNum || y < 0)
            throw new Exception("RowNumberOutOfRangeException:" + "[" + 0 + "," + colNum + "-1]");
        return this.matrix[x][y];
    }

    @Override
    public String toString() {
        return "Matrix{" +
                "matrix=" + Arrays.toString(matrix) +
                '}';
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    private int size() {
        return rowNum * colNum;
    }


}
