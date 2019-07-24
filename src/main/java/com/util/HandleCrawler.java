package com.util;

import com.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class HandleCrawler {

    public static void setCookie() {
        try {
            CloseableHttpResponse response = ApacheHttpUtil.sendGet(Constant.proxyUrl);
            if (response.getStatusLine().getStatusCode() == 521) {
                String jsluidCookie = getJsluidCookie(response.getAllHeaders());
                log.info("jsluidCookie is :" + jsluidCookie);

                HttpEntity entity = response.getEntity();
                String html = EntityUtils.toString(entity, "utf-8");
//                String runString = getRunString(html);
//                String function = html.substring(html.indexOf("function")).replace("</script> </body></html>", runString + ";").replace("eval(\"qo=eval;qo(po);\")", "return po");
                String functionFirst = handleFirst(html);
                functionFirst = functionFirst.substring(0, functionFirst.indexOf("qaq();")) + "qaq();";
                log.info("functionFirst is :" + functionFirst);

                ScriptEngineManager m = new ScriptEngineManager(); //获取JavaScript执行引擎
                ScriptEngine engine = m.getEngineByName("JavaScript"); //执行JavaScript代码
                String origin = (String) engine.eval(functionFirst);

                String secondName = origin.substring(4, origin.indexOf("="));
                String functionSecond = handleDocument(handleSecond(origin, secondName));
                log.info("functionSecond is :" + functionSecond);
                if (!functionSecond.contains("window")) {
                    String real = (String) engine.eval(functionSecond);
                    String jslclearance = getJslclearance(real);
                    log.info("jslclearance is :" + jslclearance);

                    Constant.COOKIE = "__jsluid_h=" + jsluidCookie + "; __jsl_clearance=" + jslclearance;
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            log.error("获取Cookie失败", e);
        }
    }

    private static String getJsluidCookie(Header[] headers) {
        String jsluidCookie = null;
        for (Header header : headers) {
            if (header.getName().equals("Set-Cookie")) {
                jsluidCookie = header.getValue();
                break;
            }
        }
        Pattern pattern = Pattern.compile("(?<=__jsluid_h=).+?(?=; max-age=)");
        Matcher matcher = pattern.matcher(jsluidCookie);
        while (matcher.find()) {
            jsluidCookie = matcher.group(0);
        }
        return jsluidCookie;
    }

    private static String getRunString(String html) {
        Pattern pattern = Pattern.compile("(?<=window.onload=setTimeout\\(\").+?(?=\", 200\\))");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private static String handleFirst(String html) {
        String handleFirst = html.replace("eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}));break}catch(_){}", "function aa(){return y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)});}break}catch(_){}return aa();}qaq();");
        String function = handleFirst.replace("<script>", "").replace("</script>", "");
        return "function qaq(){" + function;
    }

    private static String handleSecond(String html, String name) {
        return html.replace("setTimeout('location.href=location.pathname+location.search.replace(/[\\?|&]captcha-challenge/,\\'\\')',1500);document.cookie=", "return ")
                .replace(";if((function(){try{return !!window.addEventListener;}catch(e){return false;}})()){document.addEventListener('DOMContentLoaded'," + name + ",false)}else{document.attachEvent('onreadystatechange'," + name + ")}", ";" + name + "();")
                .replace("(window.headless+[]+[[]][0]).charAt(8)", "'d'")
                .replace("window['__p'+'hantom'+'as']", "'f'")
                .replace("(window['callP'+'hantom']+[]+[[]][0]).charAt(-~[-~[-~~~!{}+((+!-{})<<(+!-{}))+((+!-{})<<(+!-{}))]])", "'e'")
                .replace("(window['callP'+'hantom']+[]).charAt(~~[])", "'u'")
                .replace(name + ".firstChild.href", "\"https:///\"")
                .replace(name + ".match(/https?:\\/\\//)[0]", "\"https://\"");
    }

    private static String handleDocument(String html) {
        if (html.contains("document.createElement") && html.contains("firstChild.href")) {
            return html.replace(html.substring(html.indexOf("document.createElement"), html.indexOf("firstChild.href") + 15), "\"https:///\"");
        } else if (html.contains("document.createElement")) {
            return html.replace(html.substring(html.indexOf("document.createElement"), html.indexOf("\"https:///\"")), "");
        } else {
            return html;
        }
    }

    private static String getJslclearance(String real) {
        String jslclearance = null;
        Pattern pattern = Pattern.compile("(?<=__jsl_clearance=).+?(?=;Expires=)");
        Matcher matcher = pattern.matcher(real);
        while (matcher.find()) {
            jslclearance = matcher.group(0);
        }
        return jslclearance;
    }
}
