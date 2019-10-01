import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;
import org.dom4j.XPath;
import org.dom4j.xpath.DefaultNamespaceContext;
import org.dom4j.xpath.DefaultXPath;
import org.jaxen.dom.DOMXPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class FindElementTest {

    @Parameterized.Parameter(0)
    public String xmlFile;

    @Parameterized.Parameter(1)
    public String xpathString;

    @Parameterized.Parameters(name = "{index}: {0} {1}")
    public static List<Object[]> data() {
        List<String> xpaths = Arrays.asList(
                "/_:objects",
                "/_:objects/_:documentInfo",
                "/_:objects/_:documentInfo/_:elementInfo");

        List<String> files = Arrays.asList(
                "/test.xml",
                "/test-ns.xml",
                "/test-ns2.xml"
        );

        List<Object[]> data = new ArrayList<>();
        files.forEach(f -> {
            xpaths.forEach(xp -> {
                data.add(new Object[]{f, xp});
            });
        });

        return data;
    }

    @Test
    public void findElementInfo() throws Exception {

        String xml = readFile(xmlFile);
        final Document document = DocumentHelper.parseText(xml);
        XPath xpath = new DefaultXPath(xpathString);
        xpath.setNamespaceContext(new DefaultNamespaceContext(document.getRootElement()) {
            @Override
            public String translateNamespacePrefixToUri(String prefix) {
                if ("_".equals(prefix)) {
                    Namespace ns = document.getRootElement().getNamespaceForPrefix("");

                    if (ns != null) {
                        return ns.getURI();
                    }
                } else {
                    return super.translateNamespacePrefixToUri(prefix);
                }

                return null;            }
        });

        final List<?> nodes = xpath.selectNodes(document, xpath, false);

        assertThat(nodes, is(not(empty())));
    }

    private String readFile(String s) throws IOException {
        try (Reader r = new InputStreamReader(getClass().getResourceAsStream(s))) {
            StringBuilder out = new StringBuilder();
            char[] charBuf = new char[100];
            int nchars;
            while((nchars = r.read(charBuf)) > 0) {
                out.append(charBuf, 0, nchars);
            }
            return out.toString();
        }
    }
}
