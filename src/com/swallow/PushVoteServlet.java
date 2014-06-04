package com.swallow;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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

    private Utils utils = new Utils();
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doProcess(req, resp);
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doProcess(req, resp);
    }
    
    @SuppressWarnings("unchecked")
    public void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        String keyword = "[推投]";
        String sDate = req.getParameter("sDate");
        String eDate = req.getParameter("eDate");
        String options = req.getParameter("hiddenValue") == null || req.getParameter("hiddenValue").trim().length() == 0 ? "none" : req.getParameter("hiddenValue");
        String reVote = req.getParameter("revote");
        String count = req.getParameter("count") == null || req.getParameter("count").trim().length() == 0 || req.getParameter("count").equals("0") ? "1" : req.getParameter("count");
        int co = Integer.parseInt(count);
        String url = req.getParameter("url");
        
        //提名模式
        String nModeMin = req.getParameter("nominateMin") == null || req.getParameter("nominateMin").trim().length() == 0 ? "1" : req.getParameter("nominateMin");
        int nMin = Integer.parseInt(nModeMin);  //最少要有幾票才會納入投票

        String input = req.getParameter("input");
        //single, oneline, web, nominate
        
        String pr = req.getParameter("pointrank");
        int p0 = -1;
        int p1 = -1;
        int p2 = -1;
        int p3 = -1;
        int p4 = -1;
        int p5 = -1;
        int p6 = -1;
        int p7 = -1;
        int p8 = -1;
        int p9 = -1;
        
        Map<String, Integer> points = new HashMap<String, Integer>();
        if (pr != null && pr.equals("O")) {
            try {
                p0 = req.getParameter("p0") != null ? Integer.parseInt(req.getParameter("p0")) : -1;
                points.put("p0", p0);
                p1 = req.getParameter("p1") != null ? Integer.parseInt(req.getParameter("p1")) : -1;
                points.put("p1", p1);
                if (co > 2) {       // 三票
                    p2 = req.getParameter("p2") != null ? Integer.parseInt(req.getParameter("p2")) : -1;
                    points.put("p2", p2);
                }
                if (co > 3) {
                    p3 = req.getParameter("p3") != null ? Integer.parseInt(req.getParameter("p3")) : -1;
                    points.put("p3", p3);
                }
                if (co > 4) {
                    p4 = req.getParameter("p4") != null ? Integer.parseInt(req.getParameter("p4")) : -1;
                    points.put("p4", p4);
                }
                if (co > 5) {
                    p5 = req.getParameter("p5") != null ? Integer.parseInt(req.getParameter("p5")) : -1;
                    points.put("p5", p5);
                }
                if (co > 6) {
                    p6 = req.getParameter("p6") != null ? Integer.parseInt(req.getParameter("p6")) : -1;
                    points.put("p6", p6);
                }
                if (co > 7) {
                    p7 = req.getParameter("p7") != null ? Integer.parseInt(req.getParameter("p7")) : -1;
                    points.put("p7", p7);
                }
                if (co > 8) {
                    p8 = req.getParameter("p8") != null ? Integer.parseInt(req.getParameter("p8")) : -1;
                    points.put("p8", p8);
                }
                
                if (co > 9 && co <= 10) {
                    p9 = req.getParameter("p9") != null ? Integer.parseInt(req.getParameter("p9")) : -1;
                    points.put("p9", p9);
                }                
            } catch (Exception e) {}
        }
        if (url == null || url.trim().length() == 0 || !url.contains("ptt.cc")) {
            resp.getWriter().println("4");
        } else {
            try {
                Document doc = Jsoup.connect(url).timeout(10000).get();
                Document doc2 = doc.clone();
                Map<String, Integer> result = new HashMap<String, Integer>();           //裝載投票結果, 選項/總票數
                Map<String, List<String>> user = new HashMap<String, List<String>>();   //裝載使用者資訊, IDs/所投選項
                Map<String, List<String>> info = new HashMap<String, List<String>>();   //裝載選票資訊, 所投選項/IDs

                Map<String, Integer> result2 = new HashMap<String, Integer>();          //裝載投票結果, 選項/積分數
                
                List<String> userId = new ArrayList<String>();                          //所有使用者
                
                if (doc.select("title").text().startsWith(keyword) || !doc.select("title").text().startsWith(keyword)) {
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

                        Elements ele = doc.getElementsByClass("push");
                        if (!input.equals("nominate")) {
                            for (int j = 0; j < option.length; j ++) {
                                result.put(option[j].trim(), 0);        //將選項放入MAP, 初始化都是零票
                                info.put(option[j].trim(), null);       //將選項放入MAP, 初始化都是零票
                            }
                        } else {
                            Elements ele2 = ele.clone();
                            StringBuffer tmpBuffer = new StringBuffer("");
                            for (int k = 0; k < ele2.size(); k ++) {
                                String tmp1 = ele2.get(k).getElementsByClass("push-content").text();
                                if (tmp1.charAt(0) == ':' && tmp1.contains("@") && tmp1.indexOf("@") == tmp1.lastIndexOf("@")) {
                                    String tmpContent = tmp1.substring(tmp1.indexOf(":") + 1, tmp1.indexOf("@"));
                                    if (!result.containsKey(tmpContent)) {
                                    	result.put(tmpContent.trim(), 0);       //將選項放入MAP, 初始化都是零票
                                        tmpBuffer.append(tmpContent.trim());
                                        if (k != ele2.size() - 1) {
                                        	tmpBuffer.append(",");  
                                        }
                                    }
                                    if (!info.containsKey(tmpContent))
                                    	info.put(tmpContent.trim(), null);      //將選項放入MAP, 初始化都是零票
                                } else if (tmp1.indexOf("@") != tmp1.lastIndexOf("@")) {
                                    String tmpContent = tmp1.substring(tmp1.indexOf(":") + 1, tmp1.lastIndexOf("@"));
                                    String[] tmps = tmpContent.split("@");
                                    for (int ii = 0; ii < tmps.length; ii ++) {
                                        if (!result.containsKey(tmps[ii].trim())) {
                                        	result.put(tmps[ii].trim(), 0);       //將選項放入MAP, 初始化都是零票
                                            tmpBuffer.append(tmps[ii].trim());
                                            if (k != ele2.size() - 1) {
                                            	tmpBuffer.append(",");  
                                            }
                                        }
                                        if (!info.containsKey(tmps[ii].trim()))
                                        	info.put(tmps[ii].trim(), null);      //將選項放入MAP, 初始化都是零票
                                    }
                                }
                            }
                            option = tmpBuffer.toString().split(",");
                        }
                        
                        for (int i = 0; i < ele.size(); i ++) {
                            String id = ele.get(i).select("span").get(1).text();
                            String content = ele.get(i).select("span").get(2).text();
                            
                            String vote = "";
                            String[] tmp = null; 
                            String date = ele.get(i).select("span").get(3).text().substring(0, 5);
                            if (content.contains("@")) {
                                if (content.indexOf("@") != content.lastIndexOf("@")) { //多個 @, 也就是有可能多筆投票
                                    String tmpContent = content.substring(content.indexOf(":") + 1, content.lastIndexOf("@"));
                                    tmp = tmpContent.split("@");
                                } else {                                                //位置在同一個點，所以應該是單筆投票
                                    vote = content.substring(content.indexOf(":") + 1, content.indexOf("@")).replace("　", "").trim();
                                }
                            }
                            
                            if (!vote.equals("") && tmp == null) {
                                if (sDate == null || eDate == null || sDate.trim().length() == 0 || eDate.trim().length() == 0) {   //不開啓日期過濾
                                    process(id, vote, result, user, count, reVote, userId, info, points, pr, result2);
                                } else if (utils.dateCheck(date, sDate, eDate)) {   //開啟日期過濾
                                    process(id, vote, result, user, count, reVote, userId, info, points, pr, result2);
                                }
                            } else if (vote.equals("") && tmp != null) {    //多票數回圈     
                                for (int k = 0; k < tmp.length; k ++) {
                                    String tmpVote = tmp[k] == null || tmp[k].trim().length() == 0 ? "" : tmp[k].replace("　", "").trim();
                                    if (sDate == null || eDate == null || sDate.trim().length() == 0 || eDate.trim().length() == 0) {   //不開啓日期過濾
                                        process(id, tmpVote, result, user, count, reVote, userId, info, points, pr, result2);
                                    } else if (utils.dateCheck(date, sDate, eDate)) {   //開啟日期過濾
                                        process(id, tmpVote, result, user, count, reVote, userId, info, points, pr, result2);
                                    }
                                }
                            }
                        }
                        JSONArray joArray = new JSONArray();
                        int totalVoteCount = 0;
                        List<String> processDone = new ArrayList<String>();//裝載處理完畢IDs
                        //result2 把積分分數放進去
                        //info -> 選項/IDs, 用選項把ID拿出來，然後處理ID後放回去
                        if (pr.equals("O")) {
                            for (int j = 0; j < userId.size(); j ++) {
                                if (!processDone.contains(userId.get(j))) {
                                    for (int k = 0; k < user.get(userId.get(j)).size(); k++) {  //看有幾個選項
                                        String tmp = user.get(userId.get(j)).get(k);    //選項loop
                                        List<String> myids = info.get(tmp); //所有投這個選項的ID
                                        myids.remove(userId.get(j));
                                        String tmpStr = "p" + k;
                                        myids.add(userId.get(j)+"("+points.get(tmpStr)+")");
                                        if (result2.get(tmp) == null) {
                                            result2.put(tmp, points.get(tmpStr));
                                        } else {
                                            result2.put(tmp, result2.get(tmp) + points.get(tmpStr));
                                        }
                                    }
                                    processDone.add(userId.get(j));
                                }
                            }
                        }
                        for (int i = 0; i < option.length; i ++) {
                            JSONObject jo = new JSONObject();
                            int mycount = result.get(option[i].trim());
                            if (input.equals("nominate")) {
                            	if (nMin <= mycount) {
	                                jo.put("keyword", option[i].trim());
	                                jo.put("count", mycount);
	                                jo.put("voter", info.get(option[i].trim()));
	                                jo.put("points", result2.get(option[i].trim()));
	                                totalVoteCount = totalVoteCount + (info.get(option[i].trim()) == null ? 0 : info.get(option[i].trim()).size());
                            		joArray.add(jo);
                            	} else {
                            		if (mycount != 0) {
                            			for (int ii = 0; ii < info.get(option[i].trim()).size(); ii ++) {
                            				String id = info.get(option[i].trim()).get(ii);
                            				if (pr.equals("O")) {
                            					id = id.substring(0, id.indexOf("("));
                            				}
                    						List<String> t = user.get(id);
                    						t.remove(option[i].trim());
                            				user.put(id, t);
                            			}
                            			result.remove(option[i].trim());
                            			info.remove(option[i].trim());
                            		}
                            	}
                            } else {
                                jo.put("keyword", option[i].trim());
                                jo.put("count", mycount);
                                jo.put("voter", info.get(option[i].trim()));
                                jo.put("points", result2.get(option[i].trim()));
                                totalVoteCount = totalVoteCount + (info.get(option[i].trim()) == null ? 0 : info.get(option[i].trim()).size());
                            	joArray.add(jo);
                            }
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
    }
    
    public static void process (String id, String vote, Map<String, Integer> result, 
            Map<String, List<String>> user, String co, String reVote, List<String> userId,
            Map<String, List<String>> info, Map<String, Integer> points, String pr, Map<String, Integer> result2) {
        
        int voted = 0;
        if (user.get(id) != null) {
            voted = user.get(id).size();
        }
        int count = Integer.parseInt(co);
        if (!user.containsKey(id) && result.containsKey(vote) && voted == 0) {  
//          result2.put(vote,  result2.get(vote) + points.get("p0"));
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
            result.put(vote, result.get(vote) == null ? 0 : result.get(vote) + 1);  //給這次要投的加一票。
            String preVoteResult = user.get(id).get(0);                             //第一次投的結果
            result.put(preVoteResult, result.get(preVoteResult) - 1);               //給上次投的扣一票。
            List<String> tmp = new ArrayList<String>();                             //重新new個List
            tmp.add(vote);                                                          //把選項加進去
            user.put(id, tmp);                                                      //直接蓋過去就好
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
            List<String> userObj = user.get(id);        //這個ID投過的票
            if (!userObj.contains(vote)) {              //看看有沒有投過這個選項
                userObj.add(vote);                      //沒有的話加進List記錄內
                user.put(id, userObj);                  //再把資料放進userMap
                result.put(vote, result.get(vote) + 1); //給這次要投的加一票。
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
            List<String> userObj = user.get(id);                            //這個ID投過的票
            if (!userObj.contains(vote) && count == voted) {                //沒有投過這個選項, 且票數已經滿了
                String preVoteResult = user.get(id).get(0);                 //先找出第一次投的結果
                result.put(preVoteResult, result.get(preVoteResult) - 1);   //給第一次投的扣一票。
                userObj.remove(preVoteResult);                              //把第一次投的結果從List移除。
                result.put(vote, result.get(vote) + 1);                     //給這次要投的加一票。
                userObj.add(vote);                                          //沒有的話加進List記錄內
                user.put(id, userObj);                                      //再把資料放進userMap
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
                
            } else if (!userObj.contains(vote) && count >= voted) { //沒有投過這個選項, 票數尚未滿

                result.put(vote, result.get(vote) + 1);             //給這次要投的加一票。
                userObj.add(vote);                                  //沒有的話加進List記錄內
                user.put(id, userObj);                              //再把資料放進userMap
                
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
}