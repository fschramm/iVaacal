package edu.hm.cs.ivaacal.dataSource;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.mortbay.util.URIUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: Christoph Waldleitner
 */
public class GoogleMaps {

    /**
     * The logger for this class.
     */
    private final static Logger LOGGER = Logger.getLogger(GoogleMaps.class);

    /**
     * Location of the company
     */
    private final String home;

    /**
     * Constructor
     *
     * @param home      location of the company
     */
    public GoogleMaps(final String home){
         this.home = home;
    }

    /**
     * Returns the duration from the company to the given location by car.
     *
     * @param address       location to drive from the company
     * @return              duration to the location in millis
     */
    public int getDuration(String address){
        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + home
                + "&destination=" + address
                + "&sensor=false&units=metric&mode=driving";
        url = url.replaceAll(" ","%20");
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);

            NodeList nl1 = doc.getElementsByTagName("duration");
            org.w3c.dom.Node node1 = nl1.item(nl1.getLength()-1);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = null;
            for(int i=0;i<nl2.getLength(); i++){
                node2 = nl2.item(i);
                if(node2.getNodeName()=="value"){
                    return Integer.parseInt(node2.getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
