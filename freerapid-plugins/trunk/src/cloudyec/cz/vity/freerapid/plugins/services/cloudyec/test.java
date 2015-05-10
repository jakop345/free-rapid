package cz.vity.freerapid.plugins.services.cloudyec;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.regex.Matcher;

/**
 * @author tong2shot
 */
class test {
    public static void main(String[] args) throws Exception {
        String embedContent =
                "<html>\n" +
                        "<head>\n" +
                        "<style>\n" +
                        "body {\n" +
                        "margin:0; \n" +
                        "padding:0;\n" +
                        "background-color:#000;\n" +
                        "}\n" +
                        "</style>\n" +
                        "<script type=\"text/javascript\" src=\"/js/mobileapp.js\"></script>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "\n" +
                        "<script type=\"text/javascript\" src=\"/player/swfobject.js\"></script>\n" +
                        "<div name=\"mediaspace\" id=\"mediaspace\">\n" +
                        "        \t<p>\n" +
                        "\t        \tTo view this page ensure that Adobe Flash Player version \n" +
                        "\t\t\t\t10.0.0 or greater is installed. \n" +
                        "\t\t\t</p>\n" +
                        "\t\t\t<script type=\"text/javascript\"> \n" +
                        "\t\t\t\tvar pageHost = ((document.location.protocol == \"https:\") ? \"https://\" :\t\"http://\"); \n" +
                        "\t\t\t\tdocument.write(\"<a href='http://www.adobe.com/go/getflashplayer'><img src='\" \n" +
                        "\t\t\t\t\t\t\t\t+ pageHost + \"www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>\" ); \n" +
                        "\t\t\t</script> \n" +
                        "\t</div>\n" +
                        "\t\t<script type=\"text/javascript\" src=\"/play/js/swfobject.js\"></script>\n" +
                        "\n" +
                        "        <script type=\"text/javascript\">\n" +
                        "\t\t   var swfVersionStr = \"10.0.0\";\n" +
                        "\t\tfunction logout(){\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\tfunction disableAds(x,y,z) {\n" +
                        "if(z>0){\n" +
                        "document.getElementById('adv1').innerHTML ='';\n" +
                        "vp=1;\n" +
                        "}\t\t}\t\n" +
                        "\t\t\t\n" +
                        "\n" +
                        "var winW = 400, winH = 300;\n" +
                        "if (document.body && document.body.offsetWidth) {\n" +
                        " winW = document.body.offsetWidth;\n" +
                        " winH = document.body.offsetHeight;\n" +
                        "}\n" +
                        "if (document.compatMode=='CSS1Compat' &&\n" +
                        "    document.documentElement &&\n" +
                        "    document.documentElement.offsetWidth ) {\n" +
                        " winW = document.documentElement.offsetWidth;\n" +
                        " winH = document.documentElement.offsetHeight;\n" +
                        "}\n" +
                        "if (window.innerWidth && window.innerHeight) {\n" +
                        " winW = window.innerWidth;\n" +
                        " winH = window.innerHeight;\n" +
                        "var xx=111;\n" +
                        "}\n" +
                        "\n" +
                        "      \n" +
                        "              \n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "var vars = {\n" +
                        "\t\t\t\t\t\t  cid:\"1\", \n" +
                        "\t\t\t\t\t\t  numOfErrors: \"0\", \n" +
                        "\t\t\t\t\t\t  cid2: \"\", \n" +
                        "\t\t\t\t\t\t  cid3:\"cloudy.ec\", \n" +
                        "\t\t\t\t\t\t  user:\"\", \n" +
                        "\t\t\t\t\t\t  pass:\"\", \n" +
                        "\t\t\t\t\t\t  domain: \"http://www.cloudy.ec\",\n" +
                        "\t\t\t\t\t\t  key: \"36.72.141.49-b9679d4bbcdbc644892e7da288b81df2-\",\n" +
                        "\t\t\t\t\t\t  file:\"a8d6293413786\", \n" +
                        "\t\t\t\t\t\t  crk:\"1\",\t\n" +
                        "\t\t\t\t\t\t  skin : \"/play/skins/gblue.xml\",\n" +
                        "\t\t\n" +
                        "\t\t};\n" +
                        "\t\tvar params = {scale:'noScale', salign:'lt', menu:'false', startparam:'start',wmode:'transparent'};\n" +
                        "\t\tvar attributes = { id:'player', name:'player', bgcolor:'#000000', allowFullScreen:'true'}; \n" +
                        "\n" +
                        "\t\tswfobject.embedSWF(\"/play/Main.swf\", \"mediaspace\", winW, winH, \"9.0.0\", \"/play/js/expressInstall.swf\", vars, params, attributes );\t\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\t\t\t\n" +
                        "\n" +
                        "\n" +
                        "</script>\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "<script type=\"text/javascript\">\n" +
                        "function adclose(){\n" +
                        "document.getElementById('aad').innerHTML=\"\";\n" +
                        "}\n" +
                        "</script>\n" +
                        "<style>\n" +
                        "\t#aad{\n" +
                        "\t\twidth:300px;\n" +
                        "    \tmargin-left:-150px;\n" +
                        "    \tleft:50%;\n" +
                        "    \tmargin-top:-50px;\n" +
                        "    \ttop:20%;\n" +
                        "\t\tposition: absolute;\n" +
                        "\t\tz-index: 99999;\n" +
                        "\t\ttext-align: center;\n" +
                        "\t}\n" +
                        "\t#aad span{\n" +
                        "\t\tmargin-top:5px;\n" +
                        "\t\tdisplay: block;\n" +
                        "\t}\n" +
                        "\t.btn-newad{\n" +
                        "\t\t\n" +
                        "\t\tfont-weight: bold;\n" +
                        "\t\tdisplay: block;\n" +
                        "\t\ttext-decoration: none;\n" +
                        "\t\tcursor: pointer;\n" +
                        "\t\tmargin-top: 10px;\n" +
                        "\t\tpadding: 4px 12px 5px;\n" +
                        "\t\ttext-shadow: 0 1px 1px rgba(255, 255, 255, 0.75);\n" +
                        "\t\tfont-size: 14px;\n" +
                        "\t\tline-height: normal;\n" +
                        "\t\t-webkit-border-radius: 4px;\n" +
                        "\t\t-moz-border-radius: 4px;\n" +
                        "\t\tborder-radius: 4px;\n" +
                        "\t\t-webkit-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);\n" +
                        "\t\t-moz-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);\n" +
                        "\t\tbox-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);\n" +
                        "\t\t-webkit-transition: 0.1s linear all;\n" +
                        "\t\t-moz-transition: 0.1s linear all;\n" +
                        "\t\ttransition: 0.1s linear all;\n" +
                        "\t\tbackground-color: hsl(0, 0%, 79%);\n" +
                        "\t\tbackground-repeat: repeat-x;\n" +
                        "\t\tbackground-image: -khtml-gradient(linear, left top, left bottom, from(hsl(0, 0%, 121%)), to(hsl(0, 0%, 79%)));\n" +
                        "\t\tbackground-image: -moz-linear-gradient(top, hsl(0, 0%, 121%), hsl(0, 0%, 79%));\n" +
                        "\t\tbackground-image: -ms-linear-gradient(top, hsl(0, 0%, 121%), hsl(0, 0%, 79%));\n" +
                        "\t\tbackground-image: -webkit-gradient(linear, left top, left bottom, color-stop(0%, hsl(0, 0%, 121%)), color-stop(100%, hsl(0, 0%, 79%)));\n" +
                        "\t\tbackground-image: -webkit-linear-gradient(top, hsl(0, 0%, 121%), hsl(0, 0%, 79%));\n" +
                        "\t\tbackground-image: -o-linear-gradient(top, hsl(0, 0%, 121%), hsl(0, 0%, 79%));\n" +
                        "\t\tbackground-image: linear-gradient(hsl(0, 0%, 121%), hsl(0, 0%, 79%));\n" +
                        "\t\tborder-color: hsl(0, 0%, 79%) hsl(0, 0%, 79%) hsl(0, 0%, 68.5%);\n" +
                        "\t\tcolor: #333;\n" +
                        "\t\ttext-shadow: 0 1px 1px rgba(255, 255, 255, 0.69);\n" +
                        "\t\t-webkit-font-smoothing: antialiased;\n" +
                        "\t}\n" +
                        "\t.btn-newad:hover {\n" +
                        "\t\tbackground-position: 0 -15px;\n" +
                        "\t\tcolor: #333;\n" +
                        "\t\ttext-decoration: none;\n" +
                        "\t}\n" +
                        "\t.btn-newad.disabled {\n" +
                        "\t\tbackground-position: 0 -25px;\n" +
                        "\t\tcolor: #333;\n" +
                        "\t}\n" +
                        "</style>\n" +
                        "\n" +
                        "<div id=\"aad\" class=\"ad\">\n" +
                        "\n" +
                        "<iframe src='/ads/ads.php'; height='250' width='300' frameborder='0' border='0' marginwidth='0' marginheight='0' scrolling='no'></iframe>\n" +
                        "<a href=\"javascript:pop_click();adclose();\" class=\"btn-newad\" id=\"first_btn\">Close ad</a>\n" +
                        "\n" +
                        "</div>\n" +
                        "\n" +
                        "<script src='/popups/index.php?cb=1431241176' type='text/javascript'></script>\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "<script>\n" +
                        "  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n" +
                        "  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n" +
                        "  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n" +
                        "  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');\n" +
                        "\n" +
                        "  ga('create', 'UA-43817902-1', 'auto');\n" +
                        "  ga('send', 'pageview');\n" +
                        "\n" +
                        "</script>\n" +
                        "\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>\n" +
                        "\n";


        String fileKey;
        String content = embedContent.contains("file\\s*?:\\s*?\"") ? embedContent : "Blah";
        Matcher matcher = PlugUtils.matcher("key\\s*?:\\s*?([\"']?.+?[\"'])?,", content);
        if (matcher.find()) {
            fileKey = matcher.group(1);
            if (fileKey.contains("\"") || fileKey.contains("'")) { //filekey is string
                fileKey = fileKey.replace("\"", "").replace("'", "");
            } else { //filekey param is stored in variable
                matcher = PlugUtils.matcher(String.format("var %s\\s*=\\s*[\"'](.+?)[\"']\\s*;", fileKey), content);
                if (!matcher.find()) {
                    throw new PluginImplementationException("Error parsing file key");
                }
                fileKey = matcher.group(1);
            }
        } else {
            throw new PluginImplementationException("File key not found");
        }
        System.out.println(fileKey);
    }
}
