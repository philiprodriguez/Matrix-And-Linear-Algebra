
//Runtimes given in this class assume an m*n matrix.
public class Matrix
{

  // O(m*n)
  public static double[][] getCopy(double[][] matrix)
  {
    double[][] copy = new double[matrix.length][matrix[0].length];
    for(int r = 0; r < matrix.length; r++)
    {
      for(int c = 0; c < matrix[r].length; c++)
      {
        copy[r][c] = matrix[r][c];
      }
    }
    return copy;
  }

  // O(m*n)
  public static double[][] getReducedEchelonForm(double[][] matrix)
  {
    double[][] reduced = getCopy(matrix);
    int startRow = 0;
    for(int c = 0; c < reduced[0].length; c++)
    {
      //Find the row with a non-zero thing to work with!
      int hit = -1;
      for(int r = startRow; r < reduced.length; r++)
      {
        if (Math.abs(reduced[r][c]) > 0)
        {
          hit = r;
          break;
        }
      }

      //Did we find a non-zero row for this col?
      if (hit == -1)
      {
        //Nothing to work with in this column!
        continue;
      }

      //If we're here then we have a non-zero to work with and must cancel the
      //other rows! Also put the found row into the right place...
      swapRows(reduced, hit, startRow);
      hit = startRow;
      startRow++;

      //Cancel the other rows in this column...
      for(int r = 0; r < reduced.length; r++)
      {
        if (r != hit && Math.abs(reduced[r][c]) > 0)
        {
          //Cancel out row r...
          double fraction = -1.0*reduced[r][c]/reduced[hit][c];
          multiplyRow(reduced, hit, fraction);
          addRows(reduced, r, hit);
        }
      }

      //Make the hit row have a 1 in leftmost place...
      multiplyRow(reduced, hit, 1.0/reduced[hit][c]);
    }
    return reduced;
  }

  //Add into row1 the row row2. Basically, row1 = row1+row2. O(n)
  private static void addRows(double[][] matrix, int row1, int row2)
  {
    for(int c = 0; c < matrix[0].length; c++)
    {
      matrix[row1][c] += matrix[row2][c];
    }
  }

  //O(n)
  private static void multiplyRow(double[][] matrix, int row, double scalar)
  {
    for(int c = 0; c < matrix[0].length; c++)
    {
      matrix[row][c] *= scalar;
    }
  }

  //O(1)
  private static void swapRows(double[][] matrix, int row1, int row2)
  {
    double[] temp = matrix[row1];
    matrix[row1] = matrix[row2];
    matrix[row2] = temp;
  }

  public static void print(double[][] matrix, int precision)
  {
    int maxLength = 0;
    for(int r = 0; r < matrix.length; r++)
    {
      for(int c = 0; c < matrix[0].length; c++)
      {
        String s = String.format("%." + precision + "f ", matrix[r][c]);
        maxLength = Math.max(s.length(), maxLength);
      }
    }
    for(int r = 0; r < matrix.length; r++)
    {
      System.out.print("| ");
      for(int c = 0; c < matrix[0].length; c++)
      {
        String s = String.format("%." + precision + "f ", matrix[r][c]);
        while(s.length() < maxLength)
          s = " " + s;
        System.out.print(s);
      }
      System.out.println("|");
    }
  }

  public static void main(String[] args)
  {
    double[][] matrix = {
      {1, 0, -2, -1},
      {-2, 1, 6, 7},
      {3, -2, -5, -3}
    };
    print(matrix, 0);
    System.out.println();
    print(getReducedEchelonForm(matrix), 0);
  }
}
