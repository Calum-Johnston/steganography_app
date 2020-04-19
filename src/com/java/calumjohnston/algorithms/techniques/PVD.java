package com.java.calumjohnston.algorithms.techniques;

import com.java.calumjohnston.algorithms.util;

import java.awt.image.BufferedImage;

public class PVD {

    public static int encode(BufferedImage image, int[] firstPixel, String data){
        int currentX = firstPixel[0];
        int currentY = firstPixel[1];
        int nextX = (currentX + 1) % image.getWidth();
        int nextY = ((nextX == 0) ? currentY + 1 : currentY);
        int firstColour = util.getColourAtPosition(image, currentX, currentY);
        int secondColour = util.getColourAtPosition(image, nextX, nextY);
        int[] newColour = updatePixels(firstColour, secondColour, data);
        util.writeColourAtPosition(image, currentX, currentY, newColour[0]);
        util.writeColourAtPosition(image, nextX, nextY, newColour[1]);
        return newColour[2];
    }

    private static int[] updatePixels(int firstColour, int secondColour, String data){

        // Calculate pixel difference
        int d = secondColour - firstColour;

        // Get the range data for this colour interval
        int[] encodingData = quantisationRangeTable(Math.abs(d));

        // Calculate the quantisation range width, then the number of bits to encode
        int width = encodingData[1] - encodingData[0] + 1;
        int n = (int)Math.floor(Math.log(width)/Math.log(2.0));

        // Get the data to encode into the image (ensuring we don't go out of range)
        while(data.length() < n){
            data = data + "0";
        }
        int decVal = Integer.parseInt(data.substring(0, n), 2);

        // CHECK RANGE
        int[] newColours = updateColoursPVD(firstColour, secondColour, d, encodingData[1]);

        if(newColours[0] < 0 || newColours[0] > 255 || newColours[1] < 0 || newColours[1] > 255){
            return new int[] {firstColour, secondColour, n};
        }else {

            // Calculate embedding values
            int d1;
            if(d >= 0){ d1 = encodingData[0] + decVal; }
            else{ d1 = -(encodingData[0] + decVal); }

            // Encode the data
            newColours = updateColoursPVD(firstColour, secondColour, d, d1);

            // Write colour data back to the image
            return new int[] {newColours[0], newColours[1], n};
        }
    }

    /**
     * Inserts data into two colours by modifying their difference
     *
     * @param firstColour       First colour to be manipulated
     * @param secondColour      Second colour to be manipulated
     * @param d                 The difference between the colours
     * @param d1                The new difference calculated from the data to be inserted
     * @return                  Updated colours
     */
    private static int[] updateColoursPVD(int firstColour, int secondColour, int d, int d1){
        double m = d1 - d;

        // Obtain new colour values for firstColour and secondColour by averaging new difference to them
        if (Math.abs(d) % 2 == 1) {
            firstColour -= (int) Math.ceil(m/2);
            secondColour += (int) Math.floor(m/2);
        } else {
            firstColour -= (int) Math.floor(m/2);
            secondColour += (int) Math.ceil(m/2);
        }

        return new int[] {firstColour, secondColour};
    }

    /**
     * Returns the range the difference of two values sits in (for PVD)
     *
     * @param difference    The difference between two consecutive pixel values
     * @return              The data required for encoding
     */
    private static int[] quantisationRangeTable(int difference){
        if(difference <= 7){
            return new int[] {0, 7};
        }else if(difference <= 15){
            return new int[] {8, 15};
        }else if(difference <= 31){
            return new int[] {16, 31};
        }else if(difference <= 63){
            return new int[] {32, 63};
        }else if(difference <= 127){
            return new int[] {64, 127};
        }else{
            return new int[] {128, 255};
        }
    }




    public static String decode(BufferedImage image, int[] firstPixel){
        int currentX = firstPixel[0];
        int currentY = firstPixel[1];
        int nextX = (currentX + 1) % image.getWidth();
        int nextY = ((nextX == 0) ? currentY + 1 : currentY);
        int firstColour = util.getColourAtPosition(image, currentX, currentY);
        int secondColour = util.getColourAtPosition(image, nextX, nextY);
        return retrieveData(firstColour, secondColour);
    }

    private static String retrieveData(int firstColour, int secondColour){
        int d = secondColour - firstColour;

        int[] decodingData = quantisationRangeTable(Math.abs(d));

        // Calculate the quantisation range width, then the number of bits to encode
        int width = decodingData[1] - decodingData[0] + 1;
        int t = (int)Math.floor(Math.log(width)/Math.log(2.0));

        // DETERMINE WHETHER DATA EMBEDDING HAS OCCURRED
        int[] newColours = updateColoursPVD(firstColour, secondColour, d, decodingData[1]);

        if(newColours[0] < 0 || newColours[0] > 255 || newColours[1] < 0 || newColours[1] > 255) {
            return "";
        }else{
            int b = Math.abs(d) - decodingData[0];
            return util.conformBinaryLength(b, t);
        }
    }

}
