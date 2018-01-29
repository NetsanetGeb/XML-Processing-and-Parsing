<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ap="http://www.ApplicantProfile.se" version="1.0">

	<xsl:template match="/">
		<xsl:element name="ap:ApplicantProfile">
			<xsl:element name="ap:PersonalInfo">
				<xsl:copy-of select="document('result/DomParsedShortCVResult.xml')/ap:GeneralInfo/*" />
			</xsl:element>
			<xsl:element name="ap:EducationInfo">
				<xsl:copy-of select="document('result/SAXParsedTranscriptResult.xml')/ap:StudiesInfo/*" />
			</xsl:element>
			<xsl:element name="ap:WorkExprienceInfo">
				<xsl:copy-of
					select="document('result/JAXBEmploymentRecord.xml')/ap:EmploymentInfo/*" />
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>