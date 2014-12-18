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
            element = {
                attr: function (a, v) {
                    this[a] = v;
                }
            };
            this.elements[id] = element;
        }
        return element;
    },

    ready: function (f) {
        f();
    }
};

$ = function (arg) {
    if (typeof arg === "string" && arg.substring(0, 1) === "#") {
        return document.getElementById(arg.substring(1));
    }
    return arg;
};

EnvJs = true;