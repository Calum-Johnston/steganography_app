package com.java.calumjohnston.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The class produces sequence of unique numbers within a set
 * range based off a key.
 * The key is some user input (typically a password).
 *
 * Designed for Image Steganography
 */
public class pseudorandom{

    int height;
    int width;
    int sequencePosition;
    String seed;
    ArrayList<int[]> orderSequence;

    /**
     * Secondary Constructor for the class
     * Only called when no user-input given
     *
     * @param height    the height of the image
     * @param width     the width of the image
     */
    public pseudorandom(int height, int width) {
        this(height, width, "default", 1);
    }

    /**
     * Secondary Constructor for the class
     * Only called when no user-input given
     *
     * @param height    the height of the image
     * @param width     the width of the image
     * @param seed          the seed to be used for random generation
     */
    public pseudorandom(int height, int width, String seed) {
        this(height, width, seed, 1);
    }

    /**
     * Primary Constructor for the class
     *
     * @param height        the height of the image
     * @param width         the width of the image
     * @param seed          the seed to be used for random generation
     * @param gap           gap between generated numbers
     */
    public pseudorandom(int height, int width, String seed, int gap){
        this.height = height;
        this.width = width;
        this.seed = seed;
        this.sequencePosition = 0;
        this.orderSequence = new ArrayList<int[]>();
        generateRandomList(gap);
    }

    /**
     * Generates the random sequence to be used
     *
     * @param gap           gap between generated numbers
     */
    public void generateRandomList(int gap){

        // Change final value depending on the gap (prevents errors later)
        int finalValue = width * height;
        if(gap > 1 && (width * height) % gap != 0){
            finalValue = (width * height) - (gap - 1);
        }

        for(int i = 18; i < finalValue; i += gap){
            int x = i % width;
            int y = i / width;
            orderSequence.add(new int[] {x, y});
        }
        // https://stackoverflow.com/questions/6284589/setting-a-seed-to-shuffle-arraylist-in-java-deterministically
        // https://stackoverflow.com/questions/27346809/getting-a-range-off-user-input-for-random-generation
        Collections.shuffle(orderSequence, new Random(seed.hashCode()));
    }

    /**
     *  Gets the random sequence
     *
     * @return      the next element in the sequence
     */
    public ArrayList<int[]> getRandomSequence(){
        return orderSequence;
    }

}
