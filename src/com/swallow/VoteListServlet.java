package com.swallow;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@SuppressWarnings("serial")
public class VoteListServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		System.out.println("GET");
		try {
			this.doProcess(req, resp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		System.out.println("POST");
		try {
			this.doProcess(req, resp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		String boardName = req.getParameter("BN");
		String getday = req.getParameter("getday") == null || req.getParameter("getday").trim().length() == 0 ? "3" : req.getParameter("getday");
		int nowPage = nowPageNm(boardName);
		System.out.println("boardNm : " + boardName + ", getDay : " + getday + ", nowPage : " + nowPage);
		boolean flag = true;
		JSONArray joArray = new JSONArray();
		while (flag) {
			System.out.println(nowPage + " page is loading now .......");
			String url = "http://www.ptt.cc/bbs/"+boardName+"/index" + nowPage + ".html";
			Document doc = Jsoup.connect(url).timeout(10000).get();
			Elements rent = doc.getElementsByClass("r-ent");
			for (int i = 0; i < rent.size(); i ++) {
				String date = rent.get(i).getElementsByClass("date").text();
				String author = rent.get(i).getElementsByClass("author").text();
				String title = rent.get(i).getElementsByClass("title").text();
				String link = rent.get(i).getElementsByClass("title").select("a").attr("href");
				SimpleDateFormat sdf = new SimpleDateFormat("M/dd");
				String sDate = sdf.format(new Date());
				String eDate = sdf.format(new Date(new Date().getTime() - (86400000 * Integer.parseInt(getday))));
				System.out.println("#date info : " + date + ", " + sDate + ", " + eDate + ", " + dateCheck(date, sDate, eDate));
				if (dateCheck(date, sDate, eDate)) {
					if (title.startsWith("[推投]")) {
						JSONObject joMap = new JSONObject();
						joMap.put("date", date);
						joMap.put("title", title);
						joMap.put("author", author);
						joMap.put("link", link);
						joArray.add(joMap);
					}
				} else {
					flag = false;
				}
			}
			nowPage -- ;
		}
		System.out.println(joArray.toJSONString());
		resp.getWriter().write(joArray.toJSONString());
		System.out.println("done");
	}
	
	public static int nowPageNm(String bn) {
		String url = "http://www.ptt.cc/bbs/"+bn+"/index.html";
		Document doc;
		try {
			doc = Jsoup.connect(url).timeout(10000).get();
			String x = doc.getElementsByClass("pull-right").get(0).getElementsByClass("btn").get(1).attr("href");
			x = x.replace("/bbs/"+bn+"/index", "").replace(".html", "");
			return Integer.parseInt(x) + 1; 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean dateCheck(String postDate, String startDate, String endDate) throws ParseException {
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
