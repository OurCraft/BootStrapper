package org.craft.bootstrap;

import javax.swing.*;

public class BootstrapMain
{

    public static void main(String[] args)
    {
        BootstrapFrame frame = new BootstrapFrame();
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.log("Launching");

    }

}
