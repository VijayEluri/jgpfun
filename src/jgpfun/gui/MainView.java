/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainView.java
 *
 * Created on Apr 16, 2010, 8:42:34 PM
 */
package jgpfun.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public class MainView extends javax.swing.JPanel {

    private int rps;

    private int progress;

    private final World2d world;


    /*
     * TODO:
     * add setters for things to draw or make a worldmodel including all stuff to draw
     * possibly make object lists for world objects, like bodies and food
     */
    /** Creates new form MainView */
    public MainView(final World2d world) {
        this.world = world;
        initComponents();

        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                world.clickEvent(p.x, p.y);
                //fixme: only repaint if necessary
                repaint();
            }


            @Override
            public void mousePressed(MouseEvent e) {
            }


            @Override
            public void mouseReleased(MouseEvent e) {
            }


            @Override
            public void mouseEntered(MouseEvent e) {
            }


            @Override
            public void mouseExited(MouseEvent e) {
            }

        });
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        world.draw(g);

        if (rps != 0) {
            g.setColor(Color.yellow);
            g.drawString("RPS: " + rps, 10, 15);
        }

        if (progress != 0) {
            g.setColor(Color.yellow);
            g.drawString("" + progress + "%", 10, 30);
        }
    }


    public void drawStuff(int rps, int progress) {
        this.rps = rps;
        this.progress = progress;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
