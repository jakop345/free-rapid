**************************************************************
*   FreeRapid Downloader                                     *
*      by Ladislav Vitasek aka Vity                          *
*   Mail: info@wordrider.net - questions/suggestions         *
*   Website/Forum/Bugtracker: http://wordrider.net/freerapid *
*   Last change: 5th October 2008                            *
**************************************************************

=======================================
Content:
  I.   What is FreeRapid Downloader
 II.   System requirements
III.   How to run FreeRapid
 IV.   Known problems and limitations
  V.   Bug report
 VI.   FAQ 
=======================================


I.   What is FreeRapid Downloader
=======================================

FreeRapid downloader is a simple Java downloader for support downloading from Rapidshare and other file share archives.

Main features:
 - support for concurrent downloading from multiple services
 - downloading using proxy list
 - download history
 - clipboard monitoring 
 - programming interface (API) for adding other services like plugins
 - works on Linux and MacOS


Misc.:
 - Drag&Drop URLs

Currently supported services are:
 -  Rapidshare.com (for Premium account see Homepage for more details)
 -  FileFactory.com
 -  Uploaded.to
 -  MegaUpload.com (=megarotic.com = sexuploader.com)
 -  DepositFiles.com
 -  NetLoad.in
 -  ..others are coming


II. System requirements
============================

Recommended configuration:
    * Windows 2000/XP/Linux(core 2.4)* or higher operating system
    * Pentium 800MHz processor
    * min 1024x768 screen resolution
    * 40 MB of free RAM
    * 10 MB free disk space
    * Java 2 Platform - version at least 1.6 (Java SE 6 Runtime) installed

Application needs at least Java 6.0 to start (http://java.sun.com/javase/downloads/index.jsp , JRE 6).


III.  How to run FreeRapid Downloader
=======================================

Installation
------------
Unzip files to any of your directory, but beware special characters (like '+' or '!') on the path.
If you make an upgrade to higher version, you can delete previous folder. All user
settings are preserved. All user settings are saved in home directories:
MS Windows: c:\Documents and Settings\YOUR_USER_NAME\application data\VitySoft\FRD
            and in registry HKEY_CURRENT_USER\Software\JavaSoft\Prefs\vitysoft\frd
Linux: ~/.FRD

DO NOT copy new version over older one.


Launching
-----------
Windows
 Simply launch frd.exe

Linux
 Run command ./frd.sh

All platforms
 Run command java -jar frd.jar


additional parameters for launching are:

java -jar frd.jar [-h -v -d -D<property>=<value>]

options
  -h (--help,-?)      print this message
  -v (--version)      print the version information and exit
  -d (--debug)        print debugging information
  -Dproperty=value    Passes properties and values to the application (mostly for debug or testing purposes)
   

IV.   Known bugs and Limitations
=======================================
- Application will not start if it's placed on the path with special characters like '+' or '%'
  - X please move application to another location without such characters

- Selection from "top to bottom" in the main table during dragging while downloading partly disappears
    X select table rows by ctrl+mouse click or select items from bottom to top
- Substance look and feel throws org.jvnet.substance.api.UiThreadingViolationException:
                                                              Component creation must be done on Event Dispatch Thread
    X ignore this exception in the app.log
- DirectoryChooser throws java.lang.InternalError or freezes on Win Vista (64bit)
    X ignore this exception in the app.log
- java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component
    X ignore this exception in the app.log    
- Linux users reported not showing icon in tray on Linux
    X the only one known solution for this problem could be an upgrade JRE to version 1.6.0_10-rc or higher


V.    Bug report
=======================================
If you see a bug, please do not assume that i know about it. Let me know as soon as possible so that i can fix it before
the next release. Since my resources are limited, i can not backport bug fixes to earlier releases.
To report a bug, you can use the issue tracker (preferred), project forums or my personal e-mail.

Please report your JRE version and attach file app.log (if neccessary).
http://bugtracker.wordrider.net/
bugs@wordrider.net


VI.   FAQ
=======================================

Q: Why did you create another "RapidShare Downloader"?
A: 1) Because I don't want to be dependant on the russian software, which is probably full of malware and spyware.
   2) Because I can simply fix automatic downloading myself.
   3) Because they have unintuitive user interface and missing important features.
   4) Because I can.

