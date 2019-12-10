package com.zackehh.ui.components;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class JResultTextTest {

    @Test
    public void testJResultText() throws Exception {
        JResultText jResultText = new JResultText();
        String text = "This is some text";

        jResultText.setText(text);

        assertEquals(jResultText.getText().length(), text.length() + 2);
        assertEquals(jResultText.getText(), "  " + text);
    }

}
