package kidstech;
// Copyright: Jay Summet 2010
// Distributed under a Creative Commons Attribution-NonCommercial
// license, details found here:
// http://creativecommons.org/licenses/by-nc/3.0/

import resources.Jama.Matrix;
import resources.Jama.SingularValueDecomposition;




public class Homography {

 Matrix fHomography;
 Matrix rHomography;


  // Constructor
  public Homography() {
	
	//Initially, the homography will do no real transformations
	// So we set both forward and reverse homographies
	// to the identity matrix.
	fHomography = Matrix.identity(3,3);
	rHomography = Matrix.identity(3,3);

	} // end Homography() constructor.


 //************************************************************************
 // This function does all the heavy mathmatical lifting. It is based upon
 // the method listed on pages 2 and 3 of the following paper:
 //  Smarter Presentations: Exploiting Homography in Camera-Projector Systems
 //  by Rahul Sukthankar, Robert G. Stockton, Matthew D. Mullin
 //  in ICCV 2001
 //
 // Basically, it accepts 4 (or more) sets of correspondence points, and 
 // calculates the corresponding projective homography.

public void findHomography( Matrix inPoints, Matrix outPoints) {
  // Our input must have the name number of rows and columns (same number of
  // points) so we have a correspondenace between in and out points.
  assert( inPoints.getRowDimension() == outPoints.getRowDimension() );
  assert( inPoints.getColumnDimension() == outPoints.getColumnDimension() );

  // We must have at least 4 points, and each point must have only  X,Y data
  assert( inPoints.getRowDimension() > 3);
  assert( inPoints.getColumnDimension() == 2); 
  

 // Depending upon the input size, we build a 9x9 (or larger if more than the
 // minimum number of 4 points) matrix.

 Matrix A;

 // Note that all must be initialized to zeros, which Java / Jama does...

 if( inPoints.getRowDimension() > 4) {
   A = new Matrix(inPoints.getRowDimension() * 2, 9);
  } // end if more than 4 points...
 else // exactly 4 points
  {
   A = new Matrix( 9,9);
  }


 // Now, fill in the magical values. Each set of correspondance points
 // gets 2 rows in the A matrix...
 for(int i=0, twoi=0 ;  i < inPoints.getRowDimension(); i++, twoi +=2)
 {
  // Get the correspondance points:
  double sX = outPoints.get(i,0);
  double sY = outPoints.get(i,1);
  double bX = inPoints.get(i,0);
  double bY = inPoints.get(i,1);

 // Put data into the first row...
 A.set(twoi,0, bX);
 A.set(twoi,1, bY);
 A.set(twoi,2, 1.0);
 A.set(twoi,3, 0.0);
 A.set(twoi,4, 0.0);
 A.set(twoi,5, 0.0);
 A.set(twoi,6, -bX * sX );
 A.set(twoi,7, -bY * sX );
 A.set(twoi,8, -sX );

 //Put data into the second row...
 A.set(twoi+1, 0, 0.0 );
 A.set(twoi+1, 1, 0.0 ); 
 A.set(twoi+1, 2, 0.0 );
 A.set(twoi+1, 3, bX  );
 A.set(twoi+1, 4, bY  );
 A.set(twoi+1, 5, 1.0 );
 A.set(twoi+1, 6, -bX * sY);
 A.set(twoi+1, 7, -bY * sY);
 A.set(twoi+1, 8, -sY     );
 
 } // end for each correspondance point...


 // We now use a singular value decomposition to solve the series of
 // equations we set up previously... This next line does a lot of work ;>
  
 SingularValueDecomposition svd = new  SingularValueDecomposition(A);

 // Look at the diagonal of the S matrix (singular values),
 // Find the index of the smallest one!
 double SV[] = svd.getSingularValues();
 double min = 99999999;
 int index = -1;
 for(int i = 0; i < SV.length; i++) {
   if( Math.abs( SV[i] ) < min ) {
      min = Math.abs( SV[i]);
      index = i;
    }
 }
 
  Matrix V = svd.getV(); 


 //Normalize data before using...
 double acc = 0.0;
 for(int i = 0; i < 9; i++) {
    acc += V.get(i,index) * V.get(i,index); // squared 
  }
 
 double scale = 0.0;
 if( V.get(8,index) > 0.0)  {
    scale = Math.sqrt(acc);
 } else {
    scale = - Math.sqrt(acc); 
 }

 //Divide all elements by the scale factor.
 // We really only care about the "index" column, but it's easier to use the
 // times function and the inverse to do them all at once...
 V.times( 1.0 / scale); 


 // Finally, copy the data into the homography matrix.
 fHomography.set(0,0, V.get(0,index) );
 fHomography.set(0,1, V.get(1,index) );
 fHomography.set(0,2, V.get(2,index) );
 fHomography.set(1,0, V.get(3,index) );
 fHomography.set(1,1, V.get(4,index) );
 fHomography.set(1,2, V.get(5, index) );
 fHomography.set(2,0, V.get(6, index) );
 fHomography.set(2,1, V.get(7, index) );
 fHomography.set(2,2, V.get(8, index) );

 //Also find the inverse homography for ease of calculations later.
 rHomography = fHomography.inverse();

 // all done!
 } // end findHomography

public double[] reverseTranslatePoint(double x, double y) {
   return translatePoint(rHomography, x,y);
 } // end reverseTranslatePoint.

public double[] forwardTranslatePoint(double x, double y) {
   return translatePoint(fHomography, x,y);
} // end forwardTranslatePoint.

// This private helper method will multiply a 2D point by a homography.
// it's used by the forward/reverse translate point methods.
private double[] translatePoint( Matrix h, double x, double y) {
  //Place the point into a 3x1 matrix.
  Matrix inMat = new Matrix(3,1);
  inMat.set(0,0,x);
  inMat.set(1,0,y);
  inMat.set(2,0, 1.0);

  Matrix outPt = h.times(inMat );

  // Divide the X and Y coordinate by the W factor to set W to 1...
  double [] outArray = new double[2];
  double X,Y,W;
  X =  outPt.get(0,0);
  Y =   outPt.get(1,0);
  W =  outPt.get(2,0);

  outArray[0] =  X / W;
  outArray[1] =  Y / W; 
  return outArray;

 } // end translatePoint


 public static void main( String args[]) {

  // Test method to make sure it all works!
  // We are making a 2x2 box correspond to a 4x4 box, both centered on the
  // origin...
  Matrix inPoints = new Matrix(4,2);
  Matrix outPoints = new Matrix(4,2);


  inPoints.set(0,0, -1.0);
  inPoints.set(0,1, -1.0);
  outPoints.set(0,0, -2.0);
  outPoints.set(0,1, -2.0);


  inPoints.set(1,0, -1.0 );
  inPoints.set(1,1,  1.0 );
  outPoints.set(1,0, -2.0 );
  outPoints.set(1,1,  2.0 );

  inPoints.set(2,0,  1.0 );
  inPoints.set(2,1, -1.0 );
  outPoints.set(2,0, 2.0 );
  outPoints.set(2,1, -2.0 );

  
  inPoints.set(3,0, 1.0  );
  inPoints.set(3,1, 1.0 );
  outPoints.set(3,0, 2.0  );
  outPoints.set(3,1, 2.0 );

  Homography h = new Homography();
 
 // Test identity:
  double[] outP = h.forwardTranslatePoint(0, 0);
  System.out.println("should be 0.0,0.0:  " + outP[0] +","+ outP[1] );

  h.findHomography(inPoints, outPoints);

  outP = h.forwardTranslatePoint(1, 1 );

  System.out.println("should be 2.0,2.0: " + outP[0] +","+ outP[1] );


 }

}
