import jlibs.xml.sax.XMLDocument;
import jlibs.xml.xsl.TransformerUtil;
import org.xml.sax.SAXException;
import utilities.FileUtils;
import utilities.Utils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

/**
 * @author Vity
 */
public class TranslateProcessor {
    private File inputDir;
    private String sourceLanguage;
    private String targetLanguage;
    private final static Set<String> ignoreMissingFiles = new HashSet<String>();
    private boolean commentOutStringsForRemoving;

    {
        ignoreMissingFiles.add("UIStringsManager_(en|de|es|fr|it|ja|ko|sv|zh).properties");
        ignoreMissingFiles.add("DatePicker_(cs|da|de|en|en_GB|en_US|es|fr|it|nl|pl_PL|pl|pt|pt_BR|sv).properties");
        ignoreMissingFiles.add("ErrorPane_(cs|da|de|en|en_GB|en_US|es|fr|it|nl|pl_PL|pl|pt|pt_BR|sv).properties");
        ignoreMissingFiles.add("LoginPane_(cs|da|de|en|en_GB|en_US|es|fr|it|nl|pl_PL|pl|pt|pt_BR|sv).properties");
        ignoreMissingFiles.add("swingx_(cs|da|de|en|en_GB|en_US|es|fr|it|nl|pl_PL|pl|pt|pt_BR|sv).properties");
        ignoreMissingFiles.add("TipOfTheDay_(cs|da|de|en|en_GB|en_US|es|fr|it|nl|pl_PL|pl|pt|pt_BR|sv).properties");
    }


    public TranslateProcessor(String inputDir, String sourceLanguage, String targetLanguage, boolean commentOutStringsForRemoving) {
        this.commentOutStringsForRemoving = commentOutStringsForRemoving;
        this.inputDir = new File(inputDir);
        if (sourceLanguage == null || sourceLanguage.isEmpty()) {
            this.sourceLanguage = "";
        } else this.sourceLanguage = "_" + sourceLanguage;
        if (targetLanguage == null || targetLanguage.isEmpty()) {
            this.targetLanguage = "";
        } else this.targetLanguage = "_" + targetLanguage;
    }

    public File run() throws SAXException, TransformerException, IOException {
        final List<File> sourceFiles = new ArrayList<File>();
        getFilesForComparison(inputDir, sourceFiles);
        final List<PropertiesFile> sourcePropertiesFiles = new ArrayList<PropertiesFile>();
        final List<PropertiesFile> targetPropertiesFiles = new ArrayList<PropertiesFile>();


        for (File sourceFile : sourceFiles) {

            String name = Utils.getPureFilename(sourceFile);
            final int i = name.indexOf('_');
            if (i > 0) {
                name = name.substring(0, i);
            }
            final PropertiesFile sourcePropertiesFile = new PropertiesFile(sourceFile, sourceLanguage);
            sourcePropertiesFiles.add(sourcePropertiesFile);
            final File targetFile = new File(sourceFile.getParentFile(), name + targetLanguage + ".properties");

            if (!targetFile.exists()) {
                boolean found = false;
                for (String s : ignoreMissingFiles) {
                    if (targetFile.getName().matches(s)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }
            }
            final PropertiesFile propertiesFile = new PropertiesFile(targetFile, targetLanguage);
            if (!propertiesFile.isFileMissing()) {
                propertiesFile.setMissingProperties(sourcePropertiesFile.getMissingPropertiesIn(propertiesFile));

                if (commentOutStringsForRemoving) {
                    final Set<String> propertiesForRemovingIn = sourcePropertiesFile.getPropertiesForRemovingIn(propertiesFile);
                    if (!propertiesForRemovingIn.isEmpty()) {
                        String s = Utils.loadFile(targetFile, "UTF-8");
                        for (String replaceString : propertiesForRemovingIn) {
                            s = s.replace(replaceString, "#" + replaceString);
                        }
                        final FileWriter fileWriter = new FileWriter(targetFile, false);
                        fileWriter.write(s);
                        fileWriter.close();
                    }
                } else
                    propertiesFile.setForRemovingProperties(sourcePropertiesFile.getPropertiesForRemovingIn(propertiesFile));
            }
            targetPropertiesFiles.add(propertiesFile);
        }
        final String s = generateXML(targetPropertiesFiles);
        final String appPath = Utils.getAppPath();
        final File dir = new File(appPath, "output");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        final File outputFile = new File(dir, "output" + targetLanguage + ".html");
        final StreamResult streamResult = new StreamResult(new FileWriter(outputFile));
        final StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(s.getBytes("UTF-8")));
        final StreamSource xsl = new StreamSource(MainApp.class.getResourceAsStream("output.xsl"));
        final Transformer transformer = TransformerUtil.newTransformer(xsl, false, 4, "UTF-8");
        transformer.transform(xmlSource, streamResult);
        return outputFile;
    }

    private String generateXML(List<PropertiesFile> targetPropertiesFiles) throws TransformerConfigurationException, SAXException {
        final StringWriter xmlWriter = new StringWriter();
        XMLDocument xml = new XMLDocument(new StreamResult(xmlWriter), false, 4, "UTF-8");
        xml.startDocument();
        xml.startElement("translation");
        for (PropertiesFile propertiesFile : targetPropertiesFiles) {
            final Map<String, String> missingProperties = propertiesFile.getMissingProperties();
            final Set<String> removingProperties = propertiesFile.getForRemovingProperties();

            if (!propertiesFile.isFileMissing() && missingProperties.isEmpty() && removingProperties.isEmpty()) {
                continue;
            }

            xml.startElement("propertiesFile");
            xml.addAttribute("name", FileUtils.getRelativeDirectory(inputDir, propertiesFile.getFile()).toString());
            xml.addAttribute("missing", String.valueOf(propertiesFile.isFileMissing()));

            if (!propertiesFile.isFileMissing()) {
                if (!missingProperties.isEmpty()) {
                    xml.startElement("missing");
                    for (Map.Entry<String, String> entry : missingProperties.entrySet()) {
                        xml.startElement("property");
                        xml.addAttribute("key", entry.getKey());
                        xml.addAttribute("value", entry.getValue());
                        xml.endElement("property");
                    }
                    xml.endElement("missing");
                }

                if (!removingProperties.isEmpty()) {
                    xml.startElement("removing");
                    for (String removing : removingProperties) {
                        xml.startElement("property");
                        xml.addAttribute("key", removing);
                        xml.endElement("property");
                    }
                    xml.endElement("removing");
                }
            }
            xml.endElement("propertiesFile");
        }
        xml.endElement("translation");
        xml.endDocument();
        return xmlWriter.toString();
    }

    private void getFilesForComparison(File inputDir, List<File> files) {
        final String[] list = inputDir.list();
        if (list != null) {
            for (String s : list) {
                getFilesForComparison(new File(inputDir, s), files);
            }
        }
        final File[] listFiles = inputDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (dir.getName().endsWith("resources")) {
                    if (sourceLanguage.isEmpty()) {
                        if (!name.contains("_")) {
                            return name.endsWith(sourceLanguage + ".properties");
                        }
                    } else {
                        return name.endsWith(sourceLanguage + ".properties");
                    }
                }
                return false;
            }
        });
        if (listFiles != null) {
            Collections.addAll(files, listFiles);
        }
    }
}

