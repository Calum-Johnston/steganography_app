package com.java.calumjohnston.algorithms.techniques;

import com.java.calumjohnston.algorithms.util;

import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class LSBMR {

    public static void encode(BufferedImage image, int[] firstPixel, int[] secondPixel, char firstData, char secondData){
        int currentX = firstPixel[0];
        int currentY = firstPixel[1];
        int nextX = secondPixel[0];
        int nextY = secondPixel[1];
        int firstColour = util.getColourAtPosition(image, currentX, currentY);
        int secondColour = util.getColourAtPosition(image, nextX, nextY);
        int[] newColour = updatePixels(firstColour, secondColour, firstData, secondData);
        util.writeColourAtPosition(image, currentX, currentY, newColour[0]);
        util.writeColourAtPosition(image, nextX, nextY, newColour[1]);
    }

    private static int[] updatePixels(int firstColour, int secondColour, char firstData, char secondData){

        // Get the binary relationships between the firstColour and secondColour
        int pixel_Relationship_2;
        if(firstColour == 0){
            // Fix for 0 - 1 / 2 giving 0 instead of -0.5
            pixel_Relationship_2 = -1 + secondColour;
        }else{
            pixel_Relationship_2 = (int) Math.floor((firstColour - 1) / 2) + secondColour;
        }
        char m1 = util.getLSB(pixel_Relationship_2);

        // Get LSBs of pixel colour
        char firstColourLSB = util.getLSB(firstColour);

        // Determines what to embed based on LSBss and relationship between them
        // Could add an update LSB function here too!! (for pixelData!!)
        if (firstData == firstColourLSB) {
            char m = util.getLSB((int) Math.floor(firstColour / 2) + secondColour);
            if(!(secondData == m)){
                if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                    secondColour -= 1;
                    if(secondColour == -1){
                        secondColour = 255;
                    }
                } else {
                    secondColour += 1;
                    if(secondColour == 256){
                        secondColour = 0;
                    }
                }
            }
        } else if (!(Character.toString(firstData).equals(firstColourLSB))) {
            if (secondData == m1) {
                firstColour -= 1;
                if(firstColour == -1){
                    firstColour = 255;
                }
            } else {
                firstColour += 1;
                if(firstColour == 256){
                    firstColour = 0;
                }
            }
        }
        return new int[] {firstColour, secondColour};
    }



    public static String decode(BufferedImage image, int[] firstPixel, int[] secondPixel){
        int currentX = firstPixel[0];
        int currentY = firstPixel[1];
        int nextX = secondPixel[0];
        int nextY = secondPixel[1];
        int firstColour = util.getColourAtPosition(image, currentX, currentY);
        int secondColour = util.getColourAtPosition(image, nextX, nextY);
        String firstLSB = retrieveLSB(firstColour);
        String secondLSB = retrieveLSB((firstColour / 2) + secondColour);
        return firstLSB + secondLSB;
    }

    private static String retrieveLSB(int colour){
        if(colour % 2 == 0){
            return "0";
        }else{
            return "1";
        }
    }

}
