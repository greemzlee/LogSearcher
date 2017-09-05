package com.spbsplat.gui.viewer;


import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

/**
 * Компонент кнопки закрытий вкладки
 */
class ButtonTabComponent extends JPanel {
    private final FilesTabbedPane parent;

    public ButtonTabComponent(final FilesTabbedPane parent) {

        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (parent == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.parent = parent;
        setOpaque(false);

        JLabel label = new JLabel() {
            public String getText() {
                int i = parent.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) {
                    return parent.getTitleAt(i);
                }
                return null;
            }
        };

        add(label);
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        //tab button
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

    }

    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            int size = 20;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            setUI(new BasicButtonUI());

            setContentAreaFilled(false);

            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);

            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);

            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = parent.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                parent.removeFile(i);
            }
        }


        public void updateUI() {
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.RED);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}