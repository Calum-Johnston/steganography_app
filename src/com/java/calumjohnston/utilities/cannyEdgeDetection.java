package com.java.calumjohnston.utilities;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Canny Edge Detection Class: This class implements the detection of edges
 * in an image
 *
 * Taken from: https://docs.opencv.org/3.4/da/d5c/tutorial_canny_detector.html
 */
public class cannyEdgeDetection {

    {System.loadLibrary( Core.NATIVE_LIBRARY_NAME );}

    private static final int MAX_LOW_THRESHOLD = 100;
    private static final int RATIO = 3;
    private static final int KERNEL_SIZE = 3;
    private static final Size BLUR_SIZE = new Size(3,3);
    private int lowThresh = 0;
    private Mat srcBlur = new Mat();
    private Mat detectedEdges = new Mat();
    private Mat dst = new Mat();

    /**
     * Constructor
     */
    public cannyEdgeDetection(){
    }

    public BufferedImage detect(BufferedImage image){
        Mat src = convertImagetoMat(image);
        Mat processedSrc = preProcessImage(src);
        for(int i = 0; i < 10; i++){
            System.out.println(src.get(0, i)[0] + " " + processedSrc.get(0, i)[0]);
            System.out.println(src.get(0, i)[1] + " " + processedSrc.get(0, i)[1]);
            System.out.println(src.get(0, i)[2] + " " + processedSrc.get(0, i)[2]);
        }
        return (BufferedImage) update(src);
    }

    public Mat preProcessImage(Mat image){
        for(int i = 0; i < image.rows(); i++){
            for(int j = 0; j < image.cols(); j++){
                int red = (int) image.get(i, j)[0] & 254;
                int green = (int) image.get(i, j)[1] & 254;
                int blue = (int) image.get(i, j)[2] & 254;
                image.put(i, j, new double[] {red, green, blue});
            }
        }
        return image;
    }

    public Mat convertImagetoMat(BufferedImage image){
        // https://stackoverflow.com/questions/14958643/converting-bufferedimage-to-mat-in-opencv
        Mat src = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        src.put(0, 0, data);
        return src;
    }

    private Image update(Mat src) {
        Imgproc.blur(src, srcBlur, BLUR_SIZE);
        Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
        dst = new Mat(src.size(), CvType.CV_8UC3, Scalar.all(0));
        src.copyTo(dst, detectedEdges);
        return HighGui.toBufferedImage(dst);
    }

}
