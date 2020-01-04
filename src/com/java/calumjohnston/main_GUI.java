package com.java.calumjohnston;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The main_GUI class runs the GUI for the main page
 */
public class main_GUI{
    private static JFrame frame;
    private JPanel rootPanel;
    private JButton encodeButton;

    /**
     * Constructor for this class
     */
    public main_GUI(){

        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                // Open encoder GUI
            }
        });
    }


    // ======= MAIN MENU =======
    /**
     * Main Method - instantiates the program
     *
     * @param args  Arguments input when program is run
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new main_GUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Steganography - MAIN MENU");
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
