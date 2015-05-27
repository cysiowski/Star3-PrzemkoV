import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import Utils.MatrixToBufferedImageConverter;






public class Main {

	static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	public static void main(String[] args) {
		MatrixToBufferedImageConverter mbic = new MatrixToBufferedImageConverter();
		
		
		JFrame  jf = new JFrame("Krzychu to bos!");
		Imgproc ip = new Imgproc();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//jf.setSize(600,600);
		ShowPanel sp = new ShowPanel();
		jf.setContentPane(sp);
		jf.setSize(650,500);
		// TODO Auto-generated method stub
		VideoCapture capture =new VideoCapture(0);
		jf.setVisible(true);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Mat matryx = new Mat();
		Mat grayMatryx = new Mat();
		Mat blurredGrayMatryx = new Mat();
		Mat circles = new Mat();
		Mat obrazek = Imgcodecs.imread("/home/cysiowski/Pictures/ELO.png");
		//Mat matryx = obrazek;
	if(capture.isOpened()){
			System.out.println("elo");
			
			capture.retrieve(matryx);
				while(true){
			capture.read(matryx);
				if(!matryx.empty()){
					ip.cvtColor(matryx, grayMatryx, Imgproc.COLOR_BGR2GRAY);
					ip.GaussianBlur(grayMatryx, blurredGrayMatryx, new Size(3,3), 2,2);
					ip.HoughCircles(blurredGrayMatryx, circles, ip.CV_HOUGH_GRADIENT,1, blurredGrayMatryx.rows()/8, 60, 70, 0, 0 );
					for(  int i = 0 ; i < circles.cols(); i++ )
					  {
						
						    double vCircle[] = circles.get(0,i);
						    Point p = new Point(vCircle);
						    double x = vCircle[0];
						    int x0 = (int) Math.round(x);
						    double y = vCircle[1];
						    int y0 = (int) Math.round(y);
						    double radius = vCircle[2];
						    int radius0 = (int) Math.round(radius);
						
					      ip.circle(matryx, p, 3, new Scalar(0,255,0), -1, 8, 0 );
					      // circle outline
					      ip.circle(matryx, p, radius0, new Scalar(0,0,255), 3, 8, 0 );
					   }
					mbic.setMatrix(matryx, ".jpg");
					BufferedImage bufim = mbic.getImage();
					sp.setFrame(bufim);
					sp.repaint();
				} else{
					System.out.println("No nic nie ma :(");
				}
			}
	}
			
		
		
		
	capture.release();
	}

}
