package com.java.calumjohnston.utilities;

import org.opencv.core.Core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class sobelEdgeDetection {

    {System.loadLibrary( Core.NATIVE_LIBRARY_NAME );}

    public sobelEdgeDetection(){}

    public ArrayList<int[]> getEdgePixels(BufferedImage image, int data){

        // Determine edges through sobel technique
        double[][] gradients = sobelGradients(image);

        // Calculate threshold
        double threshold = calculateThreshold(gradients, data);

        // Determine places to embed data
        return determineEdgeInfo(gradients, threshold);
    }

    public double[][] sobelGradients(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        double[][] gradients = new double[width][height];
        for(int x = 0; x < width; x += 3){
            for(int y = 0; y < height; y += 3){
                if(x == 0 || x == width - 1 || y == 0 || y == height - 1){
                    gradients[x][y] = 0;
                }else{
                    double x_dir = (img.getRGB(x+1,y-1) & 0xFF) + (2*img.getRGB(x+1,y) & 0xFF) + (img.getRGB(x+1,y+1) & 0xFF) -
                            (img.getRGB(x-1,y-1) & 0xFF) - (2*img.getRGB(x-1,y) & 0xFF) - (img.getRGB(x-1,y+1) & 0xFF);
                    double y_dir = (img.getRGB(x-1,y+1) & 0xFF) + (2*img.getRGB(x,y+1) & 0xFF) + (img.getRGB(x+1,y+1) & 0xFF) -
                            (img.getRGB(x-1,y-1) & 0xFF) - (2*img.getRGB(x,y-1) & 0xFF) - (img.getRGB(x+1,y-1) & 0xFF);
                    gradients[x][y] = Math.abs(x_dir) + Math.abs(y_dir);
                }
            }
        }
        return gradients;
    }

    private double calculateThreshold(double[][] gradients, int data){
        boolean complete = false;
        int width = gradients.length;
        int height = gradients[0].length;
        int thres = 400;
        while(!complete){
            int count = 0;
            for(int x = 0; x < width; x += 3){
                for(int y = 0; y < height; y += 3){
                    if(x == 0 || x == width - 1 || y == 0 || y == height - 1){

                    }else{
                        if(gradients[x][y] >= thres){
                            count += 9;
                        }
                    }
                }
            }
            if(count > data){
                complete = true;
            }
            thres -= 10;
        }
        return thres;
    }

    public ArrayList<int[]> determineEdgeInfo(double[][] gradients, double thres){
        ArrayList<int[]> pixelInfo = new ArrayList<>();
        int width = gradients.length;
        int height = gradients[0].length;
        for(int x = 0; x < width; x += 3){
            for(int y = 0; y < height; y += 3){
                if(x == 0 || x == width - 1 || y == 0 || y == height - 1){
                }else{
                    if(gradients[x][y] >= thres){
                        for(int i = -1; i <= 1; i++){
                            for(int j = -1; j <= 1; j++){
                                pixelInfo.add(new int[] {x+i,y+j});
                            }
                        }
                    }
                }
            }
        }
        return pixelInfo;
    }
}
