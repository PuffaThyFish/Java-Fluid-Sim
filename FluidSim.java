import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import javax.swing.*;

public class FluidSim {
    public static void main(String[] args) {
        // init it works test method
        System.out.println("hi ");

        // init visual thingys
        Simulator sim = new Simulator(600, 200);
        sim.runSim();
    }
}

class Simulator {
    public Panelinator panel;
    public int[][] logicGrid;

    public Simulator(int width, int height) {
        // init visuals
        JFrame frame = new JFrame();
        panel = new Panelinator(width, height);
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // init logical grid
        logicGrid = new int[width][height];
    }

    public void runSim() {
        logicGrid[60][32] = 237;
        logicGrid[60][33] = 237;
        logicGrid[60][34] = 237;
        logicGrid[60][35] = 237;
        panel.updateBuffer(logicGrid);
    }

    public void click(int x, int y) {

    } 
}

class Panelinator extends JPanel {
    public BufferedImage pixelGrid;

    public Panelinator(int width, int height) {
        pixelGrid = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                click(e.getX(), e.getY());
            }
        });
    }

    public void updateBuffer(int[][] logicGrid) {
        for (int x = 0; x < pixelGrid.getWidth(); x++) {
            for (int y = 0; y < pixelGrid.getHeight(); y++) {
                pixelGrid.setRGB(x, y, logicGrid[x][y]);
            }
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(pixelGrid, 0, 0, this);
    }
}

class Tile {
    private int x;
    private int y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getCoords() {
        return x;
    }
}

class Dirt {
    public int color = 0x0099FF;

    public Dirt(int x, int y) {
        super()
    }

    public int moveTile(int[][] logicGrid) {
        return 
    }
}