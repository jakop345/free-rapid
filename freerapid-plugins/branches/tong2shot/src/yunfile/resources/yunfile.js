document = {
    dom: Packages.javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument(),
    elements: new java.util.HashMap(),

    getElementById: function (id) {
        var element = this.elements.get(id);
        if (element == null) {
            element = this.dom.createElement(id);
            this.elements.put(id,element);
        }
        return element;
    }
};

Java = undefined;
Packages = undefined;
com = undefined;
edu = undefined;
java = undefined;
javafx = undefined;
javax = undefined;
org = undefined;
JavaImporter = undefined;
importClass = undefined;
importPackage = undefined;
JavaAdapter = undefined;
exit = undefined;
quit = undefined;
