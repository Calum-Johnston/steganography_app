package com.java.calumjohnston.utilities;

import org.opencv.core.Core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class sobelEdgeDetection {

    {System.loadLibrary( Core.NATIVE_LIBRARY_NAME );}

    double[][] gradients;
    double threshold;

    public sobelEdgeDetection(){ }

    public ArrayList<int[]> getEdgePixels(BufferedImage image, int data){

        // Determine edges through sobel technique
        gradients = sobelGradients(image);

        // Calculate threshold
        threshold = calculateThreshold(gradients, data);

        // Determine places to embed data
        return determineEdgeInfo(gradients, threshold);
    }

    public ArrayList<int[]> getEdgePixels(BufferedImage image, int data, int thres){
        // Determine edges through sobel technique
        gradients = sobelGradients(image);

        // Determine places to embed data
        return determineEdgeInfo(gradients, thres);
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
                    double x_dir = (img.getRGB(x - 1,y-1) & 0xFF) + (2*(img.getRGB(x - 1,y) & 0xFF)) + (img.getRGB(x-1,y+1) & 0xFF) -
                            (img.getRGB(x+1,y-1) & 0xFF) - (2*(img.getRGB(x+1,y) & 0xFF)) - (img.getRGB(x+1,y+1) & 0xFF);
                    double y_dir = (img.getRGB(x-1,y-1) & 0xFF) + (2*(img.getRGB(x,y-1) & 0xFF)) + (img.getRGB(x+1,y-1) & 0xFF) -
                            (img.getRGB(x-1,y+1) & 0xFF) - (2*(img.getRGB(x,y+1) & 0xFF)) - (img.getRGB(x+1,y+1) & 0xFF);
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
        int thres = 410;
        while(!complete){
            int count = 0;
            thres -= 10;
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
                                pixelInfo.add(new int[] {x+j,y+i});
                            }
                        }
                    }
                }
            }
        }
        return pixelInfo;
    }

    public BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }


    public double adjustGradients(BufferedImage image, int length){
        double[][] newGradients = sobelGradients(image);
        int width = newGradients.length;
        int height = newGradients[0].length;
        for(int x = 0; x < width; x += 3){
            for(int y = 0; y < height; y += 3){
                if(x == 0 || x == width - 1 || y == 0 || y == height - 1){
                }else{
                    if(gradients[x][y] > newGradients[x][y]){
                        double[][] values = new double[3][3];
                        values[0][0] = image.getRGB(x-1, y-1) & 0xFF;
                        values[0][1] = image.getRGB(x-1, y) & 0xFF;
                        values[0][2] = image.getRGB(x-1, y+1) & 0xFF;
                        values[1][0] = image.getRGB(x, y-1) & 0xFF;
                        values[1][1] = image.getRGB(x, y) & 0xFF;
                        values[1][2] = image.getRGB(x, y+1) & 0xFF;
                        values[2][0] = image.getRGB(x+1, y-1) & 0xFF;
                        values[2][1] = image.getRGB(x+1, y) & 0xFF;
                        values[2][2] = image.getRGB(x+1, y+1) & 0xFF;
                        double modifiedGradient = newGradients[x][y];
                        double targetGradient = gradients[x][y];
                        out:
                        for(int i = 0; i <= 2; i++){
                            for(int j = 0; j <= 2; j++){
                                for(int plusminus = -1; plusminus <= 1; plusminus += 2){
                                    if(i != 1 || j != 1){
                                        values[i][j] = values[i][j] + (plusminus) * 4;
                                        double x_dir = (values[0][0]) + (2*values[0][1]) + (values[0][2])
                                                - (values[2][0]) - (2*values[2][1]) - (values[2][2]);
                                        double y_dir = (values[0][0]) + (2*values[1][0]) + (values[2][0])
                                                - (values[0][2]) - (2*values[1][2]) - (values[2][2]);
                                        double newGrad = Math.abs(x_dir) + Math.abs(y_dir);
                                        if(newGrad >= targetGradient){
                                            Color color = new Color((int) values[i][j], (int) values[i][j], (int) values[i][j]);
                                            image.setRGB(x + i - 1, y + j - 1, color.getRGB());
                                            break out;
                                        }else if(targetGradient - newGrad < targetGradient - modifiedGradient){
                                            Color color = new Color((int) values[i][j], (int) values[i][j], (int) values[i][j]);
                                            image.setRGB(x + i - 1, y + j - 1, color.getRGB());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        double x_dir = (values[0][0]) + (2*values[0][1]) + (values[0][2])
                                - (values[2][0]) - (2*values[2][1]) - (values[2][2]);
                        double y_dir = (values[0][0]) + (2*values[1][0]) + (values[2][0])
                                - (values[0][2]) - (2*values[1][2]) - (values[2][2]);
                        double newGrad = Math.abs(x_dir) + Math.abs(y_dir);
                    }
                }
            }
        }

        return calculateThreshold(gradients, length);

    }
}
