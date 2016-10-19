package com.vasilev.zonskoparkiranjeskopje.parser;

import android.support.annotation.NonNull;
import android.util.Xml;

import com.vasilev.zonskoparkiranjeskopje.data.Zone;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class XmlParser {

    private static final String ZONES_TAG = "zones";
    private static final String ZONE_TAG = "zone";
    private static final String NAME_TAG = "name";
    private static final String CODE_TAG = "code";
    private static final String LATITUDE_TAG = "latitude";
    private static final String LONGITUDE_TAG = "longitude";
    private static final String COST_TAG = "cost";
    private static final String HOURS_TAG = "hours";

    @NonNull
    public static List<Zone> parse(@NonNull InputStream inputStream) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, null);
        parser.nextTag();
        return parseZones(parser);
    }

    @NonNull
    private static List<Zone> parseZones(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Zone> zoneList = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, ZONES_TAG);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String tagName = parser.getName();
            if (tagName.equals(ZONE_TAG)) zoneList.add(parseZone(parser));
            else skip(parser);
        }
        return zoneList;
    }

    @NonNull
    private static Zone parseZone(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, ZONE_TAG);
        String name = null;
        String code = null;
        double latitude = Double.MIN_VALUE;
        double longitude = Double.MIN_VALUE;
        int cost = Integer.MIN_VALUE;
        int hours = Integer.MIN_VALUE;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String tagName = parser.getName();
            switch (tagName) {
                case NAME_TAG:
                    name = parseData(parser, NAME_TAG);
                    break;
                case CODE_TAG:
                    code = parseData(parser, CODE_TAG);
                    break;
                case LATITUDE_TAG:
                    latitude = Double.parseDouble(parseData(parser, LATITUDE_TAG));
                    break;
                case LONGITUDE_TAG:
                    longitude = Double.parseDouble(parseData(parser, LONGITUDE_TAG));
                    break;
                case COST_TAG:
                    cost = Integer.parseInt(parseData(parser, COST_TAG));
                    break;
                case HOURS_TAG:
                    hours = Integer.parseInt(parseData(parser, HOURS_TAG));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        if (name == null) {
            throw new IllegalStateException("Did not parse zone name");
        }
        if (code == null) {
            throw new IllegalStateException("Did not parse zone code");
        }
        return new Zone(name, code, latitude, longitude, cost, hours);
    }

    @NonNull
    private static String parseData(@NonNull XmlPullParser parser, @NonNull String tag) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String data = "";
        if (parser.next() == XmlPullParser.TEXT) {
            data = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, tag);
        return data;
    }

    private static void skip(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) throw new IllegalStateException("Skip called when not at start of a tag");
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                default:
                    break;
            }
        }
    }
}
