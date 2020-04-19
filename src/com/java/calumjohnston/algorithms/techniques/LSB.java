package com.java.calumjohnston.algorithms.techniques;

import com.java.calumjohnston.algorithms.util;

import java.awt.image.BufferedImage;

public class LSB {

    public static void encode(BufferedImage image, int[] pixel, char data){
        int currentX = pixel[0];
        int currentY = pixel[1];
        int colour = util.getColourAtPosition(image, currentX, currentY);
        int newColour = updateLSB(data, colour);
        util.writeColourAtPosition(image, currentX, currentY, newColour);
    }

    private static int updateLSB(char data, int colour){
        if(colour % 2 != Character.getNumericValue(data)){
            if(colour % 2 == 0){
                colour++;
            }else{
                colour--;
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
