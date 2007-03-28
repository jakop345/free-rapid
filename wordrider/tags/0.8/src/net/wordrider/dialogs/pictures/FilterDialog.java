package net.wordrider.dialogs.pictures;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.actions.SaveAsFileAction;
import net.wordrider.dialogs.AppDialog;
import net.wordrider.dialogs.JButtonGroup;
import net.wordrider.dialogs.layouts.EqualsLayout;
import net.wordrider.dialogs.pictures.filters.*;
import net.wordrider.files.ti68kformat.TIImageFileInfo;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class FilterDialog extends AppDialog {
    private final JButton btnSave = Swinger.getButton("dialog.images.btn.save");
    private final JButton btnCancel = Swinger.getButton("dialog.images.btn.cancel");
    private final JButton btnReset = Swinger.getButton("dialog.images.btn.reset");
    private final PicturePanel panelOutputPicture = new PicturePanel();

    public JScrollPane getScrollPaneInput() {
        return scrollPaneInput;
    }

    private final JScrollPane scrollPaneInput = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private final JLabel labelFileInputPath = new JLabel();
    private final JCheckBox checkSaveSettings = Swinger.getCheckBox("dialog.images.checkSaveSettings");
    private final JCheckBox checkCropSelection = Swinger.getCheckBox("dialog.images.cropcheck");
    private final JSpinner spinnerWidth = new JSpinner();
    private final JSlider sliderBrightness = new JSlider();

    private final JRadioButton radioBgWhite = Swinger.getRadio("dialog.images.white");
    private final JRadioButton radioBgLCD = Swinger.getRadio("dialog.images.lcd");

    private JComboBox comboFilter;
    private final JSlider sliderContrast = new JSlider();
    private final JSpinner spinnerHeight = new JSpinner();
    private JComboBox comboPosition;
    private final InputPicturePanel panelInputPicture = new InputPicturePanel(this);
    private final JLabel labelInputImageSize = new JLabel();

    private static final int FILTER_INPUT_PICTURE = 0; //0
    static final int FILTER_SELECTION = 1; //1
    private static final int FILTER_CROPFIT = FILTER_SELECTION + 1; //2
    private static final int FILTER_BRIGHTNESS = FILTER_CROPFIT + 1; //3
    private static final int FILTER_CONTRAST = FILTER_BRIGHTNESS + 1; //4
    private static final int FILTER_DITHER = FILTER_CONTRAST + 1; //5
    private static final int FILTER_SCREEN = FILTER_DITHER + 1; //6

    private static final int FILTER_COUNT = 7;

    private final FilterPlugin[] filterPlugins = new FilterPlugin[FILTER_COUNT];

    private static final int TI89SCREENWIDTH = 152; //TI89SCREENWIDTH
    private static final int TI89SCREENHEIGHT = 79;
    private static final int TI92SCREENWIDTH = 240;
    private static final int TI92SCREENHEIGHT = 120;
    private static final int POSITION_CROP = 0;
    private static final int POSITION_FITWIDTH = 1;
    private static final int POSITION_FITHEIGHT = 2;
    private static final int POSITION_STRETCH = 3;
    private final File file;
    private File outputFile = null;

    private TIImageFileInfo fileInfo = null;
    private final ChangeListener spinnerChangeListener = new SpinnerChangeListener();

    private static final int SRC_SPINNER_WIDTH = 0;
    private static final int SRC_SPINNER_HEIGHT = 1;
    private static final int SRC_INIT = 2;
    static final int SRC_SELECTIONCHANGED = 3;
    private static final int SRC_CROPONOFF = 4;
    private static final int SRC_POSITION = 5;
    private static final int SRC_FILTER = 6;
    private static final int SRC_BRIGHTNESS = 7;
    private static final int SRC_CONTRAST = 8;
    private static final int SRC_BACKGROUND = 9;

    private static final String PROPERTY_IMAGES_CROP = "images.crop";
    private static final String PROPERTY_IMAGES_FILTER = "images.filter";
    private static final String PROPERTY_IMAGES_POSITION = "images.position";
    private static final String PROPERTY_IMAGES_BRIGHTNESS = "images.brightness";
    private static final String PROPERTY_IMAGES_CONTRAST = "images.contrast";
    private static final String PROPERTY_IMAGES_BACKGROUND = "images.background";

    private final boolean formatTI92;
    private final int imageScreenWidth;
    private final int imageScreenHeight;
    //private FilterManager filterManager = new FilterManager();
    private final static Logger logger = Logger.getLogger(FilterDialog.class.getName());
    private final JLabel labelInfoPosition = new JLabel();

    public FilterDialog(final Frame owner, final File file, final Image inputImage) throws HeadlessException {
        super(owner, true);    //call to super
        formatTI92 = AppPrefs.getProperty(AppPrefs.TI92IMAGEFORMAT, false);
        if (formatTI92) {
            imageScreenWidth = TI92SCREENWIDTH;
            imageScreenHeight = TI92SCREENHEIGHT;
        } else {
            imageScreenWidth = TI89SCREENWIDTH;
            imageScreenHeight = TI89SCREENHEIGHT;
        }
        this.file = file;
        try {
            init();
            initValues();
            initFilters(inputImage);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        this.pack();
        Swinger.centerDialog(owner, this);
        this.setModal(true);
        this.setTitle(Lng.getLabel("dialog.images.title"));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    void updateLabelInfoPosition(String s) {
        labelInfoPosition.setText(s);
    }

    boolean isCropEnabled() {
        return checkCropSelection.isSelected();
    }

    private final class SpinnerChangeListener implements ChangeListener {
        public final void stateChanged(final ChangeEvent e) {
            if (e.getSource().equals(spinnerWidth)) {
                final Number value = (Number) spinnerWidth.getValue();
                final int val = value.intValue();
                if (val % 8 != 0) {
                    setSpinnerValue(spinnerWidth, WidthSpinnerModel.getNextHigh(val, imageScreenWidth));
                }
                updateFilter(FILTER_CROPFIT, SRC_SPINNER_WIDTH);
            } else updateFilter(FILTER_CROPFIT, SRC_SPINNER_HEIGHT);

        }
    }


    private final class CropSelectionChangeStatus implements ItemListener {
        public final void itemStateChanged(final ItemEvent e) {
            updateFilter(FILTER_SELECTION, SRC_CROPONOFF);
        }
    }

    private final class OutputScreenListener implements ItemListener {
        public final void itemStateChanged(final ItemEvent e) {
            panelOutputPicture.setLCDBackground(radioBgLCD.isSelected(), formatTI92);
            updateFilter(FILTER_SCREEN, SRC_BACKGROUND);
        }
    }

    private void initValues() {
        checkCropSelection.setSelected(AppPrefs.getProperty(PROPERTY_IMAGES_CROP, true));
        radioBgWhite.setSelected(AppPrefs.getProperty(PROPERTY_IMAGES_BACKGROUND, true));
        final WidthSpinnerModel widthModel = new WidthSpinnerModel();
        spinnerWidth.setModel(widthModel);
        widthModel.setValue(new Integer(imageScreenWidth));
        widthModel.setMinimum(8);
        widthModel.setMaximum(imageScreenWidth);
        ((SpinnerNumberModel) spinnerHeight.getModel()).setMaximum(imageScreenHeight);
        ((SpinnerNumberModel) spinnerHeight.getModel()).setMinimum(1);
        spinnerHeight.setValue(imageScreenHeight);
        spinnerWidth.addChangeListener(spinnerChangeListener);
        spinnerHeight.addChangeListener(spinnerChangeListener);
        final SliderChangeListener sliderChangeListener = new SliderChangeListener();
        sliderBrightness.setMinimum(-50);
        sliderBrightness.setMaximum(50);
        sliderBrightness.setValue(AppPrefs.getProperty(PROPERTY_IMAGES_BRIGHTNESS, 0));
        sliderBrightness.setMajorTickSpacing(50);
        sliderBrightness.setPaintTicks(true);
        sliderBrightness.addChangeListener(sliderChangeListener);
        sliderContrast.setMinimum(-50);
        sliderContrast.setMaximum(50);
        sliderContrast.setValue(AppPrefs.getProperty(PROPERTY_IMAGES_CONTRAST, 0));
        sliderContrast.setMajorTickSpacing(50);
        if (file != null) {
            final String path = file.getPath();
            labelFileInputPath.setText(Lng.getLabel("dialog.images.filename", file.getName()));
            labelFileInputPath.setToolTipText(path);
        } else labelFileInputPath.setText("");
        sliderContrast.setPaintTicks(true);
        sliderContrast.addChangeListener(sliderChangeListener);
        comboFilter.setSelectedIndex(AppPrefs.getProperty(PROPERTY_IMAGES_FILTER, 0));
        comboPosition.setSelectedIndex(AppPrefs.getProperty(PROPERTY_IMAGES_POSITION, 0));
        final ItemListener changeListener = new ComboChangeListener();
        comboFilter.addItemListener(changeListener);
        comboPosition.addItemListener(changeListener);
        checkCropSelection.addItemListener(new CropSelectionChangeStatus()); //!
        final ItemListener outputScreenListener = new OutputScreenListener();
        radioBgLCD.addItemListener(outputScreenListener);
        radioBgWhite.addItemListener(outputScreenListener);
    }

    private final class SliderChangeListener implements ChangeListener {
        public final void stateChanged(final ChangeEvent e) {
            if (e.getSource().equals(sliderContrast)) {
                updateFilter(FILTER_CONTRAST, SRC_CONTRAST);
            } else updateFilter(FILTER_BRIGHTNESS, SRC_BRIGHTNESS);
        }
    }

    private final class ComboChangeListener implements ItemListener {
        public final void itemStateChanged(final ItemEvent e) {
            if (e.getSource().equals(comboPosition))
                updateFilter(FILTER_CROPFIT, SRC_POSITION);
            else
                updateFilter(FILTER_DITHER, SRC_FILTER);
        }
    }

    /**
     * Input Image filter
     */
    private final class InputImageFilterPlugin extends FilterPlugin {
        public InputImageFilterPlugin(final Image inputImage) {
            super();
            //  this.fileName = fileName;
            generatedImage = inputImage;
        }

        public final Image updateFilter(final Image inputImage, final int source) {
            freeActualImage();
            //generatedImage = loadInputImage(fileName);
            panelInputPicture.setImg(generatedImage = inputImage);
            return inputImage;
        }
    }

    /**
     * Dither Image filter
     */
    private final class DitherFilterPlugin extends FilterPlugin {

        private final DitherRaster[] raster = {new QuantizeRaster(), new OrderedDither(), new OrderedFilter4x4(), new ErrorDiffusion(),
                new JarvisErrorDiffusion(), new Shiau2ErrorDiffusion(), new StuckiErrorDiffusion(), new BurkeFilter(), new OrderedFilter2x2(), new OrderedFilter3x3_1(), new OrderedFilter3x3_2(), new RandomRaster()};

        public final Image updateFilter(final Image inputImage, final int source) {
            freeActualImage();
            return generatedImage = createImage(new FilteredImageSource(inputImage.getSource(), raster[comboFilter.getSelectedIndex()]));
        }
    }

    /**
     * Selection Image filter
     */
    private final class SelectionFilterPlugin extends FilterPlugin {
        public final Image updateFilter(final Image inputImage, final int source) {
            freeActualImage();
            if (checkCropSelection.isSelected() && panelInputPicture.isSelection()) {
                final Rectangle rect = panelInputPicture.getSelectionRectangle();
                generatedImage = createImage(new FilteredImageSource(inputImage.getSource(), new CropImageFilter(rect.x, rect.y, rect.width, rect.height)));
            } else generatedImage = inputImage;
            return generatedImage;
        }
    }

    public final TIImageFileInfo getFileInfo() {
        return fileInfo;
    }

    private final class CropFitFilterPlugin extends FilterPlugin {
        public final Image updateFilter(final Image inputImage, final int source) {
            int width = getOutputImgWidth();
            int height = getOutputImgHeight();

            final int currentSelected = comboPosition.getSelectedIndex();
            final ImageFilter cropFilter;
            freeActualImage();
            switch (currentSelected) {
                case POSITION_CROP:
                    //cropFilter = new CropImageFilter(rect.x, rect.y, Math.min(rect.width, width), Math.min(rect.height, height));
                    //  if (inputImage.getWidth(null) > width || inputImage.getHeight(null) > height)
                    //if (source == SRC_SELECTIONCHANGED || S)
                    if (source != SRC_SPINNER_WIDTH && source != SRC_SPINNER_HEIGHT) {
                        setSpinnerValue(spinnerWidth, width = getNextHighWidth(inputImage.getWidth(null)));
                        setSpinnerValue(spinnerHeight, height = Math.min(inputImage.getHeight(null), imageScreenHeight));
                    }
                    cropFilter = new CropImageFilter(0, 0, width, height);
                    //else return inputImage;
                    break;
                case POSITION_FITWIDTH:
                    cropFilter = new AreaAveragingScaleFilter(width, -1);
                    break;
                case POSITION_FITHEIGHT:
                    cropFilter = new AreaAveragingScaleFilter(-1, height);
                    break;
                case POSITION_STRETCH: //stretch
                    cropFilter = new AreaAveragingScaleFilter(width, height);
                    break;
                default:
                    return generatedImage = inputImage;
            }
            generatedImage = createImage(new FilteredImageSource(inputImage.getSource(), cropFilter));
            if (currentSelected == POSITION_FITHEIGHT) {
                if (source == SRC_SELECTIONCHANGED || source == SRC_POSITION || source == SRC_CROPONOFF) {
                    final int newWidth = getNextHighWidth(generatedImage.getWidth(null));
                    if (newWidth != width) {
                        setSpinnerValue(spinnerWidth, newWidth);
                        width = newWidth;
                    }
                }
                generatedImage = makeCropImage(width, height);
            } else if (currentSelected == POSITION_FITWIDTH) {
                if (source == SRC_SELECTIONCHANGED || source == SRC_POSITION || source == SRC_CROPONOFF) {
                    final int newHeight = Math.min(generatedImage.getHeight(null), imageScreenHeight);
                    if (newHeight != height) {
                        setSpinnerValue(spinnerHeight, newHeight);
                        height = newHeight;
                    }
                }
                generatedImage = makeCropImage(width, height);
            }

            return generatedImage;
        }

        private Image makeCropImage(final int width, final int height) {
            return createImage(new FilteredImageSource(generatedImage.getSource(), new CropImageFilter(0, 0, width, height)));
        }

    }

    private int getNextHighWidth(final int value) {
        if (value > imageScreenWidth)
            return imageScreenWidth;
        if (value % 8 == 0)
            return value;
        return WidthSpinnerModel.getNextHigh(value);
    }

    private int getOutputImgHeight() {
        return ((Number) spinnerHeight.getValue()).intValue();
    }

    private int getOutputImgWidth() {
        return ((Number) spinnerWidth.getValue()).intValue();
    }

    private final class ContrastFilterPlugin extends FilterPlugin {
        private final ContrastFilter contrastFilter = new ContrastFilter();

        public final Image updateFilter(final Image inputImage, final int source) {
            freeActualImage();
            final int inputValue = sliderContrast.getValue();
            if (inputValue != 0) {
                contrastFilter.setGain(inputValue * 2);
                generatedImage = createImage(new FilteredImageSource(inputImage.getSource(), contrastFilter));
            } else
                generatedImage = inputImage;
            return generatedImage;
        }
    }

    private final class BrigthnessFilterPlugin extends FilterPlugin {
        private final BrightnessFilter brightnessFilter = new BrightnessFilter();

        public final Image updateFilter(final Image inputImage, final int source) {
            final int inputValue = sliderBrightness.getValue();
            freeActualImage();
            if (inputValue != 0) {
                brightnessFilter.setBrightness(inputValue * 2);
                generatedImage = createImage(new FilteredImageSource(inputImage.getSource(), brightnessFilter));
            } else
                generatedImage = inputImage;
            return generatedImage;
        }
    }

    private final class ScreenFilterPlugin extends FilterPlugin {
        private final ImageFilter alphaFilter = new AlphaFilter();

        public final Image updateFilter(final Image inputImage, final int source) {
            freeActualImage();
            if (radioBgLCD.isSelected()) {
                generatedImage = createImage(new FilteredImageSource(inputImage.getSource(), alphaFilter));
            } else
                generatedImage = inputImage;
            return generatedImage;
        }
    }

    private void initFilters(final Image inputImage) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //filterPlugins[FILTER_INPUT_PICTURE] = new InputImageFilterPlugin("hatgray.jpg");
        filterPlugins[FILTER_INPUT_PICTURE] = new InputImageFilterPlugin(inputImage);
        filterPlugins[FILTER_BRIGHTNESS] = new BrigthnessFilterPlugin();
        filterPlugins[FILTER_CONTRAST] = new ContrastFilterPlugin();
        filterPlugins[FILTER_CROPFIT] = new CropFitFilterPlugin();
        filterPlugins[FILTER_DITHER] = new DitherFilterPlugin();
        filterPlugins[FILTER_SELECTION] = new SelectionFilterPlugin();
        filterPlugins[FILTER_SCREEN] = new ScreenFilterPlugin();
        updateFilter(FILTER_INPUT_PICTURE, SRC_INIT);
        labelInputImageSize.setText(Lng.getLabel("dialog.images.imagesize", new Object[]{
                inputImage.getWidth(null), inputImage.getHeight(null)}));
        radioBgLCD.setSelected(!radioBgWhite.isSelected());
    }

    void updateFilter(final int filterID, final int sourceID) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                {
                    FilterDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    Image out = (filterID > FILTER_INPUT_PICTURE) ? filterPlugins[filterID - 1].getGeneratedImage() : filterPlugins[FILTER_INPUT_PICTURE].getGeneratedImage();
                    for (int i = filterID; i < FILTER_COUNT; ++i) {
                        out = filterPlugins[i].updateFilter(out, sourceID);
                    }
                    panelOutputPicture.setImg(out);
                    FilterDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

    }

    private final class ClickButtonAction extends AbstractAction {
        public final void actionPerformed(final ActionEvent e) {
            if (e.getSource().equals(btnSave)) {
                fileInfo = new TIImageFileInfo();
                if (file != null) {
                    final String fn = Utils.getPureFilename(file);
                    if (fn.length() > 0)
                        fileInfo.setVarName(fn);
                }
                if (checkSaveSettings.isSelected()) {
                    AppPrefs.storeProperty(PROPERTY_IMAGES_CROP, checkCropSelection.isSelected());
                    AppPrefs.storeProperty(PROPERTY_IMAGES_BACKGROUND, radioBgWhite.isSelected());
                    AppPrefs.storeProperty(PROPERTY_IMAGES_CONTRAST, sliderContrast.getValue());
                    AppPrefs.storeProperty(PROPERTY_IMAGES_BRIGHTNESS, sliderBrightness.getValue());
                    AppPrefs.storeProperty(PROPERTY_IMAGES_FILTER, comboFilter.getSelectedIndex());
                    AppPrefs.storeProperty(PROPERTY_IMAGES_POSITION, comboPosition.getSelectedIndex());
                }
                final File file = SaveAsFileAction.saveAsImageProcess(fileInfo, panelOutputPicture.getImg());
                if (file != null) {
                    FilterDialog.this.setOutputFile(file);
                    FilterDialog.this.doClose();
                }
            } else if (e.getSource().equals(btnCancel)) {
                FilterDialog.this.doClose();

            } else if (e.getSource().equals(btnReset)) {
                radioBgWhite.setSelected(AppPrefs.getProperty(PROPERTY_IMAGES_BACKGROUND, true));
                radioBgLCD.setSelected(!radioBgWhite.isSelected());
                checkCropSelection.setSelected(AppPrefs.getProperty(PROPERTY_IMAGES_CROP, true));
                sliderContrast.setValue(AppPrefs.getProperty(PROPERTY_IMAGES_CONTRAST, 0));
                sliderBrightness.setValue(AppPrefs.getProperty(PROPERTY_IMAGES_BRIGHTNESS, 0));
                comboFilter.setSelectedIndex(AppPrefs.getProperty(PROPERTY_IMAGES_FILTER, 0));
                comboPosition.setSelectedIndex(AppPrefs.getProperty(PROPERTY_IMAGES_POSITION, 0));
            }
        }
    }

    public final File getOutputFile() {
        return outputFile;
    }

    private void setOutputFile(final File file) {
        this.outputFile = file;
    }

    protected final AbstractButton getCancelButton() {
        return btnCancel;
    }

    protected final AbstractButton getOkButton() {
        return btnSave;
    }

    private void setSpinnerValue(final JSpinner spinner, final Integer value) {
        spinner.removeChangeListener(spinnerChangeListener);
        spinner.getModel().setValue(value);
        spinner.addChangeListener(spinnerChangeListener);
    }

    private void setSpinnerValue(final JSpinner spinner, final int value) {
        spinner.removeChangeListener(spinnerChangeListener);
        spinner.getModel().setValue(value);
        spinner.addChangeListener(spinnerChangeListener);
    }

    private static class CellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setComponentOrientation(list.getComponentOrientation());
            final JLabel label = (JLabel) value;
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setIcon(label.getIcon());
            setIconTextGap(10);
            setText(label.getText());
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
            return this;
        }
    }

    private void init() throws Exception {
        final JPanel panelCmd = new JPanel(new BorderLayout(5, 4));
        panelCmd.setBorder(BorderFactory.createCompoundBorder(panelCmd.getBorder(), BorderFactory.createEmptyBorder(2, 4, 2, 6)));
        final JPanel panelBtn = new JPanel(new EqualsLayout(5));
        final JPanel panelCenter = new JPanel(new BorderLayout());
        final JPanel panelPictures = new JPanel(new GridBagLayout());
        final JLabel labelOutputPicture = Swinger.getLabel("dialog.images.outputpicture");
        final JLabel labelInputPicture = Swinger.getLabel("dialog.images.inputpicture");
        final JPanel panelOptions = new JPanel(new GridBagLayout());
        final JLabel labelBrightness = Swinger.getLabel("dialog.images.brightness", "light.gif");
        final JLabel labelWidth = Swinger.getLabel("dialog.images.width");
        final JLabel labelFilter = Swinger.getLabel("dialog.images.filter");
        final JLabel labelContrast = Swinger.getLabel("dialog.images.contrast", "contrast.gif");
        final JLabel labelHeight = Swinger.getLabel("dialog.images.height");
        final Container contentPane = this.getContentPane();
        panelInputPicture.setBackground(Color.GRAY);
        contentPane.setLayout(new BorderLayout());
        final Border titledBorder2 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(148, 145, 140)), Lng.getLabel("dialog.images.options"));

        final JLabel labelPosition = Swinger.getLabel("dialog.images.position");
        labelPosition.setLabelFor(comboPosition);

        final String orderLabel = Lng.getLabel("dialog.images.ordered");
        comboFilter = new JComboBox(new Object[]{Lng.getLabel("dialog.images.quantize"),
                orderLabel + " 4x4 #1", orderLabel + " 4x4 #2", "Floyd-Steinberg", "Jarvis", "Shiau", "Stucki", "Burke", orderLabel + " 2x2", orderLabel + " 3x3 #1", orderLabel + " 3x3 #2", Lng.getLabel("dialog.images.random")});
        comboPosition = new JComboBox(new Object[]{Swinger.getLabel("dialog.images.crop", "crop.gif"),
                Swinger.getLabel("dialog.images.fitwidth", "res-width.gif"), Swinger.getLabel("dialog.images.fitheight", "res-height.gif"),
                Swinger.getLabel("dialog.images.stretch", "res-both.gif")});
        comboPosition.setRenderer(new CellRenderer());
        final ActionListener buttonListener = new ClickButtonAction();
        btnSave.addActionListener(buttonListener);
        btnCancel.addActionListener(buttonListener);
        btnReset.addActionListener(buttonListener);

        panelBtn.setPreferredSize(new Dimension(239, 30));

        labelOutputPicture.setFont(labelOutputPicture.getFont().deriveFont(Font.BOLD));
        labelInputPicture.setFont(labelOutputPicture.getFont());
        final Dimension outputDimension = new Dimension(imageScreenWidth + 2, imageScreenHeight + 2);
        panelOutputPicture.setPreferredSize(outputDimension);
        panelOutputPicture.setMaximumSize(outputDimension);
        panelOutputPicture.setMinimumSize(outputDimension);

        panelPictures.setBorder(BorderFactory.createLineBorder(Color.black));
        labelBrightness.setLabelFor(sliderBrightness);

        final JLabel labelOutputImageSize = Swinger.getLabel((this.formatTI92) ? "dialog.images.maxsizeTi92" : "dialog.images.maxsize");

        labelFilter.setMaximumSize(new Dimension(23, 15));
        labelFilter.setLabelFor(comboFilter);

        labelContrast.setLabelFor(sliderContrast);
        panelOptions.setBorder(titledBorder2);
        scrollPaneInput.setPreferredSize(new Dimension(200, 100));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        scrollPaneInput.getViewport().setBackground(Color.LIGHT_GRAY);
        labelInfoPosition.setSize(new Dimension(165, 15));
        labelInfoPosition.setMaximumSize(new Dimension(165, 15));
        labelInfoPosition.setMinimumSize(new Dimension(165, 15));

        final Dimension buttonSize = new Dimension(75, 25);

        btnCancel.setMinimumSize(buttonSize);
        btnSave.setMinimumSize(buttonSize);
        btnReset.setMinimumSize(buttonSize);
        panelCmd.add(checkSaveSettings, BorderLayout.WEST);
        panelCmd.add(panelBtn, BorderLayout.CENTER);
        panelBtn.add(btnSave);
        panelBtn.add(btnCancel);
        panelBtn.add(btnReset);

        contentPane.add(panelCenter, BorderLayout.CENTER);
        contentPane.add(panelCmd, BorderLayout.SOUTH);
        panelCenter.add(panelPictures, BorderLayout.CENTER);
        final JPanel panelBackground = new JPanel();
        panelBackground.setLayout(new BoxLayout(panelBackground, BoxLayout.X_AXIS));
        final JButtonGroup group = new JButtonGroup();
        group.add(radioBgLCD);
        group.add(radioBgWhite);
        panelBackground.setBorder(null);
        panelBackground.add(Swinger.getLabel("dialog.images.background"));
        panelBackground.add(radioBgWhite);
        panelBackground.add(radioBgLCD);

        panelPictures.add(labelInputPicture, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 4, 2, 0), 0, 0));
        panelPictures.add(labelInputImageSize, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 4, 4, 4), 0, 0));
        panelPictures.add(labelFileInputPath, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 4, 4, 4), 0, 0));
        panelPictures.add(scrollPaneInput, new GridBagConstraints(0, 2, 2, 1, 1.0, 2.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 3, 0, 7), 70, 40));
        panelPictures.add(checkCropSelection, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(4, 4, 6, 7), 0, 0));
        panelPictures.add(labelInfoPosition, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 6, 0), 0, 0));
        panelPictures.add(labelOutputPicture, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 4, 2, 0), 0, 0));
        panelPictures.add(labelOutputImageSize, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 4, 2, 0), 0, 0));
        panelPictures.add(panelOutputPicture, new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 20, 2, 4), 0, 0));
        panelPictures.add(panelBackground, new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 2, 4), 0, 0));

        scrollPaneInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneInput.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneInput.getViewport().add(panelInputPicture, null);
        ///scrollPaneInput.getViewport().setFocusable(true);
        //scrollPaneInput.setFocusable(true);

        panelInputPicture.init();
        final JPanel sizePanel = new JPanel(new GridBagLayout());
        final JPanel brightContrastPanel = new JPanel(new GridBagLayout());
        final Dimension spinnerDimension = new Dimension(45, 20);
        spinnerWidth.setPreferredSize(spinnerDimension);
        spinnerHeight.setPreferredSize(spinnerDimension);
        sizePanel.setBorder(null);
        brightContrastPanel.setBorder(null);
        sizePanel.add(labelWidth, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 10), 0, 0));
        sizePanel.add(spinnerWidth, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 2));
        sizePanel.add(labelHeight, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 3, 10), 0, 0));
        sizePanel.add(spinnerHeight, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 2));
        panelOptions.add(sizePanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 0), 0, 0));

        panelOptions.add(comboPosition, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
        panelOptions.add(comboFilter, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 10, 5, 0), 0, 0));
        final Integer value = 999;
        final String longText = Lng.formatLabel(Lng.getLabel("dialog.images.cropinfo"), new Object[]{value, value, value, value});
        final Dimension preferredSize = labelInfoPosition.getMinimumSize();
        preferredSize.width = labelInfoPosition.getFontMetrics(labelInfoPosition.getFont()).stringWidth(longText);
        labelInfoPosition.setPreferredSize(preferredSize);

        brightContrastPanel.add(labelBrightness, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        brightContrastPanel.add(sliderBrightness, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        brightContrastPanel.add(labelContrast, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 10, 0, 0), 0, 0));
        brightContrastPanel.add(sliderContrast, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 5, 10), 0, 0));
        panelOptions.add(brightContrastPanel, new GridBagConstraints(2, 0, 1, 3, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 10, 0), 0, 0));

        panelOptions.add(labelFilter, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 6, 0), 0, 0));
        panelOptions.add(labelPosition, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 10, 6, 0), 0, 0));
        panelCenter.add(panelOptions, BorderLayout.SOUTH);
    }
}