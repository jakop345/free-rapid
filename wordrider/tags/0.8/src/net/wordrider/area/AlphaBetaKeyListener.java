package net.wordrider.area;

import net.wordrider.core.AppPrefs;

import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Vity
 */
public class AlphaBetaKeyListener implements KeyListener {
    //GAMA for old TI92PlusPc \u0083
    private static final char ALFA = '\u20AC', GAMA = '\u0192', GAMA_U = '\u201A', BETA = '\u0081', DELTA = '\u2026', DELTA_U = '\u201E', EPSILON = '\u2020', FI = '\u2018', LAMBDA = '\u2030', MY = '\u00B1', PI = '\u0152', PI_U = '\u2039', RO = '\u008D', SIGMA = '\u008F', SIGMA_U = '\u017D', TAU = '\u0090', OMEGA = '\u201D', OMEGA_U = '\u201C', XI = '\u0160', PSI = '\u2019', ZETA = '\u2021', INTEGRAL = '\u00b7', COMPLEX_NUMBER = '\u00c6', EXPONENT = '\u2014', THETA = '\u02C6', DERIVE = '\u00B6', CALC_MINUS = '\u00AA', UPPER_1 = '\u00b4', UPPER_2 = '\u00ae', UPPER_3 = '\u00af', INFINITY = '\u00b8', GREATER = '\u00bb', SQRT = '\u00a7';

    private static void insertChar(final KeyEvent e, final char lowerCh, final char upperCh) {
        final boolean shiftDown = e.isShiftDown();
        if (!(!shiftDown && lowerCh == 1)) {
//            if (e.getSource() instanceof JTextPane) {
//                final JTextPane pane = (JTextPane) e.getSource();
//                try {
//                    pane.getDocument().insertString(pane.getCaretPosition(), String.valueOf((shiftDown) ? upperCh : lowerCh), pane.getInputAttributes());
//                } catch (BadLocationException ex) {
//                    LogUtils.processException(logger, ex);
//                }
//            } else {
            final JTextComponent pane = (JTextComponent) e.getSource();
            pane.replaceSelection(String.valueOf((shiftDown) ? upperCh : lowerCh));
//            }
            e.consume();
        }
    }

    public void keyPressed(final KeyEvent e) {
        if (e.isAltDown() && !AppPrefs.getProperty(AppPrefs.ALT_KEY_FOR_MENU, false)) {
            final int code = e.getKeyCode();
            if (Character.isLetter(e.getKeyChar()) || code == KeyEvent.VK_QUOTE || code == KeyEvent.VK_QUOTEDBL || code == KeyEvent.VK_MINUS || code == KeyEvent.VK_1 || code == KeyEvent.VK_2 || code == KeyEvent.VK_3 || code == KeyEvent.VK_PERIOD || code == KeyEvent.VK_EQUALS || code == KeyEvent.VK_8 || code == KeyEvent.VK_SLASH) {
                char lower = 0, upper = 0;
                switch (code) {
                    case KeyEvent.VK_A:
                        lower = upper = ALFA;
                        break;
                    case KeyEvent.VK_B:
                        lower = upper = BETA;
                        break;
                    case KeyEvent.VK_D:
                        lower = DELTA;
                        upper = DELTA_U;
                        break;
                    case KeyEvent.VK_E:
                        lower = EPSILON;
                        upper = EXPONENT;
                        break;
                    case KeyEvent.VK_F:
                        lower = upper = FI;
                        break;
                    case KeyEvent.VK_G:
                        lower = GAMA;
                        upper = GAMA_U;
                        break;
                    case KeyEvent.VK_I:
                        lower = COMPLEX_NUMBER;
                        upper = INTEGRAL;
                        break;
                    case KeyEvent.VK_L:
                        lower = upper = LAMBDA;
                        break;
                    case KeyEvent.VK_M:
                    case KeyEvent.VK_U:
                        lower = upper = MY;
                        break;
                    case KeyEvent.VK_P:
                        lower = PI;
                        upper = PI_U;
                        break;
                    case KeyEvent.VK_R:
                        lower = upper = RO;
                        break;
                    case KeyEvent.VK_S:
                        lower = SIGMA;
                        upper = SIGMA_U;
                        break;
                    case KeyEvent.VK_T:
                        lower = TAU;
                        upper = THETA;
                        break;
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_O:
                        lower = OMEGA;
                        upper = OMEGA_U;
                        break;
                    case KeyEvent.VK_X:
                        lower = upper = XI;
                        break;
                    case KeyEvent.VK_Y:
                        lower = upper = PSI;
                        break;
                    case KeyEvent.VK_Z:
                        lower = upper = ZETA;
                        break;
                    case KeyEvent.VK_QUOTE:
                    case KeyEvent.VK_QUOTEDBL:
                        lower = upper = DERIVE;
                        break;
                    case KeyEvent.VK_MINUS:
                        lower = upper = CALC_MINUS;
                        break;
                    case KeyEvent.VK_1:
                        lower = 1;
                        upper = UPPER_1;
                        break;
                    case KeyEvent.VK_2:
                        lower = 1;
                        upper = UPPER_2;
                        break;
                    case KeyEvent.VK_3:
                        lower = 1;
                        upper = UPPER_3;
                        break;
                    case KeyEvent.VK_8:
                        if (e.isShiftDown()) {
                            insertChar(e, CALC_MINUS, CALC_MINUS);
                        }
                        lower = upper = INFINITY;
                        break;
                    case KeyEvent.VK_PERIOD:
                    case KeyEvent.VK_EQUALS:
                        lower = upper = GREATER;
                        break;
                    case KeyEvent.VK_SLASH:
                        lower = upper = SQRT;
                        break;
                    default:
                        break;
                }

                if (lower != 0)
                    insertChar(e, lower, upper);
            }
        } else if (!(e.isAltDown() || e.isShiftDown() || e.isControlDown()) && e.getKeyCode() == KeyEvent.VK_INSERT) {
            if (e.getSource() instanceof RiderArea) {
                final RiderArea pane = (RiderArea) e.getSource();
                pane.switchOverTypeMode();
            }
        }

    }

    public final void keyReleased(final KeyEvent e) {
    }

    public final void keyTyped(final KeyEvent e) {
    }
}
