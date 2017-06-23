import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class AddressToLatLng {
	
	public static TreeMap<Integer,ArrayList<String>> getAsList(String fileName) throws IOException {
		TreeMap<Integer,ArrayList<String>> asList = new TreeMap<Integer,ArrayList<String>>();
		
		BufferedReader inputStream = null;
		
        int lineNum = 0;
        try {
        	inputStream = new BufferedReader(new FileReader(fileName));
		
	        String l;
	        while ((l = inputStream.readLine()) != null) {

	        	String[] arr = l.split(",");
	        	
	        	ArrayList<String> tempList = new ArrayList<String>();
	        	for (int i = 0; i < arr.length; i++) {
	        		tempList.add(arr[i]);
	        	}
	        	
	        	asList.put(lineNum, tempList);
	        	
	            lineNum++;
	        }
	    } finally {
	        if (inputStream != null) {
	            inputStream.close();
	        }
	    }
		
		return asList;
	}
	
	public static void writeGeo(ArrayList<String> latlng) throws IOException {
		PrintWriter outputStream = null;
		

        try {
        	outputStream = new PrintWriter(new FileWriter("geo.csv",true));
        	String l = "";
        	for (int i = 0; i < latlng.size(); i++) {
        		if (i==0) {
        			l += latlng.get(i);
        		} else {
        			l += ","+latlng.get(i);
        		}
        		
        	}
    		outputStream.println(l);
       	
	    } finally {
	        if (outputStream != null) {
	        	outputStream.close();
	        }
	    }
	}
	
	public static ArrayList<String> getGeo(String apiKey, String address) {
		ArrayList<String> geo = new ArrayList<String>();
		
		try {
			
			String encoded = URLEncoder.encode(address,"UTF-8");
			
			Document doc = Jsoup.connect("https://apis.daum.net/local/geo/addr2coord?apikey="+apiKey+"&q="+encoded+"&output=json").ignoreContentType(true).get();
			
			JSONObject obj = new JSONObject(doc.body().text());
			
			String channel = obj.getString("channel");
			JSONObject objChannel = new JSONObject(channel);
			
			String item = objChannel.getString("item");
			JSONObject objItem = new JSONObject(item.substring(1, item.length()-1));
			
			String lat = objItem.getString("lat");
			String lng = objItem.getString("lng");
			
			geo.add(lat);
			geo.add(lng);
		} catch (JSONException e) {
			System.out.println("JSONException: "+e);
		} catch (IOException e) {
			System.out.println("IOException: "+e);
		}
		
		return geo;
	}
	
	public static void main (String[] args) throws IOException, JSONException {
		String fileName = "filename.csv";
		
		String apiKey = "apiKey";	//daum developers api key
		String address = "";
		
		TreeMap<Integer,ArrayList<String>> asList = getAsList(fileName);

		for(int i = 0; i < asList.keySet().size(); i++) {
			ArrayList<String> asLine = asList.get(i);
			ArrayList<String> latlng = getGeo(apiKey,asLine.get(2));
			if (latlng.size() > 1) {
				asLine.add(latlng.get(0));
				asLine.add(latlng.get(1));
			}
			System.out.println(asLine);
			writeGeo(asLine);
		}
	
	}
}
