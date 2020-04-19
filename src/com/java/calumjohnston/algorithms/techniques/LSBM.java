package com.java.calumjohnston.algorithms.techniques;

import com.java.calumjohnston.algorithms.util;

import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class LSBM {

    public static void encode(BufferedImage image, int[] pixel, char data){
        int currentX = pixel[0];
        int currentY = pixel[1];
        int colour = util.getColourAtPosition(image, currentX, currentY);
        int newColour = updateLSB(data, colour);
        util.writeColourAtPosition(image, currentX, currentY, newColour);
    }

    private static int updateLSB(char data, int colour){
        if(colour % 2 != Character.getNumericValue(data)) {
            if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                colour--;
                if(colour < 0){
                    colour = 1;
                }
            } else {
                colour += 1;
                if(colour > 255){
                    colour = 254 ;
                }
            }
        }
        return colour;
    }



    public static String decode(BufferedImage image, int[] pixel){
        int currentX = pixel[0];
        int currentY = pixel[1];
        int colour  = util.getColourAtPosition(image, currentX, currentY);
        return retrieveLSB(colour);
    }

    private static String retrieveLSB(int colour){
        if(colour % 2 == 0){
            return "0";
        }else{
            return "1";
        }
    }

}
