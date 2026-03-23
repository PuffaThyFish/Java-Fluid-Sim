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

class Simulator implements ClickHandler {
    private Panelinator panel;
    private Tile[][] logicGrid;
    private int width;
    private int height;
    private String tileMode;

    public Simulator(int width, int height) {
        // init visuals
        JFrame frame = new JFrame();
        panel = new Panelinator(width, height, this);
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // init logical grid
        logicGrid = new Tile[width][height];
        this.width = width;
        this.height = height;

        // init placement 
        tileMode = "dirt";
    }

    public void runSim() {
        Timer timer = new Timer(16, e -> {
        update();
        panel.updateBuffer(logicGrid);
        });

        timer.start();
    }

    public void update() {
        for (int x = width-1; x >= 0; x--) {
            for (int y = height-1; y >= 0; y--) {
                if (logicGrid[x][y] != null) { logicGrid[x][y].moveTile(logicGrid, width, height); }
            }
        }
    }

    public void click(int x, int y) {
        String clickMsg = String.format("Click at %d, %d", x, y);
        System.out.println(clickMsg);
        if (x > 0 && x < width-1 && y > 0 && y < height-1) {
            if (this.tileMode.equals("dirt")) {
                logicGrid[x][y-1] = new Dirt(x, y-1);
                logicGrid[x-1][y] = new Dirt(x-1, y);
                logicGrid[x][y] = new Dirt(x, y);
                logicGrid[x+1][y] = new Dirt(x+1, y);
                logicGrid[x][y+1] = new Dirt(x, y+1);
            }
        }
    } 
}

interface ClickHandler {
    void click(int x, int y);
}

class Panelinator extends JPanel {
    public BufferedImage pixelGrid;

    public Panelinator(int width, int height, ClickHandler clicker) {
        // set image stuff
        pixelGrid = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));

        // add mouse inputs, sending clicks to the sim for processing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clicker.click(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                clicker.click(e.getX(), e.getY());  // spawn while holding and dragging
            }
        });
    }

    public void updateBuffer(Tile[][] logicGrid) {
        // turn logic grid into visual pixels, then update panel to show them
        for (int x = 0; x < pixelGrid.getWidth(); x++) {
            for (int y = 0; y < pixelGrid.getHeight(); y++) {
                if (logicGrid[x][y] == null) {pixelGrid.setRGB(x, y, 0x000000); }
                else {pixelGrid.setRGB(x, y, logicGrid[x][y].getColor()); }
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
    protected int x;
    protected int y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getColor() {
        return 0;
    }

    public Tile[][] moveTile(Tile[][] logicGrid, int width, int height) { return logicGrid; }

    public int getX() { return x; }
    public int getY() { return y; }
}

class Dirt extends Tile {
    private int color = 0x0099FF;

    public Dirt(int x, int y) {
        super(x, y);
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public Tile[][] moveTile(Tile[][] logicGrid, int width, int height) {
        if (y+1 < height && logicGrid[x][y+1] == null) {
            logicGrid[x][y+1] = this;
            logicGrid[x][y] = null;
            this.y = y+1;
        }
        return logicGrid;
    }
}