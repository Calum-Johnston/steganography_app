package com.java.calumjohnston.algorithms;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LSB {

    public LSB(){

    }

    /**
     * FUNCTION: Performs the LSB encoding algorithm
     * INPUT: BufferedImage coverImage: The image of which data will be hidden in
     *        StringBuilder binaryText: The binary data to be hidden within the image
     * RETURN: None
     *
     * NOTES: Algorithm detailed in LSB.txt
     * **/
    public BufferedImage encode(BufferedImage coverImage, StringBuilder binaryText){

        // Initialise starting image pixel position
        int x = 0; int y = 0;
        char data;
        int[] pixelData = new int[3];

        for(int pos = 0; pos < binaryText.length(); pos++){
            // Get message data from text at a specific point
            data = binaryText.charAt(pos);

            // Get pixel data from image at a specific location
            pixelData = getPixelData(coverImage, x, y);

            // Manipulate pixel data (only writes to Green LSB currently)
            insertData(pixelData, data);

            // Write new pixel data to image at specified location
            writePixelData(coverImage, pixelData, x, y);

            // Update position in image to manipulate pixel (serially)
            x += 1;
            if(x == coverImage.getWidth()){
                x = 0; y += 1;
            }
        }
        return coverImage;
    }

    // ENCODING FUNCTIONS
    // Get pixel data from image at a specific location
    public int[] getPixelData(BufferedImage coverImage, int x, int y){
        int pixel = coverImage.getRGB(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        return new int[] {red, green, blue};
    }

    // Insert hidden data into pixel
    public void insertData(int[] pixelData, char data){
        String binaryGreen = Integer.toBinaryString(pixelData[1]);
        String updatedGreen = binaryGreen.substring(0, binaryGreen.length() - 1) + data;
        pixelData[1] = Integer.parseInt(updatedGreen, 2);
    }

    // Write new pixel data to image at specified location
    public void writePixelData(BufferedImage coverImage, int[] pixelData, int x, int y){
        Color newColour = new Color(pixelData[0], pixelData[1], pixelData[2]);
        int newRGB = newColour.getRGB();
        coverImage.setRGB(x, y, newRGB);
    }




    public StringBuilder decode(){
        StringBuilder binaryText = new StringBuilder();
        return binaryText;
    }
}
