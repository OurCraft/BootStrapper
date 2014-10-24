package org.craft.bootstrap;

import java.awt.*;

import javax.swing.*;

public class BootstrapFrame extends JFrame
{

    private static final long serialVersionUID = 7508720796514116950L;
    private JTextArea         textArea;

    public BootstrapFrame()
    {
        setTitle("OurCraft Launcher Bootstrap - " + "OurCraft:BuildNumber");
        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(800, 500));
        add(new JScrollPane(textArea));
    }

    public void log(String message)
    {
        String formatted = "[Bootstrap] " + message;
        textArea.setText(textArea.getText() + "\n" + formatted);
        System.out.println(formatted);
    }
}
