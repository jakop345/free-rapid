<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:strip-space elements="*"/>
	<xsl:output encoding="utf-8" indent="yes" method="text"/>
	<xsl:template match="/">    
		<xsl:apply-templates select="gpx/trk/trkseg/trkpt" />
	</xsl:template>
	<xsl:template match="trkpt">	
		<xsl:value-of select="concat(@lat,'	',@lon, '&#13;')"/>
	</xsl:template>
</xsl:stylesheet>