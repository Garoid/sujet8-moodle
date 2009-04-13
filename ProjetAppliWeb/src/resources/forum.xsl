<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"> 

  <xsl:variable name="resourceURL1">
    <xsl:text>moodle</xsl:text>
  </xsl:variable>

  <xsl:template match="forum">
<xsl:variable name="resourceURLForum">
    <xsl:text>moodle/forum/</xsl:text>
    <xsl:value-of select="@*"/>
  </xsl:variable>
<rdf:Description
       rdf:about="{$resourceURLForum}" >
<xsl:apply-templates/>
</rdf:Description>
  </xsl:template>

<xsl:template match="topic">
<xsl:variable name="resourceURLTopic">
    <xsl:text>moodle/forum/topic/</xsl:text>
    <xsl:value-of select="@*"/>
  </xsl:variable>
<rdf:Description
       rdf:about="{$resourceURLTopic}" >
<xsl:apply-templates/>
</rdf:Description>
</xsl:template>

 <xsl:template match="post">
<xsl:variable name="resourceURLPost">
    <xsl:text>moodle/forum/topic/post/</xsl:text>
    <xsl:value-of select="@*"/>
  </xsl:variable>

<rdf:Description
       rdf:about="{$resourceURLPost}" >
<Id><xsl:value-of select="@*"/></Id>
<Auteur><xsl:value-of select="auteur"/></Auteur>
<Date><xsl:value-of select="date"/></Date>
<Message><xsl:value-of select="message"/></Message>
<Parent><xsl:value-of select="parent"/></Parent>
</rdf:Description>
 </xsl:template>


  <xsl:template match="/">
    <rdf:RDF>
        <xsl:apply-templates select="moodle/forum" />
    </rdf:RDF>
  </xsl:template>


</xsl:stylesheet>

