package net.wordrider.files.ti68kformat;

/**
 *
 * @author Vity
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public final class TITextFileWriter extends TIFileWriter {
    private static final String fileHeader = "**TI92P*"; //readable headers
    private String textContent = "";
    private static final HashMap<Character, Character> UNICODE2TI;
    private int sum = 0;


    static {
        UNICODE2TI = new HashMap<Character, Character>(134);
        UNICODE2TI.put('\u00D0', (char) 18);
        UNICODE2TI.put('\u00BB', (char) 22);
        UNICODE2TI.put('\u00BC', (char) 28);
        UNICODE2TI.put('\u00BD', (char) 29);
        UNICODE2TI.put('\u00BE', (char) 30);
        UNICODE2TI.put('\u00BF', (char) 31);
        UNICODE2TI.put('\u20AC', (char) 128);
        UNICODE2TI.put('\u0081', (char) 129);
        UNICODE2TI.put('\u201A', (char) 130);
        UNICODE2TI.put('\u0192', (char) 131);
        UNICODE2TI.put('\u201E', (char) 132);
        UNICODE2TI.put('\u2026', (char) 133);
        UNICODE2TI.put('\u2020', (char) 134);
        UNICODE2TI.put('\u2021', (char) 135);
        UNICODE2TI.put('\u02C6', (char) 136);
        UNICODE2TI.put('\u2030', (char) 137);
        UNICODE2TI.put('\u0160', (char) 138);
        UNICODE2TI.put('\u2039', (char) 139);
        UNICODE2TI.put('\u0152', (char) 140);
        UNICODE2TI.put('\u008D', (char) 141);
        UNICODE2TI.put('\u017D', (char) 142);
        UNICODE2TI.put('\u008F', (char) 143);
        UNICODE2TI.put('\u0090', (char) 144);
        UNICODE2TI.put('\u2018', (char) 145);
        UNICODE2TI.put('\u2019', (char) 146);
        UNICODE2TI.put('\u201C', (char) 147);//patch
        UNICODE2TI.put('\u0081', (char) 129);
        UNICODE2TI.put('\u201D', (char) 148);
        UNICODE2TI.put('\u2013', (char) 149);
        UNICODE2TI.put('\u2014', (char) 150);
        UNICODE2TI.put('\u00C6', (char) 151);
        UNICODE2TI.put('\u02DC', (char) 152);
        UNICODE2TI.put('\u2122', (char) 153);
        UNICODE2TI.put('\u0161', (char) 154);
        UNICODE2TI.put('\u203A', (char) 155);
        UNICODE2TI.put('\u0153', (char) 156);
        UNICODE2TI.put('\u009D', (char) 157);
        UNICODE2TI.put('\u017E', (char) 158);
        UNICODE2TI.put('\u0178', (char) 159);
        UNICODE2TI.put('\u00A1', (char) 160);
        UNICODE2TI.put('\u00A2', (char) 161);
        UNICODE2TI.put('\u00A3', (char) 162);
        UNICODE2TI.put('\u00A4', (char) 163);
        UNICODE2TI.put('\u00A5', (char) 164);
        UNICODE2TI.put('\u00A6', (char) 165);
        UNICODE2TI.put('\u007C', (char) 166);
        UNICODE2TI.put('\u00A7', (char) 167);
        UNICODE2TI.put('\u00A7', (char) 168);
        UNICODE2TI.put('\u00A8', (char) 169);
        UNICODE2TI.put('\u00AA', (char) 170);
        UNICODE2TI.put('\u00D7', (char) 171);
        UNICODE2TI.put('\u00A9', (char) 172);
        UNICODE2TI.put('\u00AA', (char) 173);
        UNICODE2TI.put('\u00A8', (char) 174);
        UNICODE2TI.put('\u00AB', (char) 175);
        UNICODE2TI.put('\u00AC', (char) 176);
        UNICODE2TI.put('\u00AD', (char) 177);//plus/minus
        UNICODE2TI.put('\u00AE', (char) 178);
        UNICODE2TI.put('\u00AF', (char) 179);
        UNICODE2TI.put('\u00B0', (char) 180);
        UNICODE2TI.put('\u00B1', (char) 181);
        UNICODE2TI.put('\u00B6', (char) 182);
        UNICODE2TI.put('\u00B2', (char) 183);
        UNICODE2TI.put('\u00B3', (char) 184);
        UNICODE2TI.put('\u00B4', (char) 185);
        UNICODE2TI.put('\u00B5', (char) 186);
        UNICODE2TI.put('\u00DE', (char) 187);
        UNICODE2TI.put('\u00B6', (char) 188);
        UNICODE2TI.put('\u00B7', (char) 189);
        UNICODE2TI.put('\u00B8', (char) 190);
        UNICODE2TI.put('\u00B9', (char) 191);
        UNICODE2TI.put('\u00C0', (char) 192);
        UNICODE2TI.put('\u00C1', (char) 193);
        UNICODE2TI.put('\u00C2', (char) 194);
        UNICODE2TI.put('\u00C3', (char) 195);
        UNICODE2TI.put('\u00C4', (char) 196);
        UNICODE2TI.put('\u00C5', (char) 197);
        UNICODE2TI.put('\u00E6', (char) 198);
        UNICODE2TI.put('\u00C7', (char) 199);
        UNICODE2TI.put('\u00C8', (char) 200);
        UNICODE2TI.put('\u00C9', (char) 201);
        UNICODE2TI.put('\u00CA', (char) 202);
        UNICODE2TI.put('\u00CB', (char) 203);
        UNICODE2TI.put('\u00CD', (char) 204);
        UNICODE2TI.put('\u00CC', (char) 205);
        UNICODE2TI.put('\u00CD', (char) 206);
        UNICODE2TI.put('\u00CF', (char) 207);
        //UNICODE2TI.put(new Character('\u00D0'), new Character((char) 208));
        UNICODE2TI.put('\u00D0', (char) 18);
        UNICODE2TI.put('\u00D1', (char) 209);
        UNICODE2TI.put('\u00D2', (char) 210);
        UNICODE2TI.put('\u00D3', (char) 211);
        UNICODE2TI.put('\u00D4', (char) 212);
        UNICODE2TI.put('\u00D5', (char) 213);
        UNICODE2TI.put('\u00D6', (char) 214);
        UNICODE2TI.put('\u0078', (char) 215);
        UNICODE2TI.put('\u00D8', (char) 216);
        UNICODE2TI.put('\u00D9', (char) 217);
        UNICODE2TI.put('\u00DA', (char) 218);
        UNICODE2TI.put('\u00DB', (char) 219);
        UNICODE2TI.put('\u00DC', (char) 220);
        UNICODE2TI.put('\u00DD', (char) 221);
        UNICODE2TI.put('\u00FE', (char) 222);
        UNICODE2TI.put('\u00DF', (char) 223);
        UNICODE2TI.put('\u00E0', (char) 224);
        UNICODE2TI.put('\u00E1', (char) 225);
        UNICODE2TI.put('\u00E2', (char) 226);
        UNICODE2TI.put('\u00E3', (char) 227);
        UNICODE2TI.put('\u00E4', (char) 228);
        UNICODE2TI.put('\u00E5', (char) 229);
        UNICODE2TI.put('\u00E6', (char) 230);
        UNICODE2TI.put('\u00E7', (char) 231);
        UNICODE2TI.put('\u00E8', (char) 232);
        UNICODE2TI.put('\u00E9', (char) 233);
        UNICODE2TI.put('\u00EA', (char) 234);
        UNICODE2TI.put('\u00EB', (char) 235);
        UNICODE2TI.put('\u00EC', (char) 236);
        UNICODE2TI.put('\u00ED', (char) 237);
        UNICODE2TI.put('\u00EE', (char) 238);
        UNICODE2TI.put('\u00EF', (char) 239);
        UNICODE2TI.put('\u00F0', (char) 240);
        UNICODE2TI.put('\u00F1', (char) 241);
        UNICODE2TI.put('\u00F2', (char) 242);
        UNICODE2TI.put('\u00F3', (char) 243);
        UNICODE2TI.put('\u00F4', (char) 244);
        UNICODE2TI.put('\u00F5', (char) 245);
        UNICODE2TI.put('\u00F6', (char) 246);
        UNICODE2TI.put('\u00F7', (char) 247);
        UNICODE2TI.put('\u00F8', (char) 248);
        UNICODE2TI.put('\u00F9', (char) 249);
        UNICODE2TI.put('\u00FA', (char) 250);
        UNICODE2TI.put('\u00FB', (char) 251);
        UNICODE2TI.put('\u00FC', (char) 252);
        UNICODE2TI.put('\u00FD', (char) 253);
        UNICODE2TI.put('\u00FE', (char) 254);
        UNICODE2TI.put('\u00FF', (char) 255);
    }


    public TITextFileWriter() {
    }

    // --Commented out by Inspection START (26.2.05 17:32):
    //    public final String getTextContent() {
    //        return textContent;
    //    }
    // --Commented out by Inspection STOP (26.2.05 17:32)

    public final void setTextContent(final String textContent) {
        this.textContent = (textContent == null) ? "" : textContent;
    }

    protected final char getCheckSum() {
        return (char) (sum + 0xE0 + 0x1);
    }

    protected final byte getDataType() {
        return TIFileConstants.DATATYPE_TEXT;
    }

    protected final String getFileHeaderType() {
        return fileHeader;
    }


    protected final int getContentFileSize() {
        return 0x12 + textContent.length();
    }

    protected final void writeContent(final DataOutputStream stream) throws IOException {
        final char[] content = textContent.toCharArray();
        final int length = content.length;
        final int length4 = length + 0x4;

        stream.writeInt(length4);
        stream.writeByte(0x0);
        stream.writeByte(0x1);         //start of text
        //stream.writeByte(0x20);
        //writing content
        char ch;
        Character tiChar;
        sum = (length4 & 0xff) + ((length4 >> 8) & 0xff);
        for (int i = 0; i < length; ++i) {
            ch = content[i];
            if (ch >= 128) {
                tiChar = UNICODE2TI.get(new Character(ch));
                if (tiChar != null)
                    ch = tiChar;
                //  else ch = 0xFF;
            } else if (ch == '\n') ch = '\r';
            sum += ch;
            stream.writeByte(ch);
        }
        stream.writeShort(0xE0);//ende
    }

}
