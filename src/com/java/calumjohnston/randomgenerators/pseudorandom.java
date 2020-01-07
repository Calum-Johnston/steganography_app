package com.java.calumjohnston.randomgenerators;

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
    String seed;
    List<Integer> orderSequence;
    int position;

    /**
     * Secondary Constructor for the class
     * Only called when no user-input given
     *
     * @param height    the height of the image
     * @param width     the width of the image
     */
    public pseudorandom(int height, int width) {
        this(height, width, "default");
    }

    /**
     * Primary Constructor for the class
     *
     * @param height    the height of the image
     * @param width     the width of the image
     * @param seed      the seed to be used for random generation
     */
    public pseudorandom(int height, int width, String seed){
        this.height = height;
        this.width = width;
        this.seed = seed;
        orderSequence = new ArrayList<Integer>();
        position = 0;
        generateRandomList();
    }

    /**
     * Generates the random sequence to be used
     */
    public void generateRandomList(){
        for(int i = 0; i < width * height; i++){
            orderSequence.add(i);
        }
        // https://stackoverflow.com/questions/6284589/setting-a-seed-to-shuffle-arraylist-in-java-deterministically
        // https://stackoverflow.com/questions/27346809/getting-a-range-off-user-input-for-random-generation
        Collections.shuffle(orderSequence, new Random(seed.hashCode()));
    }

    /**
     *  Gets the next element in the random sequence
     *
     * @return      the next element in the sequence
     */
    public int getNextElement(){
        int element = orderSequence.get(position);
        position += 1;
        return element;
    }

    /**
     * SETTER method for variable position
     *
     * @param position      The new position
     */
    public void setPosition(int position){
        this.position = position;
    }

    /**
     * GETTER method for variable position
     *
     * @return      The position of the current random number being accessed
     */
    public int getPosition(){
        return position;
    }
}
