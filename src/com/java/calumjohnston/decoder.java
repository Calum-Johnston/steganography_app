package com.java.calumjohnston;

import com.java.calumjohnston.algorithms.lsb.*;
import com.java.calumjohnston.algorithms.pvd.pvdDecode;
import com.java.calumjohnston.exceptions.DataOverflowException;
import unused.unused.alllsb_decode;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The decoder class defines the GUI for the decoder form
 */
public class decoder {
    private JPanel rootPanel;
    private JButton selectImageButtom;
    private JTextField textField;
    private JButton decodeButton;
    private JButton mainMenuButton;

    private final JFileChooser openFileChooser;
    private BufferedImage stegoImage;
    private String text;

    private alllsb_decode lsbDecoder;



    // ======= CONSTRUCTOR =======
    /**
     * Constructor for this class
     */
    public decoder() {

        lsbDecoder = new alllsb_decode();

        decodeButton.setEnabled(false);

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\Users\\Calum\\Documents\\Projects\\Dissertation\\Dissertation Project\\rsts"));

        selectImageButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                readImageFile();
            }
        });

        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                decodeData();
            }
        });
    }


    // ======= DECODE FUNCTIONS =======
    /**
     * Runs the decoder on an image
     */
    public void decodeData() {
        // Get algorithm here
        int pixel = stegoImage.getRGB(0, 0);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        int algorithm = Integer.parseInt(getLSB(red) + "" + getLSB(green) + "" + getLSB(blue), 2);


        // Calls algorithm to embed the data
        String data = "";
        if(algorithm == 3){
            pvdDecode pvd = new pvdDecode();
            try{
                data = pvd.decode(stegoImage);
            }catch(DataOverflowException e){
                System.out.println("Error");
            }
        }else if(algorithm == 2) {
            lsbmrDecode lsbmr = new lsbmrDecode();
            try{
                data = lsbmr.decode(stegoImage);
            }catch(DataOverflowException e){
                System.out.println("Error");
            }
        }else if(algorithm == 1){
            lsbmDecode lsbm = new lsbmDecode();
            try{
                data = lsbm.decode(stegoImage);
            }catch(DataOverflowException e){
                System.out.println("Error");
            }
        }else {
            lsbDecode lsb = new lsbDecode();
            try{
                data = lsb.decode(stegoImage);
            }catch(DataOverflowException e){
                System.out.println("Error");
            }
        }

        System.out.println(data);
        textField.setText(data);
    }


    // ======= PURPOSE FUNCTIONS =======
    /**
     * Reads an image from file explorer
     * (TYPE: .png)
     */
    public void readImageFile() {
        // Sets the type of file to get
        openFileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
        int returnValue = openFileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                // Get image based from file explorer
                stegoImage = ImageIO.read(openFileChooser.getSelectedFile());

                // User can now encode
                decodeButton.setEnabled(true);

                // Debugging purposes
                System.out.println("Successfully read in image");

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to read in image");
            }
        }
    }

    /**
     *
     * @return The JPanel of the current form
     */
    public JPanel getPanel(){
        return rootPanel;
    }

    /**
     * Gets the least significant bit of a colour
     *
     * @param colour        The colour to get the LSB of
     * @return              The LSB of colour
     */
    public int getLSB(int colour){
        if(colour % 2 == 0) {
            return 0;
        }else{
            return 1;
        }
    }

}
