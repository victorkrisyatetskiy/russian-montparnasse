package com.russianmontparnasse.rss;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class RssFeedReader {

    public List<RssItem> read(String rssUrl) {
        List<RssItem> rssItems = new ArrayList<>();
        try {
            URL url = new URL(rssUrl);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());
            document.getDocumentElement().normalize();

            NodeList itemNodes = document.getElementsByTagName("item");
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Node itemNode = itemNodes.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    String title = getElementValue(itemElement, "title");
                    String link = getElementValue(itemElement, "link");
                    String pubDate = getElementValue(itemElement, "pubDate");
                    RssItem rssItem = new RssItem(title, link, pubDate);
                    rssItems.add(rssItem);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read RSS feed: " + rssUrl, e);
        }
        return rssItems;
    }

    private String getElementValue(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            if (node != null) {
                return node.getTextContent();
            }
        }
        return null;
    }
}


