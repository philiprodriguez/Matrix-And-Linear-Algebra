
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
          multiplyRow(reduced, hit, fraction);
          addRows(reduced, r, hit);
        }
      }

      //Make the hit row have a 1 in leftmost place...
      multiplyRow(reduced, hit, 1.0/reduced[hit][c]);
    }
    return reduced;
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
        String s = String.format("%." + precision + "f ", matrix[r][c]);
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
  public static boolean isLinearlyIndependent(double[][] matrix)
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

  public static void main(String[] args)
  {
    double[][] matrix1 = {
      {1, 0, -2, -1},
      {-2, 1, 6, 7},
      {3, -2, -5, -3}
    };

    double[][] matrix2 = {
      {1, -3, 2, 6},
      {0, 1, -4, -7},
      {3, -5, -9, -9}
    };

    double[][] matrix3 = {
      {1, -3, 2, 6},
      {0, 1, -4, -7},
      {3, -5, -9, -9},
      {13, -2, -91, -19},
      {3, -5, -9, -9}
    };

    print(getReducedEchelonForm(matrix1), 0);
    print(getReducedEchelonForm(matrix2), 0);
    print(getReducedEchelonForm(matrix3), 0);
    System.out.println(isConsistent(matrix1));
    System.out.println(isConsistent(matrix2));
    System.out.println(isConsistent(matrix3));
    System.out.println(hasFreeVariable(matrix1));
    System.out.println(hasFreeVariable(matrix2));
    System.out.println(hasFreeVariable(matrix3));
    System.out.println(hasUniqueSolution(matrix1));
    System.out.println(hasUniqueSolution(matrix2));
    System.out.println(hasUniqueSolution(matrix3));
    System.out.println(isLinearlyIndependent(matrix1));
    System.out.println(isLinearlyIndependent(matrix2));
    System.out.println(isLinearlyIndependent(matrix3));
  }
}
