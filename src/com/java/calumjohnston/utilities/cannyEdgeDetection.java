package com.java.calumjohnston.utilities;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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

    public void displayDifferences(BufferedImage original, BufferedImage modified){
        Mat originalMat = convertImagetoMat(original);
        Mat modifiedMat = convertImagetoMat(modified);
        Mat originalMatEdge = detectEdges(originalMat, 0, 1);
        Mat modifiedMatEdge = detectEdges(modifiedMat, 0, 1);
        BufferedImage outImg = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
        int diff;
        int result;
        for (int i = 0; i < original.getHeight(); i++) {
            for (int j = 0; j < original.getWidth(); j++) {
                int rgb1 = (int) originalMatEdge.get(j, i)[0];
                int rgb2 = (int) modifiedMatEdge.get(j, i)[0];

                diff = Math.abs(rgb1 - rgb2); // Change
                // Make the difference image gray scale
                // The RGB components are all the same
                result = (diff << 16) | (diff << 8) | diff;
                outImg.setRGB(j, i, result); // Set result
            }
        }
        JFrame frame = new JFrame("Edge Map (Canny detector demo)");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(outImg)));
        frame.pack();
        frame.setVisible(true);
    }




    public ArrayList<int[]> getEdgePixels(BufferedImage image, int data, String seed){

        // Initialises the data
        ArrayList<int[]> edgeInfo = new ArrayList<>();
        Mat maskedImg = setupData(cloning.deepCopy(image));

        // Calculate max amount of edges
        Mat maxImg = detectEdges(maskedImg, 0, 1);
        int maxNum = Core.countNonZero(maxImg) - countParameterInfo(maxImg);

        if(data > maxNum){
            // Get all edge info
            edgeInfo = determineEdgeInfo(maxImg);

            // Get non edge info
            ArrayList<int[]> nonEdgeInfo = determineNonEdgeInfo(maxImg);

            // Randomise if necessary
            if(seed != null){
                Collections.shuffle(edgeInfo, new Random(seed.hashCode()));
                Collections.shuffle(nonEdgeInfo, new Random(seed.hashCode()));
            }

            // Add info that isn't part of edges
            edgeInfo.addAll(nonEdgeInfo);
        }else{
            // Determine the optimal thresholds
            double[] thresholds = binarySearchThres(maskedImg, data);

            // Get edge image from optimal thresholds
            Mat edgeImg = detectEdges(maskedImg, thresholds[0], thresholds[1]);

            // Store pixel data in a list
            edgeInfo = determineEdgeInfo(edgeImg);

            // Randomise if necessary
            if(seed != null){
                Collections.shuffle(edgeInfo, new Random(seed.hashCode()));
            }
        }

        // Return the list
        return edgeInfo;
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



    public double[] binarySearchThres(Mat img, int pixReq){
        int curPix = pixReq; int prePix = pixReq;
        int preChoice = 3;
        double tMax = 1000; double tMin = 0;
        double highThresh = 0; double lowThresh = 0;
        boolean complete = false;

        while(!complete){
            highThresh = ((tMin + tMax) / 2);
            lowThresh = (int)(0.33*highThresh);

            Mat edgeImg = detectEdges(img, lowThresh, highThresh);
            curPix =  Core.countNonZero(edgeImg) - countParameterInfo(edgeImg);

            // Update values
            if(curPix < pixReq){
                tMax = highThresh;
                preChoice = 1;
            }else if(curPix >= 1.1 * pixReq){
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

    private Mat detectEdges(Mat src, double lowThresh, double highThresh) {
        Imgproc.blur(src, srcBlur, BLUR_SIZE);
        Imgproc.Canny(srcBlur, detectedEdges, lowThresh, highThresh, KERNEL_SIZE, false);
        dst = new Mat(src.size(), CvType.CV_8UC3, Scalar.all(0));
        src.copyTo(dst, detectedEdges);
        return dst;
    }



    public int countParameterInfo(Mat img){
        int x = 0; int y = 0;
        int count = 0;
        while(x < 33){
            if(img.get(x,y)[0] != 0){
                count++;
            }
            x++;
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

    public ArrayList<int[]> determineNonEdgeInfo(Mat edgeImg){
        ArrayList<int[]> order = new ArrayList<>();
        int width = edgeImg.width();
        int x = 33; int y = 0;
        while(x < edgeImg.width() && y < edgeImg.height()){
            if(edgeImg.get(x,y)[0] == 0){
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