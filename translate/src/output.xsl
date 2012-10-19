<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output encoding="UTF-8" indent="yes" omit-xml-declaration="yes" method="html"/>

    <xsl:template match="/">
        <xsl:apply-templates select="//translation"/>
    </xsl:template>

    <xsl:template match="translation">
        <html>
            <head>
                <title>FreeRapid translation utility</title>
                <style>
                    .missing {font-color:red;}
                    .title {
                    font-size: large;
                    }
                    body {
                    font-family: monospace;
                    }
                    body {
                    padding: 15px;
                    }
                    table.sample {
                    border-width: 2px;
                    border-spacing: 4px;
                    border-style: solid;
                    border-color: green;
                    padding: 4px;
                    border-collapse: collapse;
                    background-color: white;
                    }
                    table.sample th {
                    border-width: 1px;
                    padding: 6px;
                    border-style: dotted;
                    border-color: gray;
                    background-color: white;
                    -moz-border-radius: ;
                    }
                    table.sample td {
                    border-width: 1px;
                    padding: 6px;
                    border-style: dotted;
                    border-color: gray;
                    background-color: white;
                    }
                </style>
            </head>
            <body>
                <h1>FreeRapid translation utility - results</h1>
                <xsl:if test="not (propertiesFiles)">
                    <xsl:text>Well done! Translation is complete.</xsl:text>
                </xsl:if>
                <xsl:apply-templates select="propertiesFile"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="propertiesFile">
        <table class="sample">
            <thead>
                <tr style="font-size: large">
                    <xsl:choose>
                        <xsl:when test="@missing='true'">
                            <span class="missing">
                                <xsl:value-of select="' File '"/>
                                <b>
                                    <xsl:value-of select="@name"/>
                                </b>
                                <xsl:value-of select="' is missing!'"/>
                            </span>
                        </xsl:when>
                        <xsl:otherwise>
                            <span class="title">
                                <xsl:text>Properties file: </xsl:text>
                                <i>
                                    <xsl:value-of select="@name"/>
                                </i>
                            </span>
                        </xsl:otherwise>
                    </xsl:choose>
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates select="missing"/>
                <xsl:apply-templates select="removing"/>
            </tbody>
        </table>
        <br/>
        <br/>
        <br/>
        <br/>
    </xsl:template>

    <xsl:template match="missing">
        <tr style="padding-top:20px;">
            <td style="color:green; font-size: large;font-family:arial,verdana">Strings to be added</td>
        </tr>
        <tr>
            <td>
                <xsl:apply-templates select="property"/>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="removing">
        <tr style="padding-top:20px;">
            <td style="color:red; font-size: large;font-family:arial,verdana">Strings to be removed</td>
        </tr>
        <tr>
            <td>
                <xsl:apply-templates select="property"/>
            </td>
        </tr>
    </xsl:template>


    <xsl:template match="property">
        <xsl:choose>
            <xsl:when test="@value">
                <b>
                    <xsl:value-of select="@key"/>
                </b>
                <xsl:text>=</xsl:text>
                <xsl:value-of select="@value"/>
            </xsl:when>
            <xsl:otherwise>
                <b>
                    <xsl:value-of select="@key"/>
                </b>
            </xsl:otherwise>
        </xsl:choose>
        <br/>
    </xsl:template>

</xsl:stylesheet>