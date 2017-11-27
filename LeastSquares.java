import java.util.*;

/*
    This is me just messing around with my own Matrix class...
*/

public class LeastSquares
{
  public static final Scanner scan = new Scanner(System.in);
  public static void main(String[] args)
  {
    polynomialWithLnSin();
  }

  public static void polynomial()
  {
    System.out.println("Enter the max polynomial degree:");
    int maxDegree = scan.nextInt();
    System.out.println("Enter the number of 2D data points you have:");
    int numDataPoints = scan.nextInt();
    double[][] matA = new double[numDataPoints][maxDegree+1];

    System.out.println("Enter your x y data for all points:");
    double[] x = new double[numDataPoints];
    double[][] y = new double[numDataPoints][1];
    for(int i = 0; i < numDataPoints; i++)
    {
      x[i]=scan.nextInt();
      y[i][0]=scan.nextInt();
    }

    for(int r = 0; r < matA.length; r++)
    {
      for(int c = 0; c < matA[0].length; c++)
      {
        matA[r][c] = Math.pow(x[r], c);
      }
    }

    double[][] at = Matrix.getTranspose(matA);
    double[][] ata = Matrix.getProduct(at, matA);
    double[][] atb = Matrix.getProduct(at, y);
    double[][] ataatb = Matrix.getConcatenation(ata, atb);
    double[][] rref = Matrix.getReducedEchelonForm(ataatb);
    Matrix.print(rref, 5);
  }

  public static void polynomialWithLnSin()
  {
    System.out.println("Enter the max polynomial degree:");
    int maxDegree = scan.nextInt();
    System.out.println("Enter the number of 2D data points you have:");
    int numDataPoints = scan.nextInt();
    double[][] matA = new double[numDataPoints][maxDegree+3];

    System.out.println("Enter your x y data for all points:");
    double[] x = new double[numDataPoints];
    double[][] y = new double[numDataPoints][1];
    for(int i = 0; i < numDataPoints; i++)
    {
      x[i]=scan.nextInt();
      y[i][0]=scan.nextInt();
    }

    for(int r = 0; r < matA.length; r++)
    {
      for(int c = 0; c < matA[0].length-2; c++)
      {
        matA[r][c] = Math.pow(x[r], c);
      }
      matA[r][matA[0].length-2] = Math.log(x[r]);
      matA[r][matA[0].length-1] = Math.sin(x[r]);
    }

    double[][] at = Matrix.getTranspose(matA);
    double[][] ata = Matrix.getProduct(at, matA);
    double[][] atb = Matrix.getProduct(at, y);
    double[][] ataatb = Matrix.getConcatenation(ata, atb);
    double[][] rref = Matrix.getReducedEchelonForm(ataatb);
    Matrix.print(rref, 5);
  }
}
