/*
 * Justin Hinze
 * csc4380
 * Project 1
 */
package conways;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class Cell extends JPanel {

    private final int row;
    private final int col;
    private boolean alive = false;
    private int aliveNeighbors;
    private final List<Cell> neighbors = new ArrayList();
    private int hashcode;

    //consturctor
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.aliveNeighbors = 0;
        this.setSize(2, 2);
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        this.setBackground(Color.white);
        this.hashcode = row*100 + col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cell other = (Cell) obj;
        if (this.hashcode != other.hashcode) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        return hashcode;
    }

    //changes background to while if dead, dark gray if alive.
    private void updateBackground() {
        if (this.alive) {
            this.setBackground(Color.darkGray);
        } else {
            this.setBackground(Color.white);
        }
    }

    //returns boolean alive
    public boolean isAlive() {
        return this.alive;
    }

    //sets boolean alive and updates background
    public void setAlive(boolean b) {
        this.alive = b;
        this.updateBackground();
    }

    //resets cell
    public void reset() {
        this.aliveNeighbors = 0;
        this.setAlive(false);
    }

    //returns number of alive neighbors
    public int getAliveNeighbors() {
        return this.aliveNeighbors;
    }

    //calculates number of alive neighbors
    public void calcAliveNeighbors() {
        this.aliveNeighbors = 0;
        this.neighbors.forEach(c -> {
            if (c.isAlive()) {
                this.aliveNeighbors++;
            }
        });
    }
    
    //returns column of cell
    public int getCol() {
        return this.col;
    }

    //returns row of cell
    public int getRow() {
        return this.row;
    }

    //returns list of neighbors
    public List<Cell> getNeighbors() {
        return this.neighbors;
    }

    //adds neighbor
    public void addNeighbor(Cell c) {
        this.neighbors.add(c);
    }
  
}
