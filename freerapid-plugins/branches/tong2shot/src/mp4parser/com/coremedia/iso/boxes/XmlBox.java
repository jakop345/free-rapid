package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * <h1>4cc = "{@value #TYPE}"</h1>
 */
public class XmlBox extends AbstractFullBox {
    String xml = "";
    public static final String TYPE = "xml ";

    public XmlBox() {
        super(TYPE);
    }

    public String getXml() {
        if (!isParsed()) {
            parseDetails();
        }
        return xml;
    }

    public void setXml(String xml) {
        if (!isParsed()) {
            parseDetails();
        }
        this.xml = xml;
    }

    @Override
    protected long getContentSize() {
        return 4 + Utf8.utf8StringLengthInBytes(xml);
    }

    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        xml = IsoTypeReader.readString(content, content.remaining());
    }

    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(Utf8.convert(xml));
    }

    @Override
    public String toString() {
        if (!isParsed()) {
            parseDetails();
        }
        return "XmlBox{" +
                "xml='" + xml + '\'' +
                '}';
    }
}
