import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import utils.MatrixToBufferedImageConverter;

import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainController {

    @FXML
    public ImageView cameraView = new ImageView();

    @FXML
    private Slider rSlider;

    @FXML
    private Slider bSlider;

    @FXML
    private TextField param3;

    @FXML
    private TextField param4;

    @FXML
    private TextField param1;

    @FXML
    private Slider gSlider;

    @FXML
    private TextField param2;

    @FXML
    private void initialize() {
        cameraView.setImage(oneScreen());

        Thread readImage = new Thread(() -> {
            while (true) {
                Image img = oneScreen();
                try {
                    frameQueue.put(img);
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        readImage.setDaemon(true);
        readImage.start();

        ScheduledService<Void> service = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    public Void call() {
                        try {
                            Image image = frameQueue.take();
                            Platform.runLater(() -> {
                                cameraView.setImage(image);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
            }
        };
        service.setDelay(Duration.millis(1));
        service.setPeriod(Duration.millis(5));
        service.start();

    }
    //loop();


    BlockingQueue<Image> frameQueue = new ArrayBlockingQueue<>(10);

    AtomicReference<Image> nextFrame = new AtomicReference<>();

    Imgproc ip = new Imgproc();
    VideoCapture capture = new VideoCapture(0);
    MatrixToBufferedImageConverter mbic = new MatrixToBufferedImageConverter();
    Mat matryx = new Mat();
    Mat grayMatryx = new Mat();
    Mat blurredGrayMatryx = new Mat();
    Mat circles = new Mat();

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public Image oneScreen() {

        //Mat obrazek = Imgcodecs.imread("/home/cysiowski/Pictures/ELO.png");
        //Mat matryx = obrazek;
        if (capture.isOpened()) {
            System.out.println("elo");

            capture.retrieve(matryx);
            capture.read(matryx);
            if (!matryx.empty()) {
                ip.cvtColor(matryx, grayMatryx, Imgproc.COLOR_BGR2GRAY);
                ip.GaussianBlur(grayMatryx, blurredGrayMatryx, new Size(5, 5), 5, 5);
                ip.HoughCircles(blurredGrayMatryx, circles, ip.HOUGH_GRADIENT, 1, blurredGrayMatryx.rows() / 8, 60, 70, 50, 300);
                //ip.HoughCircles(blurredGrayMatryx, circles, ip.CV_HOUGH_GRADIENT,1, blurredGrayMatryx.rows()/8, 60, 70, 0, 0 );
                for (int i = 0; i < circles.cols(); i++) {

                    double vCircle[] = circles.get(0, i);
                    Point p = new Point(vCircle);
                    double x = vCircle[0];
                    int x0 = (int) Math.round(x);
                    double y = vCircle[1];
                    int y0 = (int) Math.round(y);
                    double radius = vCircle[2];
                    int radius0 = (int) Math.round(radius);

                    ip.circle(matryx, p, 3, new Scalar(0, 255, 0), -1, 8, 0);
                    // circle outline
                    ip.circle(matryx, p, radius0, new Scalar(0, 0, 255), 3, 8, 0);
                }
                mbic.setMatrix(matryx, ".jpg");
                BufferedImage bufim = mbic.getImage();

                return SwingFXUtils.toFXImage(bufim, null);


            } else {
                System.out.println("No nic nie ma :(");
            }

        }
        return null;
    }
}
