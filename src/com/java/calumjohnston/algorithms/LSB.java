package com.java.calumjohnston.algorithms;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The LSB class performs LSB encoding and decoding on images
 */
public class LSB {

    /**
     * Constructor for the class
     */
    public LSB(){

    }


    /**
     * Performs the LSB encoding algorithm
     * - Detailed in lsb.txt
     *
     * @param coverImage    Image to be used (acting as the Cover)
     * @param binaryText    Data to be inserted (acting as the Payload)
     * @return              Image with data hidden within it (acting as the Stego Image)
     */
    public BufferedImage encode(BufferedImage coverImage, StringBuilder binaryText){

        int x = 0; int y = 0;
        char data;
        int[] pixelData = new int[3];

        for(int pos = 0; pos < binaryText.length(); pos++){
            data = binaryText.charAt(pos);
            pixelData = getPixelData(coverImage, x, y);
            insertData(pixelData, data);
            writePixelData(coverImage, pixelData, x, y);
            x += 1;
            if(x == coverImage.getWidth()){
                x = 0; y += 1;
            }
        }

        return coverImage;
    }


    /**
     * Gets pixel data from an image at a specific location
     *
     * @param coverImage  Image to be used
     * @param x           x coordinate of pixel
     * @param y           y coordinate of pixel
     * @return            Tuple of RGB values (representing the pixel)
     */
    public int[] getPixelData(BufferedImage coverImage, int x, int y){
        int pixel = coverImage.getRGB(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        return new int[] {red, green, blue};
    }

    /**
     * Inserts data into pixel data (by LSB technique)
     *
     * @param pixelData     Tuple of RGB values
     * @param data          Data to be inserted
     */
    public void insertData(int[] pixelData, char data){
        String binaryGreen = Integer.toBinaryString(pixelData[1]);
        String updatedGreen = binaryGreen.substring(0, binaryGreen.length() - 1) + data;
        pixelData[1] = Integer.parseInt(updatedGreen, 2);
    }

    /**
     * Writes pixel data to an image at a specific location
     *
     * @param coverImage    Image to be used
     * @param pixelData     Typle of RGB values (representing the pixel)
     * @param x             x coordinate of pixel
     * @param y             y coordinate of pixel
     */
    public void writePixelData(BufferedImage coverImage, int[] pixelData, int x, int y){
        Color newColour = new Color(pixelData[0], pixelData[1], pixelData[2]);
        int newRGB = newColour.getRGB();
        coverImage.setRGB(x, y, newRGB);
    }


    /**
     * Performs the LSB decoding algorithm
     * (detailed in LSB.txt)
     *
     * @return
     */
    public StringBuilder decode(){
        StringBuilder binaryText = new StringBuilder();
        return binaryText;
    }
}
