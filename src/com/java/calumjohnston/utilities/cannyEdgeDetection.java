package com.java.calumjohnston.utilities;

import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
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
    private Imgcodecs imageCodecs;

    /**
     * Constructor
     */
    public cannyEdgeDetection(){
        imageCodecs = new Imgcodecs();
    }

    public ArrayList<int[]> getEdgePixels(BufferedImage image, int data){

        // Initialises the data
        Mat maskedImg = setupData(cloning.deepCopy(image));

        // Calculate max amount of edges
        Mat maxImg = detectEdges(maskedImg, 0, 1);
        int maxNum = getEdgeTotal(maxImg);

        // Determine the optimal thresholds
        double[] thresholds = detOptimalThres(maskedImg, data);
        double[] asda = detOptimalThresholds(maskedImg, data);

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
        Mat src = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);
        for(int i = 0; i < image.getHeight(); i++){
            for(int j = 0; j < image.getWidth(); j++){
                src.put(i, j, (image.getRGB(i, j) & 0x00ff0000) >> 16 );
            }
        }
        return src;
    }

    private Mat maskImage(Mat image){
        for(int i = 0; i < image.rows(); i++){
            for(int j = 0; j < image.cols(); j++){
                int currentValue = (int) image.get(i, j)[0];
                if(currentValue % 4 != 0){
                    double newValue = currentValue - (currentValue % 4);
                    image.put(i, j, new double[] {newValue});
                }
            }
        }
        return image;
    }

    public String conformBinaryLength(int data, int length){
        String binaryParameter = Integer.toBinaryString(data);
        binaryParameter = (StringUtils.repeat('0', length) + binaryParameter).substring(binaryParameter.length());
        return binaryParameter;
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
            lowThresh = (int)(0.33*highThresh);

            curPix = calculateThresholds(img, lowThresh, highThresh);

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
    public double[] detOptimalThresholds(Mat img, int pixReq){
        int curPix = pixReq; int prePix = pixReq;
        int preChoice = 3;
        double highThresh = 1; double lowThresh = 0;
        boolean complete = false;
        int decreaseValue = 10; int increaseValue = 10;

        while(!complete){

            lowThresh = highThresh * 0.4;
            curPix = calculateThresholds(img, lowThresh, highThresh);

            // Update values
            if(curPix < pixReq){
                if(preChoice == 2){
                    decreaseValue -= 1;
                }
                highThresh -= decreaseValue;
                preChoice = 1;
            }else if(curPix > 1.1 * pixReq){
                if(preChoice == 1){
                    increaseValue -= 10;
                }
                highThresh += increaseValue;
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
        int x = 33; int y = 0;
        while(x < src.width() && y < src.height()){
            if(src.get(x,y)[0] != 0){
                count++;
                x = (x + 1) % src.width();
                if(x == 0){
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
        int x = 33; int y = 0;
        while(x < edgeImg.width() && y < edgeImg.height()){
            if(edgeImg.get(x,y)[0] != 0){
                order.add(new int[] {x, y});
            }
            x = (x + 1) % width;
            if(x == 0){
                y += 1;
            }
        }
        return order;
    }


}