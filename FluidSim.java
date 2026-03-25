import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class FluidSim {
    public static void main(String[] args) {
        // init it works test method
        System.out.println("hi ");

        // init visual thingys
        Simulator sim = new Simulator(200, 100);
        sim.runSim();
    }
}

class Simulator implements ClickHandler, KeyHandler {
    private Panelinator panel;
    private Tile[][] logicGrid;
    private int width;
    private int height;
    private int[] columnOrder;
    private String tileMode;

    public Simulator(int width, int height) {
        // init visuals
        JFrame frame = new JFrame();
        panel = new Panelinator(width, height, this, this);
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // init logical grid
        logicGrid = new Tile[width][height];
        this.width = width;
        this.height = height;
        columnOrder = new int[width];
        for (int i = 0; i < width; i++) columnOrder[i] = i;

        // init placement 
        tileMode = "dirt";
    }

    public void runSim() {
        Timer timer = new Timer(5, e -> {
        update();
        panel.updateBuffer(logicGrid);
        });

        timer.start();
    }

    public void update() { 
        // implements random column technique
        for (int i = width-1; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));  // random index from 0 to i inclusive
            int tmp = columnOrder[i];
            columnOrder[i] = columnOrder[j];
            columnOrder[j] = tmp;
        }


        // reach every tile and tell it to move
        for (int cx = width-1; cx >= 0; cx--) {
            int x = columnOrder[cx]; 
            for (int y = height-1; y >= 0; y--) {
                if (logicGrid[x][y] != null && logicGrid[x][y].visited == false) { 
                    logicGrid[x][y].moveTile(logicGrid, width, height); 
                }
            }
        }
        // reset all flags
        for (int y = height-1; y >= 0; y--) { 
            for (int x = width-1; x >= 0; x--) {
                if (logicGrid[x][y] != null) { logicGrid[x][y].visited = false; }  
            }
        }
    }
    

    public void click(int x, int y) {
        String clickMsg = String.format("Click at %d, %d", x, y);
        System.out.println(clickMsg);
        if (x > 0 && x < width-1 && y > 0 && y < height-1) {
            if (this.tileMode.equals("stone")) {
                logicGrid[x][y-1] = new Stone(x, y-1);
                logicGrid[x-1][y] = new Stone(x-1, y);
                logicGrid[x][y] = new Stone(x, y);
                logicGrid[x+1][y] = new Stone(x+1, y);
                logicGrid[x][y+1] = new Stone(x, y+1);
            }
            else if (this.tileMode.equals("dirt")) {
                logicGrid[x][y-1] = new Dirt(x, y-1);
                logicGrid[x-1][y] = new Dirt(x-1, y);
                logicGrid[x][y] = new Dirt(x, y);
                logicGrid[x+1][y] = new Dirt(x+1, y);
                logicGrid[x][y+1] = new Dirt(x, y+1);
            }
            else if (this.tileMode.equals("sand")) {
                logicGrid[x][y-1] = new Sand(x, y-1);
                logicGrid[x-1][y] = new Sand(x-1, y);
                logicGrid[x][y] = new Sand(x, y);
                logicGrid[x+1][y] = new Sand(x+1, y);
                logicGrid[x][y+1] = new Sand(x, y+1);
            }
            else if (this.tileMode.equals("water")) {
                logicGrid[x][y-1] = new Water(x, y-1);
                logicGrid[x-1][y] = new Water(x-1, y);
                logicGrid[x][y] = new Water(x, y);
                logicGrid[x+1][y] = new Water(x+1, y);
                logicGrid[x][y+1] = new Water(x, y+1);
            }
        }
    } 

    public void key(int keyCode) {
        if (keyCode == KeyEvent.VK_1) {
            this.tileMode = "stone";
            System.out.println("Current mode: Stone");
        }
        else if (keyCode == KeyEvent.VK_2) {
            this.tileMode = "dirt";
            System.out.println("Current mode: Dirt");
        }
        else if (keyCode == KeyEvent.VK_3) {
            this.tileMode = "sand";
            System.out.println("Current mode: Sand");
        }
        else if (keyCode == KeyEvent.VK_4) {
            this.tileMode = "water";
            System.out.println("Current mode: Water");
        }
    }
}

interface ClickHandler {
    void click(int x, int y);
}

interface KeyHandler {
    void key(int keyCode);
}

class Panelinator extends JPanel {
    public BufferedImage pixelGrid;
    private static final int SCALE = 4;

    public Panelinator(int width, int height, ClickHandler clicker, KeyHandler keyer) {
        // set image stuff
        pixelGrid = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width*SCALE, height*SCALE));
        setFocusable(true);

        // add mouse inputs, sending clicks to the sim for processing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clicker.click(e.getX()/SCALE, e.getY()/SCALE);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                clicker.click(e.getX()/SCALE, e.getY()/SCALE);  // spawn while holding and dragging
            }
        });

        addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
                keyer.key(e.getKeyCode());
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
        g.drawImage(pixelGrid, 0, 0, pixelGrid.getWidth()*SCALE, 
                    pixelGrid.getHeight()*SCALE, this);
    }
}

class Tile {
    protected int x;
    protected int y;
    protected int color = 0;
    protected boolean visited = false;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getColor() {
        return this.color;
    }

    public Tile[][] moveTile(Tile[][] logicGrid, int width, int height) { return logicGrid; }

    public int getX() { return x; }
    public int getY() { return y; }
}

class Stone extends Tile {
    public Stone(int x, int y) {
        super(x, y);
        this.color = 0xB2BEB5;
    }

    @Override
    public Tile[][] moveTile(Tile[][] logicGrid, int width, int height) {
        visited = true;
        return logicGrid;
    }
}

class Dirt extends Tile {
    public Dirt(int x, int y) {
        super(x, y);
        this.color = 0x3d251e;
    }

    @Override
    public Tile[][] moveTile(Tile[][] logicGrid, int width, int height) {
        if (y+1 < height && logicGrid[x][y+1] == null) { // straight drop
            logicGrid[x][y+1] = this;
            logicGrid[x][y] = null;
            this.y = y+1;
        }
        else if (y+1 < height && logicGrid[x][y+1] instanceof Water) { // check for water to sink in
            Tile water = logicGrid[x][y+1];
            int dir = Math.random() < 0.5 ? -1 : 1;
            if (x+dir >= 0 && x+dir < width && logicGrid[x+dir][y] == null) {
                logicGrid[x][y+1] = this;
                logicGrid[x][y] = null;
                logicGrid[x+dir][y] = water;
                water.y = y;
                water.x = x+dir;
                water.visited = true;
                this.y = y+1;
                this.color = 0x2d151e;
            }
            else if (x-dir >= 0 && x-dir < width && logicGrid[x-dir][y] == null) {
                logicGrid[x][y+1] = this;
                logicGrid[x][y] = null;
                logicGrid[x-dir][y] = water;
                water.y = y;
                water.x = x-dir;
                water.visited = true;
                this.y = y+1;
                this.color = 0x2d151e;
            }
            else {
                logicGrid[x][y+1] = this;
                logicGrid[x][y] = water;
                water.y = y;
                water.visited = true;
                this.y = y+1;
                this.color = 0x2d151e;
            }
        }
        visited = true;
        return logicGrid;
    }
}

class Sand extends Tile {
    public Sand(int x, int y) {
        super(x, y);
        this.color = 0xFFFF00;
    }

    @Override
    public Tile[][] moveTile(Tile[][] logicGrid, int width, int height) {
        if (y+1 < height && logicGrid[x][y+1] == null) { // move S
            logicGrid[x][y+1] = this;
            logicGrid[x][y] = null;
            this.y = y+1;
        }
        else if (y+1 < height && logicGrid[x][y+1] == null) { // straight drop
            logicGrid[x][y+1] = this;
            logicGrid[x][y] = null;
            this.y = y+1;
        }
        else if (y+1 < height && logicGrid[x][y+1] instanceof Water) { // check for water to sink in
            Tile water = logicGrid[x][y+1];
            int dir = Math.random() < 0.5 ? -1 : 1;
            if (x+dir >= 0 && x+dir < width && logicGrid[x+dir][y] == null) {
                logicGrid[x][y+1] = this;
                logicGrid[x][y] = null;
                logicGrid[x+dir][y] = water;
                water.y = y;
                water.x = x+dir;
                water.visited = true;
                this.y = y+1;
            }
            else if (x-dir >= 0 && x-dir < width && logicGrid[x-dir][y] == null) {
                logicGrid[x][y+1] = this;
                logicGrid[x][y] = null;
                logicGrid[x-dir][y] = water;
                water.y = y;
                water.x = x-dir;
                water.visited = true;
                this.y = y+1;
            }
            else {
                logicGrid[x][y+1] = this;
                logicGrid[x][y] = water;
                water.y = y;
                water.visited = true;
                this.y = y+1;
            }
        }
        else {
            int dir = Math.random() < 0.5 ? -1 : 1; // rng a float 0-1, check if < 0.5
            // check the random dir first, then the other option
            if (x+dir >= 0 && x+dir < width && y+1 < height && logicGrid[x+dir][y+1] == null) { 
                logicGrid[x+dir][y+1] = this;
                logicGrid[x][y] = null;
                this.y = y+1;
                this.x = x+dir;
            }
            else if (x-dir >= 0 && x-dir < width && y+1 < height && logicGrid[x-dir][y+1] == null) { 
                logicGrid[x-dir][y+1] = this;
                logicGrid[x][y] = null;
                this.y = y+1;
                this.x = x-dir;
            }
        }
        visited = true;
        return logicGrid;
    }
}

class Water extends Tile {
    public Water(int x, int y) {
        super(x, y);
        this.color = 0x0000FF;
    }

    @Override
    public Tile[][] moveTile(Tile[][] logicGrid, int width, int height) {
        if (y+1 < height && logicGrid[x][y+1] == null) { // move S
            logicGrid[x][y+1] = this;
            logicGrid[x][y] = null;
            this.y = y+1;
        }
        else {
            int dir = Math.random() < 0.5 ? -1 : 1; // rng a float 0-1, check if < 0.5
            // check the random down dir first, then the other option
            if (x+dir >= 0 && x+dir < width && y+1 < height && logicGrid[x+dir][y+1] == null) { 
                logicGrid[x+dir][y+1] = this;
                logicGrid[x][y] = null;
                this.y = y+1;
                this.x = x+dir;
            }
            else if (x-dir >= 0 && x-dir < width && y+1 < height && logicGrid[x-dir][y+1] == null) { 
                logicGrid[x-dir][y+1] = this;
                logicGrid[x][y] = null;
                this.y = y+1;
                this.x = x-dir;
            }
            // check down diagonally in two tiles
            else if (x+dir*2 >= 0 && x+dir*2 < width && y+1 < height && logicGrid[x+dir*2][y+1] == null) { 
                logicGrid[x+dir*2][y+1] = this;
                logicGrid[x][y] = null;
                this.y = y+1;
                this.x = x+dir*2;
            }
            else if (x-dir*2 >= 0 && x-dir*2 < width && y+1 < height && logicGrid[x-dir*2][y+1] == null) { 
                logicGrid[x-dir*2][y+1] = this;
                logicGrid[x][y] = null;
                this.y = y+1;
                this.x = x-dir*2;
            }
            // check sideways
            else if (x+dir >= 0 && x+dir < width && logicGrid[x+dir][y] == null) { 
                logicGrid[x+dir][y] = this;
                logicGrid[x][y] = null;
                this.x = x+dir;
            }
            else if (x-dir >= 0 && x-dir < width && logicGrid[x-dir][y] == null) { 
                logicGrid[x-dir][y] = this;
                logicGrid[x][y] = null;
                this.x = x-dir;
            }
        }
        visited = true;
        return logicGrid;
    }
}