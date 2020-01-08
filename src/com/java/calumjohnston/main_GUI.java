package com.java.calumjohnston;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main_GUI class runs the GUI for the main page
 */
public class main_GUI{
    private JPanel rootPanel;
    private JButton encodeButton;
    private JButton decodeButton;

    /**
     * Constructor for this class
     */
    public main_GUI(){
        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
    }


    public static void main(String[] args){
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new main_GUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Steganography - MAIN MENU");
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

}
