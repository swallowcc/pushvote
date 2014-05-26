package com.swallow;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@SuppressWarnings("serial")
public class VoteListServlet extends HttpServlet {

	private Utils utils = new Utils();
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			this.doProcess(req, resp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			this.doProcess(req, resp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {
		
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		
		String boardName = req.getParameter("BN");
		String getday = req.getParameter("getday") == null || req.getParameter("getday").trim().length() == 0 ? "3" : req.getParameter("getday");
		int getdayInt = Integer.parseInt(getday) > 7 ? 7 : Integer.parseInt(getday);
		int nowPage = utils.nowPageNm(boardName);
		if (nowPage == 0) {
			resp.getWriter().println(nowPage+"");
		} else {
			boolean flag = true;
			JSONArray joArray = new JSONArray();
			
			while (flag) {
				String url = "http://www.ptt.cc/bbs/" + boardName + "/index" + nowPage + ".html";
				Document doc = Jsoup.connect(url).timeout(10000).get();
				Elements rent = doc.getElementsByClass("r-ent");
				for (int i = 0; i < rent.size(); i ++) {
					String date = rent.get(i).getElementsByClass("date").text();
					String author = rent.get(i).getElementsByClass("author").text();
					String title = rent.get(i).getElementsByClass("title").text();
					String link = rent.get(i).getElementsByClass("title").select("a").attr("href");
					SimpleDateFormat sdf = new SimpleDateFormat("M/dd");
					String sDate = sdf.format(new Date());
					String eDate = sdf.format(new Date(new Date().getTime() - (86400000 * getdayInt)));
					
					if (sDate.equals(date)) {		//今日資料，隨時更動
						
					}
					
					if (utils.dateCheck(date, sDate, eDate)) {
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
			resp.getWriter().write(joArray.toJSONString());
		}
	}
}
