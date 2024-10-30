import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.util.*;

public class Game extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;
    public int mapWidth = 15;
    public int mapHeight = 15;
    public int endX;
    public int endY;
    public int timeTaken = 0;
    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private int[] pixels;
    public ArrayList<Texture> textures;
    public Camera camera;
    public Screen screen;
    public JFrame startFrame;
    public JFrame endFrame;

    public static ArrayList<ArrayList<Integer>> primFindValidPositions (int x, int y, int mapWidth, int mapLength, int[][] mapGrid) {
        ArrayList<ArrayList<Integer>> validPositions = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> curPosition = new ArrayList<Integer>();
        
        curPosition.add(0);
        curPosition.add(0);
        
        if (x + 2 < mapWidth && mapGrid[y][x + 2] == 0){
            curPosition.set(0, x + 2);
            curPosition.set(1, y);
            validPositions.add(new ArrayList<Integer>(curPosition));
        }
        if (y + 2 < mapLength && mapGrid[y + 2][x] == 0) {
            curPosition.set(0, x);
            curPosition.set(1, y + 2);
            validPositions.add(new ArrayList<Integer>(curPosition));
        }
        if (x - 2 >= 0 && mapGrid[y][x - 2] == 0) {
            curPosition.set(0, x - 2);
            curPosition.set(1, y);
            validPositions.add(new ArrayList<Integer>(curPosition));
        }
        if (y - 2 >= 0 && mapGrid[y - 2][x] == 0) {
            curPosition.set(0, x);
            curPosition.set(1, y - 2);
            validPositions.add(new ArrayList<Integer>(curPosition));
        }
        return validPositions;
    }

    
    public int[][] primGenerateMap(int mapWidth, int mapLength){
        int[][] mapGen = new int[mapLength][mapWidth];
        ArrayList<ArrayList<Integer>> openPositions = new ArrayList<ArrayList<Integer>>();
        // open slots
        ArrayList<ArrayList<Integer>> closedPositions = new ArrayList<ArrayList<Integer>>();
        // filled slots
        ArrayList<ArrayList<Integer>> trappedPositions = new ArrayList<ArrayList<Integer>>();
        //positions that are surrounded on all sides and cannot be forked in any way whatsoever, but have a peak structure
        ArrayList<ArrayList<Integer>> peakPositions = new ArrayList<ArrayList<Integer>>();
        //positions that can be forked and contain a filled slot
        
        ArrayList<ArrayList<Integer>> validPositions = new ArrayList<ArrayList<Integer>>();
        //temporary position list for the valid function check
        ArrayList<Integer> positionList = new ArrayList<Integer>();
        //temporary position list holder 
        positionList.add(0);
        positionList.add(0);
        
        // for (int[] arr1: mapGen) {
        //     for (int val: arr1) {
        //         System.out.print(val + " ");
        //     }
        //     System.out.println();
        // }
        
        for (int i = 1; i < mapWidth; i += 2) {
            for (int j = 1; j < mapLength; j += 2) {
                positionList.set(0, i);
                positionList.set(1, j);
                openPositions.add(new ArrayList<Integer>(List.copyOf(positionList)));
            }
        }
        
        
        
        mapGen[1][1] = 5;
        positionList.set(0, 1);
        positionList.set(1, 1);
        closedPositions.add(new ArrayList<Integer>(List.copyOf(positionList)));
        peakPositions.add(new ArrayList<Integer>(List.copyOf(positionList)));
        if (openPositions.contains(positionList)) {
            openPositions.remove(openPositions.indexOf(positionList));
        }
        
        System.out.println("Open positions " + openPositions);
        System.out.println("Closed positions " + closedPositions);
        System.out.println("Peaks " + peakPositions);
        System.out.println("Trapped positions " + trappedPositions);
        
        try{
            Thread.sleep(1000);
        } catch (Exception e){
            
        }
        
        int x = 0;
        int y = 0;
        int xChange = 0;
        int yChange = 0;
        int direction = 0;
        int lookPos;
        
        while (openPositions.size() != 0) {
            // System.out.println("open positions" + openPositions);
            lookPos = (int) (Math.random() * peakPositions.size());
            // System.out.println(lookPos);
            // System.out.println("peaks: " + peakPositions);
            x = peakPositions.get(lookPos).get(0);
            y = peakPositions.get(lookPos).get(1);
            
            positionList.set(0, x);
            positionList.set(1, y);
            validPositions = new ArrayList<ArrayList<Integer>>(List.copyOf(primFindValidPositions(x, y, mapWidth, mapLength, mapGen)));
            // System.out.println(validPositions);
            if (validPositions.size() > 0) {
                
                direction = (int) (Math.random() * validPositions.size());
                xChange = (validPositions.get(direction).get(0) - x) / 2;
                yChange = (validPositions.get(direction).get(1) - y) / 2;
                mapGen[y + yChange][x + xChange] = 5;
                mapGen[y + (2 * yChange)][x + (2 * xChange)] = 5;
                
                
                
                // if (primFindValidPositions(x, y, mapWidth, mapLength, mapGen).size() == 0 && peakPositions.contains(positionList)) {
                // }
                
                // System.out.println("checking last known position: " + x + " : " + y + " - " + primFindValidPositions(x, y, mapWidth, mapLength, mapGen));
                
                if (primFindValidPositions(x, y, mapWidth, mapLength, mapGen).size() == 0) {
                    peakPositions.remove(peakPositions.indexOf(positionList));
                    trappedPositions.add(new ArrayList<Integer>(List.copyOf(positionList)));
                }
                
                positionList.set(0, x + (2 * xChange));
                positionList.set(1, y + (2 * yChange));
                
                if (!peakPositions.contains(positionList) && !closedPositions.contains(positionList)) {
                    peakPositions.add(new ArrayList<Integer>(List.copyOf(positionList)));
                    closedPositions.add(new ArrayList<Integer>(List.copyOf(positionList)));
                }
                
                if (openPositions.contains(positionList)) {
                    openPositions.remove(openPositions.indexOf(positionList));
                }
                
                
            } else {
                
                if (!trappedPositions.contains(positionList)) {
                    trappedPositions.add(new ArrayList<Integer>(List.copyOf(positionList)));
                }
                
                if (closedPositions.contains(positionList)) {
                    closedPositions.remove(closedPositions.indexOf(positionList));
                }

            }
            
         
            
            
        }
        
 
        
        
        lookPos = (int) (Math.random() * peakPositions.size());
        mapGen[peakPositions.get(lookPos).get(1)][peakPositions.get(lookPos).get(0)] = 7;
        endX = peakPositions.get(lookPos).get(1);
        endY = peakPositions.get(lookPos).get(0);


        for (int i = 0; i < mapLength; i++) {
            for (int j = 0; j < mapWidth; j++) {
                if (mapGen[i][j] == 0) {
                    mapGen[i][j] = (int) (Math.random() * 3) + 2;
                } else if (mapGen[i][j] == 5) {
                    mapGen[i][j] = 0;
                } else if (mapGen[i][j] == 7) {
                    mapGen[i][j] = 1;
                }
                
            }
        }
        
        

        for (int[] arr1: mapGen) {
            for (int val: arr1) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
        
        return mapGen;
    }
    
    public int[][] map = primGenerateMap(15, 15);

    public Game() {
        thread = new Thread(this);
        image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        textures = new ArrayList<Texture>();
        textures.add(Texture.text1);
        textures.add(Texture.text2);
        textures.add(Texture.text3);
        textures.add(Texture.text4);
        camera = new Camera(1, 1, 1, 0, 0, -0.66);
        addKeyListener(camera);
        start();
    }

    private synchronized void start() {
        running = false;
        startFrame = new JFrame("Front Places");
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLocationRelativeTo(null);
        startFrame.setSize(640, 480);
        startFrame.setLayout(new BorderLayout());

        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        startPanel.setBackground(new Color(167, 142, 6));
        startPanel.setSize(100, 100);
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));

        
        JLabel title = new JLabel("Front Places", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("Play Game");

        startButton.setPreferredSize(new Dimension(100, 60));
        startButton.setMaximumSize(new Dimension(200, 60));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font("Arial", Font.PLAIN, 18));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.setVisible(false);
                running = true;
                screen = new Screen(map, mapWidth, mapHeight, textures, 640, 480);
                setSize(640, 480);
                setResizable(false);
                setTitle("Front Places");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setBackground(Color.black);
                setLocationRelativeTo(null);
                setVisible(true);
                thread.start();
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(100, 60));
        exitButton.setMaximumSize(new Dimension(200, 60));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setFont(new Font("Arial", Font.PLAIN, 18));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        startPanel.add(Box.createVerticalStrut(50)); 
        startPanel.add(title);
        startPanel.add(Box.createVerticalStrut(50)); 
        startPanel.add(startButton);
        startPanel.add(Box.createVerticalStrut(20)); 
        startPanel.add(exitButton);
        startPanel.add(Box.createVerticalStrut(50)); 
        startFrame.add(startPanel, BorderLayout.CENTER);

        startFrame.setVisible(true);
    
    }

    private synchronized void stop() {
        running = false;
        dispose();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startGameWindow() {

    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2 = image.createGraphics();
    
        // Clear the image
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, image.getWidth(), image.getHeight());
    
        screen.update(camera, pixels);
        
        if(running) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font("Arial", java.awt.Font.PLAIN, 20));
            g2.setColor(Color.WHITE);
            g2.drawString("Time Elapsed: " + timeTaken, 20, 100); 
        }
    
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        
        g.dispose();
        bs.show();
    }
    public void run() {
        System.out.println(endX + " " + endY);
        long lastTime = System.nanoTime();
        long timeCreated = System.currentTimeMillis();
        final double framesPerSec = 1000000000.0 / 60.0;
        double delta = 0;
        requestFocus();
        while(running) {
            long now = System.nanoTime();
            delta = delta + ((now-lastTime)/framesPerSec);
            lastTime = now;
            timeTaken = (int)((System.currentTimeMillis() - timeCreated)/1000);
            while(delta >= 1) {
                screen.update(camera, pixels);
                camera.update(map);
                delta--;
            }
            render();
            // if (Math.round(camera.getXPos()) == endX && Math.round(camera.getYPos()) == endY) {
            if (((int)(camera.getXPos()) <= endX + 1.0 && (int)(camera.getXPos()) >= endX - 1.0) && ((int)(camera.getYPos()) <= endY + 1.0) && ((int)(camera.getYPos()) >= endY - 1.0)){
                endFrame = new JFrame("Front Places");
                endFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                endFrame.setLocationRelativeTo(null);
                endFrame.setSize(640, 480);
                endFrame.setLayout(new BorderLayout());

                JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                
                endPanel.setBackground(new Color(167, 142, 6));
                endPanel.setSize(100, 100);
                endPanel.setLayout(new BoxLayout(endPanel, BoxLayout.Y_AXIS));

                
                JLabel title = new JLabel("You Made It Out!", JLabel.CENTER);
                title.setFont(new Font("Arial", Font.BOLD, 30));
                title.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel timeLabel = new JLabel("Time Taken:" + timeTaken, JLabel.CENTER);
                timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
                timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton endButton = new JButton("Play Again");

                endButton.setPreferredSize(new Dimension(100, 60));
                endButton.setMaximumSize(new Dimension(200, 60));
                endButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                endButton.setFont(new Font("Arial", Font.PLAIN, 18));

                endButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        
                    }
                });

                JButton exitButton = new JButton("Exit");
                exitButton.setPreferredSize(new Dimension(100, 60));
                exitButton.setMaximumSize(new Dimension(200, 60));
                exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                exitButton.setFont(new Font("Arial", Font.PLAIN, 18));
                exitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                endPanel.add(Box.createVerticalStrut(50)); 
                endPanel.add(title);
                endPanel.add(Box.createVerticalStrut(50)); 
                endPanel.add(timeLabel);
                endPanel.add(Box.createVerticalStrut(50)); 
                endPanel.add(endButton);
                endPanel.add(Box.createVerticalStrut(20)); 
                endPanel.add(exitButton);
                endPanel.add(Box.createVerticalStrut(50)); 
                endFrame.add(endPanel, BorderLayout.CENTER);

                endFrame.setVisible(true);
                setVisible(false);
                thread.interrupt();

            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
    }
}