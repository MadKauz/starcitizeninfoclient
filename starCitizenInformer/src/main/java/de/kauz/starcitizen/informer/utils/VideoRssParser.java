package de.kauz.starcitizen.informer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import de.kauz.starcitizen.informer.model.RssItem;

/**
 * Parser for RSS - youtube - data.
 * 
 * @author MadKauz
 * 
 */
public class VideoRssParser {

	private final String ns = null;

	/***
	 * Parses the given input.
	 * 
	 * @param inputStream
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<RssItem> parse(InputStream inputStream)
			throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inputStream, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Reads the parser.
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private List<RssItem> readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "rss");
		String title = null;
		String link = null;
		String imgUrl = null;
		List<RssItem> items = new ArrayList<RssItem>();
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")) {
				title = readTitle(parser);
			}
			if (name.equals("link")) {
				link = readLink(parser);
			}
			if (name.equals("description")) {
				String description = readDescription(parser);
				int partStart = description.indexOf("src");
				int partEnd = description.indexOf(".jpg");

				if (partStart != -1 || partEnd != -1) {
					imgUrl = description.substring(partStart + 5, partEnd - 2);
					imgUrl = imgUrl + "/default.jpg";
				}
			}
			if (title != null && link != null && imgUrl != null) {
				RssItem item = new RssItem(title, link, imgUrl);
				items.add(item);
				title = null;
				link = null;
				imgUrl = null;
			}
		}

		return items;
	}

	/**
	 * Reads urls.
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readLink(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "link");
		String link = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "link");
		return link;
	}

	/**
	 * Reads titles.
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readTitle(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "title");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "title");
		return title;
	}

	/**
	 * Reads descriptions.
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readDescription(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "description");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "description");
		return title;
	}

	/**
	 * Reads texts.
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
}
