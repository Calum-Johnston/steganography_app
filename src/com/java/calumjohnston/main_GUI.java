package com.java.calumjohnston;

import javax.smartcardio.Card;
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
    public main_GUI() {

    }

    public void setActionListener(ActionListener action){
        encodeButton.addActionListener(action);
    }

    public JPanel getPanel(){
        return rootPanel;
    }

}
