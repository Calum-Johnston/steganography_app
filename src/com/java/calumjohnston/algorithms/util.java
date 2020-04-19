package com.java.calumjohnston.algorithms;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class util {

    // USEFUL FUNCTIONS - MAYBE PUT IN UTILITIES
    /**
     * Gets colour channel data from an image at a specific location
     *
     * @param image             The image to get the data from
     * @param x                 x coordinate of pixel
     * @param y                 y coordinate of pixel
     * @return                  The value of the colour channel at position (x,y) in image
     */
    public static int getColourAtPosition(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) & 0x000000ff;
    }

    /**
     * Writes colour channel data to an image at a specific location
     *
     * @param image             The image to write the data to
     * @param x                 x coordinate of pixel
     * @param y                 y coordinate of pixel
     * @param data              The data to be written
     */
    public static void writeColourAtPosition(BufferedImage image, int x, int y, int data){
        Color color = new Color(data, data, data);
        image.setRGB(x, y, color.getRGB());
    }

    /**
     * Gets the least significant bit of a colour
     *
     * @param colour        The colour to get the LSB of
     * @return              The LSB of colour
     */
    public static char getLSB(int colour){
        if(colour % 2 == 0) {
            return '0';
        }else{
            return '1';
        }
    }

    /**
     * Converts some integer to binary of a defined length
     *
     * @param data      The data to be converted to binary
     * @param length    The number of bits to represent the data as
     * @return          The binary equivalent of data
     */
    public static String conformBinaryLength(int data, int length){
        String binaryParameter = Integer.toBinaryString(data);
        binaryParameter = (StringUtils.repeat('0', length) + binaryParameter).substring(binaryParameter.length());
        return binaryParameter;
    }

}
