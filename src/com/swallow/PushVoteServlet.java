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
public class PushVoteServlet extends HttpServlet {

	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		System.out.println("application version 1.3.7");
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		String keyword = "[推投]";
		String sDate = req.getParameter("sDate");
		String eDate = req.getParameter("eDate");
		String options = req.getParameter("hiddenValue") == null || req.getParameter("hiddenValue").trim().length() == 0 ? "none" : req.getParameter("hiddenValue");
		String reVote = req.getParameter("revote");
		String count = req.getParameter("count") == null || req.getParameter("count").trim().length() == 0 || req.getParameter("count").equals("0") ? "1" : req.getParameter("count");
		String url = req.getParameter("url");
		String input = req.getParameter("input");
		if (url == null || url.trim().length() == 0 || !url.contains("ptt.cc")) {
			resp.getWriter().println("4");
		} else {
			try {
				Document doc = Jsoup.connect(url).timeout(10000).get();
				Document doc2 = Jsoup.connect(url).timeout(10000).get();
				Map<String, Integer> result = new HashMap<String, Integer>();			//裝載投票結果, 選項/總票數
				Map<String, List<String>> user = new HashMap<String, List<String>>();	//裝載使用者資訊, IDs/所投選項
				Map<String, List<String>> info = new HashMap<String, List<String>>();	//裝載選票資訊, 所投選項/IDs
				List<String> userId = new ArrayList<String>();
				
				if (doc.select("title").text().contains(keyword)) {
					String[] option = null;
					if (input != null) {
						if (!options.equals("none") || input.equals("oneline")) {
							option = options.split(",");
						} else if (input.equals("web")) {
							doc2.select("span").remove();
							doc2.getElementsByClass("push").remove();
							doc2.getElementsByClass("richcontent").remove();
							doc2.getElementsByClass("article-metaline").remove();
							doc2.getElementsByClass("article-metaline-right").remove();
							String str = doc2.getElementById("main-content").text();
							if (str.indexOf("<start>") != -1 && str.indexOf("</start>") != -1) {
								str = str.substring(str.indexOf("<start>") + 7, str.indexOf("</start>"));
								option = str.split(",");
							} else {
								resp.getWriter().println("1");
							}
						}
						for (int j = 0; j < option.length; j ++) {
							result.put(option[j], 0);		//將選項放入MAP, 初始化都是零票
							info.put(option[j], null);		//將選項放入MAP, 初始化都是零票
						}
						Elements ele = doc.getElementsByClass("push");
						for (int i = 0; i < ele.size(); i ++) {
							String id = ele.get(i).select("span").get(1).text();
							String content = ele.get(i).select("span").get(2).text();
							
							String vote = "";
							if (content.contains("@")) {
								vote = content.substring(content.indexOf(":") + 1, content.indexOf("@")).replace("　", "").trim();
							}
							String date = ele.get(i).select("span").get(3).text().substring(0, 5);
							if (sDate == null || eDate == null 
									|| sDate.trim().length() == 0 || eDate.trim().length() == 0) {	//不開啓日期過濾
								process(id, vote, result, user, count, reVote, userId, info);
							} else if (dateCheck(date, sDate, eDate)) {	//開啟日期過濾
								process(id, vote, result, user, count, reVote, userId, info);
							}
						}
						JSONArray joArray = new JSONArray();
						int totalVoteCount = 0;
						for (int i = 0; i < option.length; i ++) {
							JSONObject jo = new JSONObject();
							jo.put("keyword", option[i]);
							jo.put("count", result.get(option[i]));
							jo.put("voter", info.get(option[i]));
							totalVoteCount = totalVoteCount + (info.get(option[i]) == null ? 0 : info.get(option[i]).size());
							joArray.add(jo);
						}
						JSONObject jo2 = new JSONObject();
						jo2.put("tv", totalVoteCount);
						jo2.put("tu", user.size());
						joArray.add(jo2);
						resp.getWriter().write(joArray.toJSONString());
					} else if (input == null) {
						resp.getWriter().println("1");
					}

				} else {
					resp.getWriter().println("0");	
				}
			} catch (HttpStatusException e) {
				System.out.println("HttpStatusException : " + e.getMessage());
				resp.getWriter().println("2");	
			} catch (ParseException e) {
				System.out.println("ParseException : " + e.getMessage());
				resp.getWriter().println("3");
			}
		}
		System.out.println("done");
	}
	
	public static void process (String id, String vote, Map<String, Integer> result, 
			Map<String, List<String>> user, String co, String reVote, List<String> userId,
			Map<String, List<String>> info) {
		
		int voted = 0;
		if (user.get(id) != null) {
			voted = user.get(id).size();
		}
		int count = Integer.parseInt(co);
		if (!user.containsKey(id) && result.containsKey(vote) && voted == 0) {	
			//還沒投過, 直接塞值進去即可
			result.put(vote, result.get(vote) + 1);
			List<String> tmp = new ArrayList<String>();
			tmp.add(vote);
			user.put(id, tmp);
			userId.add(id);
			List<String> tt = info.get(vote);
			if (tt == null) {
				tt = new ArrayList<String>();
				tt.add(id);
			} else {
				if (!tt.contains(id)) {
					tt.add(id);
				}
			}
			info.put(vote, tt);
			
		} else if (user.containsKey(id) && result.containsKey(vote) 
				&& reVote.equals("O") && count == 1 && voted == 1) {
			//已經投過，可以重投，不過只有一票，只要直接蓋過去就好 
			result.put(vote, result.get(vote) == null ? 0 : result.get(vote) + 1);	//給這次要投的加一票。
			String preVoteResult = user.get(id).get(0);								//第一次投的結果
			result.put(preVoteResult, result.get(preVoteResult) - 1);				//給上次投的扣一票。
			List<String> tmp = new ArrayList<String>();								//重新new個List
			tmp.add(vote);															//把選項加進去
			user.put(id, tmp);														//直接蓋過去就好
			userId.add(id);

			List<String> tmpString = info.get(preVoteResult);
			tmpString.remove(id);
			info.put(preVoteResult, tmpString);
			
			List<String> tt = info.get(vote);
			if (tt == null) {
				tt = new ArrayList<String>();
				tt.add(id);
			} else {
				if (!tt.contains(id)) {
					tt.add(id);
				}
			}
			info.put(vote, tt);

		} else if (user.containsKey(id) && result.containsKey(vote) 
				&& reVote.equals("X") && count > 1 && count > voted) {
			//已經投過，不可以重投，但有一票以上可以投，只要沒有超出限定的票數，可以繼續給他投。
			List<String> userObj = user.get(id);		//這個ID投過的票
			if (!userObj.contains(vote)) {				//看看有沒有投過這個選項
				userObj.add(vote);						//沒有的話加進List記錄內
				user.put(id, userObj);					//再把資料放進userMap
				result.put(vote, result.get(vote) + 1);	//給這次要投的加一票。
				userId.add(id);
				List<String> tt = info.get(vote);
				if (tt == null) {
					tt = new ArrayList<String>();
					tt.add(id);
				} else {
					if (!tt.contains(id)) {
						tt.add(id);
					}
				}
				info.put(vote, tt);
			}
		} else if (user.containsKey(id) && result.containsKey(vote) 
				&& reVote.equals("O") && count > 1 && count >= voted) {	
			//已經投過，可以重投，且可以投的票數大於一票，沒有超出限定的票數，才繼續給他投
			List<String> userObj = user.get(id);							//這個ID投過的票
			if (!userObj.contains(vote) && count == voted) {				//沒有投過這個選項, 且票數已經滿了
				String preVoteResult = user.get(id).get(0);					//先找出第一次投的結果
				result.put(preVoteResult, result.get(preVoteResult) - 1);	//給第一次投的扣一票。
				userObj.remove(preVoteResult);								//把第一次投的結果從List移除。
				result.put(vote, result.get(vote) + 1);						//給這次要投的加一票。
				userObj.add(vote);											//沒有的話加進List記錄內
				user.put(id, userObj);										//再把資料放進userMap
				info.get(preVoteResult).remove(id);
				List<String> tt = info.get(vote);	
				if (tt == null) {
					tt = new ArrayList<String>();
					tt.add(id);
				} else {
					if (!tt.contains(id)) {
						tt.add(id);
					}
				}
				info.put(vote, tt);
				
			} else if (!userObj.contains(vote) && count >= voted) {	//沒有投過這個選項, 票數尚未滿
				result.put(vote, result.get(vote) + 1);				//給這次要投的加一票。
				userObj.add(vote);									//沒有的話加進List記錄內
				user.put(id, userObj);								//再把資料放進userMap
				
				List<String> tt = info.get(vote);
				if (tt == null) {
					tt = new ArrayList<String>();
					tt.add(id);
				} else {
					if (!tt.contains(id)) {
						tt.add(id);
					}
				}
				info.put(vote, tt);
			}
			userId.add(id);
		}
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
