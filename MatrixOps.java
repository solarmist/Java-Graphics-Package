import java.text.DecimalFormat;


/**
 * This class provides a set of standard matrix operations.
 *
 * @version 5-Feb-2005
 */
public class MatrixOps
{

	/**
	 * Intializes and returns an indentity matrtix
	 */
	public static double [] newIdentity()
	{
		double [] array = new double[16];
		array[0] = 1.0;
		array[5] =1.0;
		array[10] = 1.0;
		array[15] = 1.0;
		return array;
	} // end newIdentity

	/**
	 * Calulates and returns m1 * m2 in column major order.
	 */
	public static double [] multMat(double [] m1, double [] m2)
	{
		double [] prod = new double[16];
		// Column major order
		prod[0] = m1[0] * m2[0] + m1[4] * m2[1] + m1[8] * m2[2] + m1[12] * m2[3 ];
		prod[1] = m1[1] * m2[0] + m1[5] * m2[1] + m1[9] * m2[2] + m1[13] * m2[3 ];
		prod[2] = m1[2] * m2[0] + m1[6] * m2[1] + m1[10] * m2[2] + m1[14] * m2[3 ];
		prod[3] = m1[3] * m2[0] + m1[7] * m2[1] + m1[11] * m2[2] + m1[15] * m2[3 ];

		prod[4] = m1[0] * m2[4] + m1[4] * m2[5] + m1[8] * m2[6] + m1[12] * m2[7 ];
		prod[5] = m1[1] * m2[4] + m1[5] * m2[5] + m1[9] * m2[6] + m1[13] * m2[7 ];
		prod[6] = m1[2] * m2[4] + m1[6] * m2[5] + m1[10] * m2[6] + m1[14] * m2[7 ];
		prod[7] = m1[3] * m2[4] + m1[7] * m2[5] + m1[11] * m2[6] + m1[15] * m2[7 ];

		prod[8] = m1[0] * m2[8] + m1[4] * m2[9] + m1[8] * m2[10] + m1[12] * m2[11 ];
		prod[9] = m1[1] * m2[8] + m1[5] * m2[9] + m1[9] * m2[10] + m1[13] * m2[11 ];
		prod[10] = m1[2] * m2[8] + m1[6] * m2[9] + m1[10] * m2[10] + m1[14] * m2[11 ];
		prod[11] = m1[3] * m2[8] + m1[7] * m2[9] + m1[11] * m2[10] + m1[15] * m2[11 ];

		prod[12] = m1[0] * m2[12] + m1[4] * m2[13] + m1[8] * m2[14] + m1[12] * m2[15 ];
		prod[13] = m1[1] * m2[12] + m1[5] * m2[13] + m1[9] * m2[14] + m1[13] * m2[15 ];
		prod[14] = m1[2] * m2[12] + m1[6] * m2[13] + m1[10] * m2[14] + m1[14] * m2[15];
		prod[15] = m1[3] * m2[12] + m1[7] * m2[13] + m1[11] * m2[14] + m1[15] * m2[15 ];
		/* Row major order
		prod[0] = m1[0] * m2[0] + m1[1] * m2[4] + m1[2] * m2[8] + m1[3] * m2[12 ];
		prod[1] = m1[0] * m2[1] + m1[1] * m2[5] + m1[2] * m2[9] + m1[3] * m2[13 ];
		prod[2] = m1[0] * m2[2] + m1[1] * m2[6] + m1[2] * m2[10] + m1[3] * m2[14 ];
		prod[3] = m1[0] * m2[3] + m1[1] * m2[7] + m1[2] * m2[11] + m1[3] * m2[15 ];

		prod[4] = m1[4] * m2[0] + m1[5] * m2[4] + m1[6] * m2[8] + m1[7] * m2[12 ];
		prod[5] = m1[4] * m2[1] + m1[5] * m2[5] + m1[6] * m2[9] + m1[7] * m2[13 ];
		prod[6] = m1[4] * m2[2] + m1[5] * m2[6] + m1[6] * m2[10] + m1[7] * m2[14 ];
		prod[7] = m1[4] * m2[3] + m1[5] * m2[7] + m1[6] * m2[11] + m1[7] * m2[15 ];

		prod[8] = m1[8] * m2[0] + m1[9] * m2[4] + m1[10] * m2[8] + m1[11] * m2[12 ];
		prod[9] = m1[8] * m2[1] + m1[9] * m2[5] + m1[10] * m2[9] + m1[11] * m2[13 ];
		prod[10] = m1[8] * m2[2] + m1[9] * m2[6] + m1[10] * m2[10] + m1[11] * m2[14 ];
		prod[11] = m1[8] * m2[3] + m1[9] * m2[7] + m1[10] * m2[11] + m1[11] * m2[15 ];

		prod[12] = m1[12] * m2[0] + m1[13] * m2[4] + m1[14] * m2[8] + m1[15] * m2[12 ];
		prod[13] = m1[12] * m2[1] + m1[13] * m2[5] + m1[14] * m2[9] + m1[15] * m2[13 ];
		prod[14] = m1[12] * m2[2] + m1[13] * m2[6] + m1[14] * m2[10] + m1[15] * m2[14 ];
		prod[15] = m1[12] * m2[3] + m1[13] * m2[7] + m1[14] * m2[11] + m1[15] * m2[15 ];*/
		return prod;
	} // end method mulMat

	public static void showMat(double [] mat){
		System.out.println(mat[0]+"  "+mat[1]+"  "+mat[2]+"  "+mat[3]);
		System.out.println(mat[4]+"  "+mat[5]+"  "+mat[6]+"  "+mat[7]);
		System.out.println(mat[8]+"  "+mat[9]+"  "+mat[10]+"  "+mat[11]);
		System.out.println(mat[12]+"  "+mat[13]+"  "+mat[14]+"  "+mat[15]);
	} // end method showMat

	public static void showMatColOrder(double [] mat){
		DecimalFormat fmt = new DecimalFormat("####.##");
		System.out.println(fmt.format(mat[0])+"  "+fmt.format(mat[4])+"  "+fmt.format(mat[8])+"  "+fmt.format(mat[12]));
		System.out.println(fmt.format(mat[1])+"  "+fmt.format(mat[5])+"  "+fmt.format(mat[9])+"  "+fmt.format(mat[13]));
		System.out.println(fmt.format(mat[2])+"  "+fmt.format(mat[6])+"  "+fmt.format(mat[10])+"  "+fmt.format(mat[11]));
		System.out.println(fmt.format(mat[3])+"  "+fmt.format(mat[7])+"  "+fmt.format(mat[11])+"  "+fmt.format(mat[15]));
	} // end method showMatColOrder

    /**
     * Constructs and returns a perspective matrix for the current settings
     *
     * @param cam       the {@link Camera Camera} object with the appropriate
     *                  settings
     * @return <code>double[]</code> - the perspective matrix
     */
    public static double[] makePerspectiveMatrix (Camera cam) {
        // From the manual page for glFrustum()
        double left = cam.windowLeft, right = cam.windowRight, top = cam.windowRight,
            bottom = cam.windowBottom, near = cam.near, far = cam.far;
        double a = 2.0 * near, b = right - left, c = top - bottom,
            d = far - near;
        return new double[] {
                       a/b,            0.0,             0.0,  0.0,
                       0.0,            a/c,             0.0,  0.0,
            (right+left)/b, (top+bottom)/c,   -(far+near)/d, -1.0,
                       0.0,            0.0, -2.0*far*near/d,  0.0 };
    } // end method makePerspectiveMatrix

    /**
     * Creates a matrix for a rotation about the X axis given the sine and
     * cosine of the angle
     *
     * @param sin the sine of the directed rotation angle
     * @param cos the cosine of the directed rotation angle
     * @return <code>double[]</code> - the 4x4 rotation matrix
     */
    public static double[] makeXRotation (double sin, double cos) {
    // (this is in column-major order, but the columns are layed out
    // as rows)
    return new double[] {
            1.0,  0.0, 0.0, 0.0,
            0.0,  cos, sin, 0.0,
            0.0, -sin, cos, 0.0,
            0.0,  0.0, 0.0, 1.0 };
    } // end method makeXRotation

    /**
     * Creates a matrix for a rotation about the Y axis given the sine and
     * cosine of the angle
     *
     * @param sin the sine of the directed rotation angle
     * @param cos the cosine of the directed rotation angle
     * @return <code>double[]</code> - the 4x4 rotation matrix
     */
    public static double[] makeYRotation (double sin, double cos) {
    // (this is in column-major order, but the columns are layed out
    // as rows)
    return new double[] {
            cos, 0.0, -sin, 0.0,
            0.0, 1.0,  0.0, 0.0,
            sin, 0.0,  cos, 0.0,
            0.0, 0.0,  0.0, 1.0 };
    } // end method makeYRotation

    /**
     * Creates a matrix for a rotation about the Z axis given the sine and
     * cosine of the angle
     *
     * @param sin the sine of the directed rotation angle
     * @param cos the cosine of the directed rotation angle
     * @return <code>double[]</code> - the 4x4 rotation matrix
     */
    public static double[] makeZRotation (double sin, double cos) {
    // (this is in column-major order, but the columns are layed out as rows)
    return new double[] {
             cos, sin, 0.0, 0.0,
            -sin, cos, 0.0, 0.0,
             0.0, 0.0, 1.0, 0.0,
             0.0, 0.0, 0.0, 1.0 };
    } // end method makeZRotation

	public static double[][] convertToMatrix2D(double[] m)
	{
		// Assumes a square matrix in column major order
		int num = (int) Math.sqrt(m.length); // # of rows and columns in square matrix
		double[][] matrix = new double[num][num];

		// Column Major Order
		for(int col = 0; col < matrix.length; col++)
					for(int row = 0; row < matrix[col].length; row++)
				matrix[row][col] = m[ col*num + row];

		/* Row major order
		for(int row = 0; row < matrix.length; row++)
					for(int col = 0; col < matrix[row].length; col++)
				matrix[row][col] = m[ row*num + col];
		*/

		return matrix;
	} // end method convertToMatrix2D

	public static double[] convertToMatrix1D(double[][] m)
	{
		// Converts to Column Major Order
		int num = m.length; // # of rows and columns in square matrix
		double[] matrix = new double[ num * num ];

		// Column Major Order
		for(int row = 0; row < m.length; row++)
			for(int col = 0; col < m[row].length; col++)
				matrix[ (col * num) + row] = m[row][col];

		/* Row major order
		for(int row = 0; row < m.length; row++)
			for(int col = 0; col < m[row].length; col++)
				matrix[ (row * num) + col] = m[row][col];
		*/

		return matrix;
	} // end method convertToMatrix1D

	public static double[] inverseTranspose(double[] m) throws Exception
	{
		double[][] temp = convertToMatrix2D(m);
		double[][] result = transpose( inverse(temp) );
		return convertToMatrix1D(result);

	} // end method inverseTranspose


///////////////////////////////////////////////////////////////////////////////
// NOTE: The following methods were taken from the specified source.
// They have been slightly altered from float to double precision and to
// use the desired error handling.
// Also, the matrices in these methods are represented as a 2D array.
// The convertMatrix2D(double[] m) and convertMatrix1D(double[][] m) methods
// are provided to perform the necessary conversion between 1D arrays
// (in column major order) and 2D arrays.

/* ---------------------------------------------------------------------------
 *  Matrix Calculator - Java Application Version
 *  Original Date: October 1997
 *  Updated Swing GUI: June 2002
 *
 *  Version      : $Revision: 1.5 $
 *  Last Modified: $Date: 2002/06/14 06:28:10 $
 *
 *  Author: Marcus Kazmierczak
 *          marcus@mkaz.com
 *          http://www.mkaz.com/math/
 *
 *  Copyright (c) 1997-2002 mkaz.com
 *  Published under a BSD Open Source License
 *  More Info: http://mkaz.com/software/mklicense.html
 *
 *  ---------------------------------------------------------------------------
 */

	private static int iDF = 0;

	public static double[][] transpose(double[][] a)
	{
    	int tms = a.length;
    	double m[][] = new double[tms][tms];
    	for (int i=0; i<tms; i++)
    		for (int j=0; j<tms; j++)
      		m[i][j] = a[j][i];
    	return m;
	} // end method transpose

	public static double[][] inverse(double[][] a) throws Exception
	{
	    // Formula used to Calculate Inverse:
	    // inv(A) = 1/det(A) * adj(A)
	    int tms = a.length;

	    double m[][] = new double[tms][tms];
	    double mm[][] =  adjoint(a);

	    double det = determinant(a);
	    double dd = 0;

	    if (det == 0)
			  throw new Exception("Determinant Equals 0, Not Invertible.");
	    else
	        dd = 1/det;

	    for (int i=0; i < tms; i++)
	    	for (int j=0; j < tms; j++)
	    	   m[i][j] = dd * mm[i][j];

	    return m;
	} // end method inverse


	public static double[][] adjoint(double[][] a)
	{
		int tms = a.length;

		double m[][] = new double[tms][tms];

		int ii, jj, ia, ja;
		double det;

		for (int i=0; i < tms; i++)
		{
			for (int j=0; j < tms; j++)
			{
				ia = ja = 0;

				double ap[][] = new double[tms-1][tms-1];

				for (ii=0; ii < tms; ii++)
				{
					for (jj=0; jj < tms; jj++)
	            {
	                if ((ii != i) && (jj != j))
	                {
	                    ap[ia][ja] = a[ii][jj];
	                    ja++;
	                } // end if
	            } // end for jj
	            if ((ii != i ) && (jj != j))
	            {
						ia++;
	            } // end if
	            ja=0;
				} // end for ii
				det = determinant(ap);
				m[i][j] = (double)Math.pow(-1, i+j) * det;
			} // end for j
		} // end for i
		m = transpose(m);
		return m;
	} // end  method adjoint


	public static double determinant(double[][] matrix)
	{
		int tms = matrix.length;

		double det=1;

		matrix = upperTriangle(matrix);

		for (int i=0; i < tms; i++) { // multiply down diagonal
			det = det * matrix[i][i];
		} // end for

	    det = det * iDF; // adjust w/ determinant factor

	    return det;
	} // end method determinant


	public static double[][] upperTriangle(double[][] m)
	{
	    double f1 = 0;
	    double temp = 0;
	    int tms = m.length;  // get This Matrix Size (could be smaller than global)
	    int v=1;

	    iDF=1;

	    for (int col=0; col < tms-1; col++)
	    {
	        for (int row=col+1; row < tms; row++)
	        {
	            v=1;

	            outahere:
	            while(m[col][col] == 0)             // check if 0 in diagonal
	            {                                   // if so switch until not
	                if (col+v >= tms)               // check if switched all rows
	                {   iDF=0;
	                    break outahere;
	                }
	                else
	                {
	                    for(int c=0; c < tms; c++)
	                    {  temp = m[col][c];
	                       m[col][c]=m[col+v][c];       // switch rows
	                       m[col+v][c] = temp;
	                    } // end for
	                    v++;                            // count row switchs
	                    iDF = iDF * -1;                 // each switch changes determinant factor
	                } // end else
	            } // end while

	            if ( m[col][col] != 0 )
	            {
						try {
	               	f1 = (-1) * m[row][col] / m[col][col];
	                	for (int i=col; i < tms; i++) { m[row][i] = f1*m[col][i] + m[row][i]; }
						} // end try
						catch(Exception e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
						} // end catch

	            } // end if
	        } // end for
	    } // end for
	    return m;
	} // end method upperTriangle

} // end class MatrixOps
