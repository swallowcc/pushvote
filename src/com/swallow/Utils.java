package com.swallow;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Utils {
	
	public String[] addElement(String[] a, String e) {
	    a  = Arrays.copyOf(a, a.length + 1);
	    a[a.length - 1] = e;
	    return a;
	}

	public int nowPageNm(String bn) {
		String url = "http://www.ptt.cc/bbs/"+bn+"/index.html";
		Document doc;
		try {
			doc = Jsoup.connect(url).timeout(10000).get();
			String x = doc.getElementsByClass("pull-right").get(0).getElementsByClass("btn").get(1).attr("href");
			x = x.replace("/bbs/"+bn+"/index", "").replace(".html", "");
			return Integer.parseInt(x) + 1; 
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public boolean dateCheck(String postDate, String startDate, String endDate) throws ParseException {
		try {
			int x = stringToDate(startDate).compareTo(stringToDate(postDate));
			int y = stringToDate(endDate).compareTo(stringToDate(postDate));

			if ((x == 1 && y == 1) || (x == -1 && y == -1)) {
				return false;
			} else {
				return true;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static Date stringToDate(String dates) throws ParseException {
		if (dates != null && dates.trim().length() != 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("M/dd");
			try {
				return sdf.parse(dates);
			} catch (ParseException e) {
				throw new ParseException(dates, 0);
			}
		} else {
			return null;
		}
	}	
}
