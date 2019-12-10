package com.zackehh.ui.components;

import javax.swing.*;

/**
 * An extremely simple override of JLabel#setText to ensure
 * that labels align with JTextFields positioned above them.
 */
public class JResultText extends JLabel {

    /**
     * Simple modification of JLabel#setText to ensure
     * that the text is aligned with any other JTextFields.
     *
     * @param text      the text to set to the JLabel
     */
    @Override
    public void setText(String text){
        super.setText("  " + text);
    }

}