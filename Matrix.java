import java.util.*;

//Runtimes given in this class assume an m*n matrix.
public class Matrix
{
  public static double epsilon = 1e-9;

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

  // O(m*n^2)
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
        if (!doubleEquals(reduced[r][c], 0.0))
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
        if (r != hit && !doubleEquals(reduced[r][c], 0.0))
        {
          //Cancel out row r...
          double fraction = -1.0*reduced[r][c]/reduced[hit][c];

          //Save row to prevent lossy multiplication from ruining it
          double[] rowBackup = Arrays.copyOf(reduced[hit], reduced[hit].length);

          multiplyRow(reduced, hit, fraction);
          addRows(reduced, r, hit);

          //Absolutely enforce that there is now a 0 in place at row r!
          //Without this, future calculations could propogate terrible errors
          reduced[r][c] = 0.0;

          //Restore row...
          reduced[hit] = rowBackup;
        }
      }

      //Make the hit row have a 1 in leftmost place...
      multiplyRow(reduced, hit, 1.0/reduced[hit][c]);
    }
    return reduced;
  }

  public static double[][] getProduct(double[][] matA, double[][] matB)
  {
    if (matA[0].length != matB.length)
      throw new IllegalArgumentException("Invalid matrix sizes for matrix multiplication!");
    double[][] result = new double[matA.length][matB[0].length];

    for(int r = 0; r < result.length; r++)
    {
      for(int c = 0; c < result[r].length; c++)
      {
        double ans = 0;

        for(int i = 0; i < matB.length; i++)
        {
          ans += matA[r][i]*matB[i][c];
        }

        result[r][c] = ans;
      }
    }

    return result;
  }

  //O(m*n)
  public static double[][] getTranspose(double[][] matrix)
  {
    double[][] copy = new double[matrix[0].length][matrix.length];
    for(int r = 0; r < matrix.length; r++)
    {
      for(int c = 0; c < matrix[r].length; c++)
      {
        copy[c][r] = matrix[r][c];
      }
    }
    return copy;
  }

  public static double[][] getConcatenation(double[][] matA, double[][] matB)
  {
    if (matA.length != matB.length)
      throw new IllegalArgumentException("Matricies must be of same row count for concatenation!");
    double[][] result = new double[matA.length][matA[0].length+matB[0].length];
    for(int r = 0; r < matA.length; r++)
    {
      for(int c = 0; c < matA[0].length; c++)
      {
        result[r][c] = matA[r][c];
      }
    }
    for(int r = 0; r < matB.length; r++)
    {
      for(int c = 0; c < matB[0].length; c++)
      {
        result[r][c+matA[0].length] = matB[r][c];
      }
    }
    return result;
  }

  private static boolean doubleEquals(double d1, double d2)
  {
    return Math.abs(d2-d1) <= epsilon;
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
        double printVal = matrix[r][c];

        //Don't print negative zeros! Yeah, negative zeros are a thing!
        if (doubleEquals(printVal, 0.0))
          printVal = 0.0;

        String s = String.format("%." + precision + "f ", printVal);
        while(s.length() < maxLength)
          s = " " + s;
        System.out.print(s);
      }
      System.out.println("|");
    }
  }

  // O(m*n^2)
  public static boolean[] getPivotColumnInfo(double[][] matrix)
  {
    boolean[] pivot = new boolean[matrix[0].length];
    double[][] reduced = getReducedEchelonForm(matrix);
    for(int r = 0; r < reduced.length; r++)
    {
      for (int c = 0; c < reduced[0].length; c++)
      {
        //If this is the first non-zero element, then it is the pivot!
        if (!doubleEquals(reduced[r][c], 0.0))
        {
          pivot[c] = true;
          break;
        }
      }
    }
    return pivot;
  }

  //O(m*n^2)
  public static boolean isConsistent(double[][] augmentedMatrix)
  {
    //Consistent iff not a pivot in rightmost column
    return !getPivotColumnInfo(augmentedMatrix)[augmentedMatrix[0].length-1];
  }

  public static boolean hasSolution(double[][] augmentedMatrix)
  {
    return isConsistent(augmentedMatrix);
  }

  // O(m*n^2)
  public static boolean hasFreeVariable(double[][] augmentedMatrix)
  {
    //Has free variable if there is a solution and there exists a non-pivot
    //column besides last
    if (!hasSolution(augmentedMatrix))
      return false;
    boolean[] pivotColumn = getPivotColumnInfo(augmentedMatrix);
    for(int c = 0; c < augmentedMatrix[0].length-1; c++)
    {
      if (!pivotColumn[c])
        return true;
    }
    return false;
  }

  public static boolean hasUniqueSolution(double[][] augmentedMatrix)
  {
    //Has unique solution iff no free variables and has solution!
    return !hasFreeVariable(augmentedMatrix) && hasSolution(augmentedMatrix);
  }

  //Return true iff the column vectors are linearly independent
  public static boolean columnsAreLinearlyIndependent(double[][] matrix)
  {
    //Linearly independent iff Ax=0 has only the trivial solution
    double[][] augmentedMatrix = new double[matrix.length][matrix[0].length+1];
    for(int r = 0; r < matrix.length; r++)
    {
      for(int c = 0; c < matrix[0].length; c++)
      {
        augmentedMatrix[r][c] = matrix[r][c];
      }
    }

    return hasUniqueSolution(augmentedMatrix);
  }

  // O(m*n); Returns true iff the matrix is an identity matrix
  public static boolean isIdentity(double[][] matrix)
  {
    if (matrix.length != matrix[0].length)
      return false;
    for(int r = 0; r < matrix.length; r++)
    {
      for(int c = 0; c < matrix[r].length; c++)
      {
        if (r == c && !doubleEquals(matrix[r][c], 1.0))
          return false;
        if (r != c && !doubleEquals(matrix[r][c], 0.0))
          return false;
      }
    }
    return true;
  }

  public static double[][] getIdentity(int size)
  {
    double[][] identity = new double[size][size];
    for(int rc = 0; rc < size; rc++)
      identity[rc][rc] = 1.0;
    return identity;
  }

  public static double[][] getInverse(double[][] matrix)
  {
    if (!isIdentity(getReducedEchelonForm(matrix)))
      throw new IllegalArgumentException("Matrix is not invertible! (A matrix must be row reducible to the identity matrix to be invertible)");

    double[][] multiAugmented = getConcatenation(matrix, getIdentity(matrix.length));
    double[][] reduced = getReducedEchelonForm(multiAugmented);
    double[][] answer = new double[matrix.length][matrix[0].length];
    for(int r = 0; r < answer.length; r++)
    {
      for(int c = 0; c < answer[r].length; c++)
      {
        answer[r][c] = reduced[r][c+matrix[0].length];
      }
    }
    return answer;
  }

  //column vectors span all of R^m iff matrix has pivot in every row.
  //public static boolean columnsSpanRm

  public static void main(String[] args)
  {
    Scanner scan = new Scanner(System.in);
    System.out.println("Enter the dimensions of the matrix:");
    int rows = scan.nextInt();
    int cols = scan.nextInt();
    System.out.println("Enter the precision");
    int precision = scan.nextInt();
    double[][] matrix = new double[rows][cols];
    for(int r = 0 ; r < rows; r++)
    {
      for(int c = 0; c < cols; c++)
      {
        matrix[r][c] = scan.nextDouble();
      }
    }

    print(getReducedEchelonForm(matrix), precision);
  }
}
