*********************************************************
*   WordRider Text Editor for Ti-89/92/V200             *
*      by Ladislav Vitasek                              *
*   Web: http://wordrider.net - latest version          *
*   Mail: info@wordrider.net - questions/suggestions    *
*   Forum: http://wordrider.net/forum                   *
*   Last change: 18th February 2007                     *
*********************************************************

=================================
Content:
  I.   What is WordRider 
 II.   System requirements 
III.   How to run Wordrider 
 IV.   Known bugs and Limitations
  V.   FAQ
=================================


I. What is WordRider
============================
Welcome to the website dedicated to open source text editor 'WordRider' which helps
with creating TxtRider's text format(HibView and uView compatible) in calculator TI-89/92+/V200.

WordRider is similar to applications such as RidEdit, MadCoder's Text Editor or Okin's Word.
WordRider brings full support of TxtRider view-tags and more user convenience with working TI-89/92 text format.
WordRider editor is "almost" WYSIWYG.

Features
    * you can easily change font sizes, alignments, margins, 
      word wrap, math expressions and insert line separators or breakpoints
    * really easy work with breakpoints and inserting special characters
    * 89i, 92i, 9xi pictures support
    * image importer plugin - easy converting JPG, GIF,... into calc image
    * math expressions are distinguished by color
    * multilanguage environment
    * free software
    * open source code under GNU GPL license
    * support for Java Look&Feels and themes
    * always updated - checking for new version
    * OS platform independent
    * and much more...

At http://wordrider.net (documentation section) you can find a list of shortcuts in Adobe PDF format and additional information.

Visit the WordRider forum at http://wordrider.net/forum

All suggestions are welcome!

II. System requirements
============================
Recommended configuration:
    * Windows 2000/XP/Linux(core 2.4)* or higher operating system
    * Pentium 500MHz processor
    * min 1024x768 screen resolution
    * 35 MB of free RAM
    * 5 MB free disk space
    * Java 2 Platform - version at least 1.5 (Java SE 5 Runtime) installed
Other operating systems were not tested although I expect it will work fine elsewhere as well.

III. How to run Wordrider
============================
Please make sure that you have Java 2 platform (at least version 1.5 - Java SE 5 Runtime) installed.
You can also obtain it at http://java.sun.com. More info can be found also in the FAQ section.

    * Windows users
      Unpack zipped file into chosen directory. Run wordrider.exe in main folder. 
      Java runtime binaries will be detected automatically and WordRider will start.
    * Linux users
      Unpack gzipped file into chosen directory. 
      Java binaries should be registered in /etc/bashrc. Start Wordrider by using launch.sh.
    * For all operating systems
      You can start WordRider manually by using command line and call java binaries directly.
      java -jar wordrider.jar

Viewing text files in the calculator
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
For viewing generated text files you will have to install PreOS (kernel OS) and Hibview
and/or TxtRider (text viewers - one of them) into your calculator.
You can intall them easily. For beginners - see tutorials at the documentation section at http://wordrider.net

IV. Limitations
==============================
LIMITATIONS
Problem: Some characters are not displayed.
Details: See our FAQ section "Why WordRider doesn't support ..."
Solution: Use an alternate character. 

Problem: Count of the characters(in the smallest and the biggest font) on the line does not correspond with a count of the characters in the calculator
Details: WordRider uses the TI92PlusPC true type font which covers most of calc characters(not all, but most of them). Its disadvantage is that it has not similar sizes with a font used in the calculator. Therefore the default width for emulation is set to 'normal' size font(25 characters, I expect that users will use it most frequently). Other font sizes are only near their count. I would be able to decrease smallest font size(to reach about 35 characters per line), but the text would have stopped to be readable(i had such responses from users with a high resolution screen).
It's really really hard to create similar true type font which is used in the calc. I don't know anybody who would be able to create it - maybe you can? :-)
Solution: Unknown.

V. FAQ
============================
Please visit http://wordrider.net to see FAQ section.