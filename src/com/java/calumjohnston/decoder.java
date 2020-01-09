package com.java.calumjohnston;

import com.java.calumjohnston.algorithms.decode;

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
public class decoder {
    private JPanel rootPanel;
    private JButton selectImageButtom;
    private JTextField textField;
    private JButton decodeButton;
    private JButton mainMenuButton;

    private final JFileChooser openFileChooser;
    private BufferedImage stegoImage;
    private String text;

    private decode decoder;



    // ======= CONSTRUCTOR =======
    /**
     * Constructor for this class
     */
    public decoder() {

        decoder = new decode();

        decodeButton.setEnabled(false);

        openFileChooser = new JFileChooser();
        // Directory is for testing - change for release
        openFileChooser.setCurrentDirectory(new File("C:\\Users\\Calum\\Documents\\3rd year - Dissertation\\Steganography Desktop App\\rsts"));

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
    public void decodeData() {
        String seed = "";

        String text = decoder.decodeImage(stegoImage);
        textField.setText(text);
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

}
