package com.java.calumjohnston.algorithms;

import java.awt.image.BufferedImage;

/**
 * PSNR Class: Implements the computation of PSNR on two images
 */
public class PSNR {

    /**
     * Constructor
     */
    public PSNR(){}

    /**
     * Calculates the Peak Signal to Noise Ratio (PSNR) of an image
     *
     * @param cover     The original image
     * @param stego     The modified image
     * @return          The PSNR of the modified image
     */
    public double calculatePSNR(BufferedImage cover, BufferedImage stego){
        // Get mean squared error of images
        double MSE = calculateMSE(cover, stego);

        // Calculate PSNR from the MSE and return it
        return 10 * Math.log10(Math.pow(255, 2) / MSE);
    }

    /**
     * Calculates the Mean Squared Error (MSE) of an images
     *
     * @param cover     The original image
     * @param stego     The modified image
     * @return          The MSE of the the modified image
     */
    public double calculateMSE(BufferedImage cover, BufferedImage stego){
        int rows = cover.getHeight();
        int columns = cover.getWidth();
        double total = 0;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                // Get pixel data for both images
                int coverPixel = cover.getRGB(i, j);
                int stegoPixel = stego.getRGB(i, j);

                // Convert RGB to YCrCy Colour Space (just for Luma channel)
                int red = (coverPixel & 0x00ff0000) >> 16;
                int green = (coverPixel & 0x0000ff00) >> 8;
                int blue = coverPixel & 0x000000ff;
                int red1 = (stegoPixel & 0x00ff0000) >> 16;
                int green1 = (stegoPixel & 0x0000ff00) >> 8;
                int blue1 = stegoPixel & 0x000000ff;
                //double coverLuma = (0.299 * ((coverPixel & 0x00ff0000) >> 16) + 0.587 * ((coverPixel & 0x0000ff00) >> 8)
                     //   + 0.114 * (coverPixel & 0x000000ff));
                //double stegoLuma = (0.299 * ((stegoPixel & 0x00ff0000) >> 16) + 0.587 * ((stegoPixel & 0x0000ff00) >> 8)
                     //   + 0.114 * (stegoPixel & 0x000000ff));

                // Calculate the squared difference and add to total
                double difSquared = Math.pow(Math.abs(red - red1), 2);
                double difSquared2 = Math.pow(Math.abs(green - green1), 2);
                double difSquared3 = Math.pow(Math.abs(blue - blue1), 2);
                total += difSquared + difSquared2 + difSquared3;
            }
        }
        // Divide total by height * width or images
        return total / (rows * columns);
    }
}
