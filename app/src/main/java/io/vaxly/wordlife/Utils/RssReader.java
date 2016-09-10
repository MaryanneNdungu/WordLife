package io.vaxly.wordlife.Utils;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.vaxly.wordlife.model.RssItem;


public class RssReader {
    private String rssUrl;

    public RssReader(String url) {
        rssUrl = url;
    }

    public List<RssItem> getItems() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        //Creates a new RssHandler which will do all the parsing.
        RssHandler handler = new RssHandler();
        //Pass SaxParser the RssHandler that was created.
        saxParser.parse(rssUrl, handler);
        return handler.getRssItemList();
    }
}
