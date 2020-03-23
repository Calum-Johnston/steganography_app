package com.java.calumjohnston.utilities;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

/**
 * Canny Edge Detection Class: This class implements the detection of edges
 * in an image
 *
 * Taken from: https://docs.opencv.org/3.4/da/d5c/tutorial_canny_detector.html
 */
public class cannyEdgeDetection {

    {System.loadLibrary( Core.NATIVE_LIBRARY_NAME );}

    private static final int KERNEL_SIZE = 3;
    private static final Size BLUR_SIZE = new Size(3,3);
    private Mat srcBlur = new Mat();
    private Mat detectedEdges = new Mat();
    private Mat dst = new Mat();

    /**
     * Constructor
     */
    public cannyEdgeDetection(){

    }

    Mat temp1 = new Mat();
    Mat temp2 = new Mat();

    public void test1(BufferedImage image, int data){
        // Initialises the data
        Mat maskedImg = setupData(cloning.deepCopy(image));

        // Determine the optimal thresholds
        double[] thresholds = detOptimalThres(maskedImg, data);

        // Get edge image from optimal thresholds
        temp1 = detectEdges(maskedImg, thresholds[0], thresholds[1]);
    }

    public void test2(BufferedImage image, int data){
        // Initialises the data
        Mat maskedImg = setupData(cloning.deepCopy(image));

        // Determine the optimal thresholds
        double[] thresholds = detOptimalThres(maskedImg, data);

        // Get edge image from optimal thresholds
        temp2 = detectEdges(maskedImg, thresholds[0], thresholds[1]);
    }

    public void test3(){
        for(int i = 0; i < temp1.width(); i++){
            for(int j = 0; j < temp1.height(); j++){
                double red1 = temp1.get(i, j)[0];
                double red2 = temp2.get(i, j)[0];
                if(red1 != red2){
                    System.out.print(i + " " + j);
                }
            }
        }
    }

    public ArrayList<int[]> getEdgePixels(BufferedImage image, int data){

        // Initialises the data
        Mat maskedImg = setupData(cloning.deepCopy(image));

        // Determine the optimal thresholds
        double[] thresholds = detOptimalThres(maskedImg, data);

        // Get edge image from optimal thresholds
        Mat edgeImg = detectEdges(maskedImg, thresholds[0], thresholds[1]);

        // Store pixel data in a list
        ArrayList<int[]> pixelInfo = determineEdgeInfo(edgeImg);

        // Return the list
        return pixelInfo;
    }


    public Mat setupData(BufferedImage image){
        Mat matImage = convertImagetoMat(image);
        return maskImage(matImage);
    }

    private Mat convertImagetoMat(BufferedImage image){
        // https://stackoverflow.com/questions/14958643/converting-bufferedimage-to-mat-in-opencv
        Mat src = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        src.put(0, 0, data);
        return src;
    }

    private Mat maskImage(Mat image){
        for(int i = 0; i < image.rows(); i++){
            for(int j = 0; j < image.cols(); j++){
                int red = (int) image.get(i, j)[2];
                int green = (int) image.get(i, j)[1];
                int blue = (int) image.get(i, j)[2];
                green = 0; blue = 0;
                image.put(i, j, new double[] {blue, green, red});
            }
        }
        return image;
    }

    private BufferedImage convertMatToImage(Mat src){
        // https://stackoverflow.com/questions/30258163/display-image-in-mat-with-jframe-opencv-3-00
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (src.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage img = new BufferedImage(src.cols(), src.rows(), type);
        src.get(0,0,((DataBufferByte)img.getRaster().getDataBuffer()).getData()); // get all the pixels
        return img;
    }



    public double[] detOptimalThres(Mat img, int pixReq){
        int curPix = pixReq; int prePix = pixReq;
        int preChoice = 3;
        double tMax = 1000; double tMin = 0;
        double highThresh = 0; double lowThresh = 0;
        boolean complete = false;

        while(!complete){
            highThresh = ((tMin + tMax) / 2);
            lowThresh = (int)(0.5*highThresh);

            curPix = calculateThresholds(img, lowThresh, highThresh);
            //System.out.println("High: " + highThresh + ", Low: " + lowThresh + ", Count: " + curPix);

            // Update values
            if(curPix < pixReq){
                tMax = highThresh;
                preChoice = 1;
            }else if(curPix > 1.1 * pixReq){
                tMin = highThresh;
                if(preChoice == 2 && curPix == prePix){
                    complete = true;
                }
                preChoice = 2;
            }else{
                complete = true;
            }
            prePix = curPix;
        }

        return new double[] {lowThresh, highThresh, prePix};
    }

    public int calculateThresholds(Mat img, double lowThresh, double highThresh){
        Mat edgeImg = detectEdges(img, lowThresh, highThresh);
        return getEdgeTotal(edgeImg);
    }

    private Mat detectEdges(Mat src, double lowThresh, double highThresh) {
        Imgproc.blur(src, srcBlur, BLUR_SIZE);
        Imgproc.Canny(srcBlur, detectedEdges, lowThresh, highThresh, KERNEL_SIZE, false);
        dst = new Mat(src.size(), CvType.CV_8UC3, Scalar.all(0));
        src.copyTo(dst, detectedEdges);
        return dst;
    }

    public int getEdgeTotal(Mat src){
        int count = 0;
        int x = 13; int y = 0;
        while(x < src.width() && y < src.height()){
            if(src.get(x,y)[0] != 0 || src.get(x,y)[1] != 0 || src.get(x,y)[2] != 0){
                count++;
                x = (x + 2) % src.width();
                if(x == 0 || x == 1){
                    y++;
                }
            }else{
                x = (x + 1) % src.width();
                if(x == 0){
                    y += 1;
                }
            }
        }
        return count;
    }



    public ArrayList<int[]> determineEdgeInfo(Mat edgeImg){
        ArrayList<int[]> order = new ArrayList<>();
        int width = edgeImg.width();
        int x = 13; int y = 0;
        while(x < edgeImg.width() && y < edgeImg.height()){
            if(edgeImg.get(x,y)[0] != 0 || edgeImg.get(x,y)[1] != 0 || edgeImg.get(x,y)[2] != 0){
                order.add(new int[] {x, y});
                x = (x + 1) % width;
                if(x == 0){
                    y += 1;
                }
            }
            x = (x + 1) % width;
            if(x == 0){
                y += 1;
            }
        }
        return order;
    }


}