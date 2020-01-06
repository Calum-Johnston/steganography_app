package com.java.calumjohnston;

import com.java.calumjohnston.algorithms.LSB;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The decoder_GUI class runs the GUI for the decoding page
 */
public class decoder_GUI {
    private JPanel rootPanel;
    private JButton selectImageButtom;
    private JTextField textField;
    private JButton decodeButton;

    private final JFileChooser openFileChooser;
    private BufferedImage stegoImage;
    private String text;


    // ======= CONSTRUCTOR =======
    /**
     * Constructor for this class
     */
    public decoder_GUI(){

        decodeButton.setEnabled(false);

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\"));

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
     * Determines which algorithm to apply when decoding the data
     */
    public void decodeData(){
        LSB l = new LSB();
        //text = l.decode(stegoImage);
        //System.out.println(text);
    }




    // ======= PURPOSE FUNCTIONS =======
    /**
     * Reads an image from file explorer
     * (TYPE: .png)
     */
    public void readImageFile(){
        // Sets the type of file to get
        openFileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
        int returnValue = openFileChooser.showOpenDialog(null);

        if(returnValue == JFileChooser.APPROVE_OPTION){
            try{
                // Get image based from file explorer
                stegoImage = ImageIO.read(openFileChooser.getSelectedFile());

                // User can now encode
                decodeButton.setEnabled(true);

                // Debugging purposes
                System.out.println("Successfully read in image");

            }catch(IOException e){
                e.printStackTrace();
                System.out.println("Failed to read in image");
            }
        }
    }




    // ======= MAIN METHOD =======
    /**
     * Main Method (to remove at some stage)
     *
     * @param args      Arguments input when program is run
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("decoder_GUI");
        frame.setContentPane(new decoder_GUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Steganography - Decode Menu");
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
