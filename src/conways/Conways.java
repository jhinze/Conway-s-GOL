/*
 * Justin Hinze
 * csc4380
 * Project 1
 */
package conways;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

//NOTE: this program requires java 8
public class Conways {

    private final JFrame frame = new JFrame("Project 1, Justin Hinze, Conway's game of life");
    private final JPanel header = new JPanel();
    private final JPanel grid = new JPanel();
    private final JPanel container = new JPanel();
    private JScrollPane scrollPane;
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JButton start = new JButton("Start");
    private final JButton pause = new JButton("Pause");
    private final JButton reset = new JButton("Reset");
    private final JButton random = new JButton("Random");
    private final JButton step = new JButton("Step");
    private final JSlider throttle = new JSlider(JSlider.HORIZONTAL, 100, 1000, 500);
    private final JLabel delay = new JLabel("Delay (ms): ");
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu file = new JMenu("File");
    private final JMenu help = new JMenu("Help");
    private final JMenuItem open = new JMenuItem("Open");
    private final JMenuItem save = new JMenuItem("Save");
    private final JMenuItem exit = new JMenuItem("Exit");
    private final JMenuItem about = new JMenuItem("About");
    private final JFileChooser fileChooser = new JFileChooser();
    private final Cell[][] cells = new Cell[70][70];
    private final List<Cell> cellList = new ArrayList();
    private final Set<Cell> changeSet = new HashSet();
    private final Set<Cell> raiseSet = Collections.synchronizedSet(new HashSet());
    private final Set<Cell> killSet = Collections.synchronizedSet(new HashSet());
    private final Hashtable<Integer, JLabel> speeds = new Hashtable<>();
    private final Random rand = new Random();
    private int speed = 500;
    private Timer timer;
    private boolean running;

    //constructor. call gui and listeners.
    public Conways() {
        this.buildGui();
        this.listeners();
    }

    //build and display gui
    private void buildGui() {
        start.setFont(new Font("serif", Font.PLAIN, 14));
        pause.setFont(new Font("serif", Font.PLAIN, 14));
        reset.setFont(new Font("serif", Font.PLAIN, 14));
        random.setFont(new Font("serif", Font.PLAIN, 14));
        step.setFont(new Font("serif", Font.PLAIN, 14));
        delay.setFont(new Font("serif", Font.PLAIN, 14));
        throttle.setFont(new Font("serif", Font.PLAIN, 14));
        delay.setHorizontalAlignment(SwingConstants.RIGHT);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.cgl", "cgl"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 950);
        frame.setLocation(100, 10);
        container.setLayout(new GridBagLayout());
        file.add(open);
        file.add(save);
        file.addSeparator();
        file.add(exit);
        help.add(about);
        menuBar.add(file);
        menuBar.add(help);
        frame.setJMenuBar(menuBar);
        for (int i = 100; i <= 1000; i += 100) {
            speeds.put(i, new JLabel("" + i));
        }
        throttle.setLabelTable(speeds);
        throttle.setPaintTicks(true);
        throttle.setPaintLabels(true);
        throttle.setSnapToTicks(true);
        throttle.setMajorTickSpacing(100);
        throttle.setPreferredSize(new Dimension(300, 50));
        throttle.setPaintTrack(false);
        start.setPreferredSize(new Dimension(80, 50));
        pause.setPreferredSize(new Dimension(80, 50));
        reset.setPreferredSize(new Dimension(80, 50));
        random.setPreferredSize(new Dimension(95, 50));
        step.setPreferredSize(new Dimension(80, 50));
        delay.setPreferredSize(new Dimension(85, 42));
        delay.setVerticalAlignment(JLabel.BOTTOM);
        header.add(start);
        header.add(pause);
        header.add(reset);
        header.add(random);
        header.add(step);
        header.add(delay);
        header.add(throttle);
        grid.setLayout(new GridLayout(0, 70));
        for (int row = 0; row < 70; row++) {
            for (int col = 0; col < 70; col++) {
                cells[row][col] = new Cell(row, col);
                cellList.add(cells[row][col]);
                grid.add(cells[row][col]);
            }
        }
        for (int row = 0; row < 70; row++) {
            for (int col = 0; col < 70; col++) {
                if (row > 0) {
                    cells[row][col].addNeighbor(cells[row - 1][col]);
                }
                if (row < 69) {
                    cells[row][col].addNeighbor(cells[row + 1][col]);
                }
                if (col > 0) {
                    cells[row][col].addNeighbor(cells[row][col - 1]);
                }
                if (col < 69) {
                    cells[row][col].addNeighbor(cells[row][col + 1]);
                }
                if (row > 0 && col > 0) {
                    cells[row][col].addNeighbor(cells[row - 1][col - 1]);
                }
                if (row < 69 && col > 0) {
                    cells[row][col].addNeighbor(cells[row + 1][col - 1]);
                }
                if (row < 69 && col < 69) {
                    cells[row][col].addNeighbor(cells[row + 1][col + 1]);
                }
                if (row > 0 && col < 69) {
                    cells[row][col].addNeighbor(cells[row - 1][col + 1]);
                }
            }
        }
        header.setBorder(BorderFactory.createEmptyBorder());
        grid.setBorder(BorderFactory.createEmptyBorder());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        container.add(header, gbc);
        gbc.gridy = 1;
        container.add(grid, gbc);
        scrollPane = new JScrollPane(container);
        frame.add(scrollPane);
        frame.pack();
        frame.setVisible(true);
    }

    //initiate listeners for buttons and cells
    private void listeners() {
        cellList.forEach(c -> {
            c.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (running) {
                        return;
                    }
                    if (c.isAlive()) {
                        killCell(c);
                    } else {
                        raiseCell(c);
                    }
                }
            });
        });
        throttle.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                speed = throttle.getValue();
                timer.setDelay(speed);
            }

        });
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulate();
            }
        });
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause();
            }
        });
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        random.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillRandom();
            }
        });
        step.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                step();
            }
        });
        timer = new Timer(speed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    timer.stop();
                    return;
                }
                step();
            }
        });
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAbout();
            }
        });
    }

    //save file method.
    private void saveFile() {
        pause();
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".cgl")) {
                    file = new File(fileChooser.getSelectedFile() + ".cgl");
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                for (Cell c : cellList) {
                    if (c.isAlive()) {
                        bufferedWriter.write(c.getRow() + "|" + c.getCol() + ";");
                    }
                }
                bufferedWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Unable to open file", "File error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //open file method
    private void openFile() {
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                String[] input = bufferedReader.readLine().split(";");
                reset();
                for (String s : input) {
                    int row = Integer.parseInt(s.substring(0, s.indexOf("|")));
                    int col = Integer.parseInt(s.substring(s.indexOf("|") + 1, s.length()));
                    raiseCell(cells[row][col]);
                }
                bufferedReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Unable to open file\nCan not read or does not exist",
                        "File error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "File format not correct", "File error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //display about dialog
    private void showAbout() {
        JOptionPane.showMessageDialog(frame,
                "Conway's Game of Life\nimplemented by Justin Hinze\nprototype version.",
                "About", JOptionPane.PLAIN_MESSAGE);
    }

    //raises random cells
    private void fillRandom() {
        reset();
        cellList.forEach(c -> {
            if (rand.nextBoolean()) {
                raiseCell(c);
            }
        });
    }

    //adds a cell to the change set
    private void markChanged(Cell c) {
        if (!changeSet.contains(c)) {
            changeSet.add(c);
        }
    }

    //raises a cell and marks its neighbors changed
    private void raiseCell(Cell c) {
        c.setAlive(true);
        markNeighbors(c);
    }

    //kills a cell and marks its neighbors changed
    private void killCell(Cell c) {
        c.setAlive(false);
        markNeighbors(c);
    }

    //marks all cells in neighbor list changed
    private void markNeighbors(Cell c) {
        c.getNeighbors().forEach(e -> {
            markChanged(e);
        });
    }

    //begins a timer and starts simulation
    private void simulate() {
        start.setEnabled(false);
        random.setEnabled(false);
        step.setEnabled(false);
        running = true;
        timer.start();
    }

    //performs next step in simulation
    //each cell calculates its neighbors in parallel
    //depending on the condition of the cell and the number of neighbors
    //it is either killed, raised, or does nothing.
    //a sychronized set is used for the raise set and kill set.
    //after the cells are marked for death or raise they are processed for
    //the next iteration. Only the cells in the changelist are checked. This 
    //increases the speed of each iteration. A cell is not needed to be checked unless
    //it was effected by the last iteration.
    private void step() {
        changeSet.parallelStream().forEach(c -> {
            c.calcAliveNeighbors();
            if (c.getAliveNeighbors() == 3 && !c.isAlive()) {
                raiseSet.add(c);
            } else {
                if (c.isAlive() && (c.getAliveNeighbors() > 3 || c.getAliveNeighbors() < 2)) {
                    killSet.add(c);
                }
            }
        });
        changeSet.clear();
        raiseSet.forEach(c -> raiseCell(c));
        raiseSet.clear();
        killSet.forEach(c -> killCell(c));
        killSet.clear();
    }

    //pause the simulation
    private void pause() {
        this.running = false;
        start.setEnabled(true);
        random.setEnabled(true);
        step.setEnabled(true);
    }

    //reset the simulation and cells
    private void reset() {
        this.running = false;
        start.setEnabled(true);
        random.setEnabled(true);
        step.setEnabled(true);
        cellList.parallelStream().forEach(c -> c.reset());
    }

    public static void main(String[] args) {
        Conways game = new Conways();
    }

}
