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

document = {
    elements: {},

    getElementById: function (id) {
        var element = this.elements[id];
        if (element === undefined) {
            element = {};
            this.elements[id] = element;
        }
        return element;
    }
};