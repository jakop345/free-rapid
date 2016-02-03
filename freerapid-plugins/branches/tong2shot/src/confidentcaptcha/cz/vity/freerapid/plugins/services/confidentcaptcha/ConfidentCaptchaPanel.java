package cz.vity.freerapid.plugins.services.confidentcaptcha;

import cz.vity.freerapid.plugins.exceptions.CaptchaEntryInputMismatchException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author birchie
 */
class ConfidentCaptchaPanel extends JPanel {
    final int clicksNeeded;
    ConfidentCaptchaImageComponent captchaImage;
    List<JRadioButton> radioButtons;

    public ConfidentCaptchaPanel(final String imagesUrl, final String sequence, final int clicksNeeded) throws Exception {
        final JLabel labelMessage = new JLabel("Click the images in this order: ");
        final JLabel labelSequence = new JLabel("<html><b>"+sequence+"</b></html>", JLabel.CENTER);

        captchaImage = new ConfidentCaptchaImageComponent(imagesUrl);
        this.clicksNeeded = clicksNeeded;
        radioButtons = new ArrayList<JRadioButton>();

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.LINE_AXIS));
        progressPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        for (int i=0; i<clicksNeeded; i++) {
            JRadioButton newRB = new JRadioButton(" ", false);
            newRB.setEnabled(false);
            radioButtons.add(newRB);
            progressPanel.add(newRB);
        }
        JButton resetButton = new JButton("Reset");
        resetButton.setFocusable(false);
        progressPanel.add(resetButton);


        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.PAGE_AXIS));
        detailsPanel.add(labelMessage);
        detailsPanel.add(new JLabel(" "));
        detailsPanel.add(labelSequence);
        detailsPanel.add(new JLabel(" "));
        detailsPanel.add(progressPanel);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(captchaImage);
        this.add(new JLabel("   "));
        this.add(detailsPanel);


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                captchaImage.clearClicks();
            }
        });
        captchaImage.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                int ii = 0;
                int clicked = captchaImage.getClicks().size();
                for (JRadioButton rb : radioButtons) {
                    ii++;
                    if (ii <= clicked)
                        rb.setSelected(true);
                    else
                        rb.setSelected(false);
                }
            }
        });
    }

    public List<Point> getClickedPoints() throws Exception {
        if (captchaImage.getClicks().size() < clicksNeeded)
            throw new CaptchaEntryInputMismatchException("ERROR insufficient clicks");
        return captchaImage.getClicks().subList(0, clicksNeeded);
    }

    public Dimension getImageDimensions() {
        return captchaImage.getImageDimensions();
    }


    private static class ConfidentCaptchaImageComponent extends JLabel implements MouseListener {
        private int clickCount = 0;
        private List<Point> clickedPoints;

        public ConfidentCaptchaImageComponent(final String imagesUrl) throws Exception {
            super(new ImageIcon(new URL(imagesUrl)));

            clickedPoints = new ArrayList<Point>();
            addMouseListener(this);
        }

        void clearClicks() {
            clickedPoints.clear();
            firePropertyChange("clicked", true, false);
        }
        List<Point> getClicks() {
            return clickedPoints;
        }

        Dimension getImageDimensions() {
            return new Dimension(this.getIcon().getIconWidth(), this.getIcon().getIconHeight());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            final Point eventLocation = e.getPoint();
            clickedPoints.add(e.getPoint());
            firePropertyChange("clicked", true, false);
        }
        @Override
        public void mousePressed(MouseEvent e) { }
        @Override
        public void mouseReleased(MouseEvent e) { }
        @Override
        public void mouseEntered(MouseEvent e) { }
        @Override
        public void mouseExited(MouseEvent e) { }
    }

}