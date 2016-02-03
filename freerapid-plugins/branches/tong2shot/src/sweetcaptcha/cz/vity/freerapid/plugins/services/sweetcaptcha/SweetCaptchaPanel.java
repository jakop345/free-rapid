package cz.vity.freerapid.plugins.services.sweetcaptcha;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author birchie
 */
class SweetCaptchaPanel extends JPanel {
    private ArrayList<ImageLabel> imgItems;
    private int selected = -1;

    public SweetCaptchaPanel(final String verify, final String challenge,
                             final String imageTargetUrl, final ArrayList<String> imageItemUrls) throws Exception {
        final JLabel labelVerify = new JLabel(verify);
        final JLabel labelChallenge = new JLabel(challenge);
        final JLabel imgTarget = new JLabel(new ImageIcon(new URL(imageTargetUrl)));

        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.LINE_AXIS));
        itemPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        imgItems = new ArrayList<ImageLabel>();
        int count = 0;
        for (String itemUrl : imageItemUrls) {
            ImageLabel newItem = new ImageLabel(new ImageIcon(new URL(itemUrl)), count++);
            imgItems.add(newItem);
            itemPanel.add(newItem);
        }
        setSelectedImage(-1);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.PAGE_AXIS));
        detailsPanel.add(labelVerify);
        detailsPanel.add(new JLabel(" "));
        detailsPanel.add(labelChallenge);
        detailsPanel.add(new JLabel(" "));
        detailsPanel.add(itemPanel);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(detailsPanel);
        this.add(new JLabel(" "));
        this.add(imgTarget);
    }

    public int getSelected() {
        return selected;
    }

    private void setSelectedImage(final int index) {
        for (ImageLabel imgItem : imgItems) {
            if (imgItem.index == index)
                imgItem.setBorder(new LineBorder(Color.RED));
            else
                imgItem.setBorder(new LineBorder(Color.BLACK));
        }
        selected = index;
    }


    private class ImageLabel extends JButton {
        private int index;

        ImageLabel(Icon image, final int index) {
            super(image);
            this.index = index;
            this.setFocusable(false);
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setSelectedImage(index);
                }
            });
        }
    }

}