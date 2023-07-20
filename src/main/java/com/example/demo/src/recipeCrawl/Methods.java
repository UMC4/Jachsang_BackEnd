package com.example.demo.src.recipeCrawl;

import com.example.demo.src.post.model.recipe.RecipeInsertReq;
import com.example.demo.src.recipeCrawl.model.Post;
import com.example.demo.src.recipeCrawl.model.RecipeReq;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Methods {

    /////////// 크롤링 메서드 ////////////////////////////////
    public ArrayList<String> getTitleList(Elements e){
        ArrayList<String> title = new ArrayList<>();
        String temp = "";
        String origin = e.toString();
        origin = origin.replace("<div class=\"common_sp_caption_tit line2\">", "");
        origin = origin.replace("</div>", "");
        origin = origin.replace("&lt", "<");
        origin = origin.replace("&gt", ">");
        for(int i = 0; i<origin.length(); i++) {
            if(origin.charAt(i)=='\n' || origin.charAt(i)=='\r') {
                if(temp.equals("")) continue;
                temp = temp.substring(1);
                title.add(temp);
                temp = "";
                continue;
            }
            temp += origin.charAt(i);
        }
        return title;
    }

    public ArrayList<String> getURL(Elements e){
        String temp = "/";
        String PostURL_FRONT = "https://www.10000recipe.com";
        String origin = e.select("a").toString().replace("<a href=\"","");
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> tempList = new ArrayList<>();

        for(int i = 0; i<origin.length(); i++) {
            if(origin.charAt(i)=='\n' || origin.charAt(i)=='\r') {
                if(temp.equals("")) continue;
                tempList.add(temp);
                temp = "";
            }
            temp += origin.charAt(i);
        }
        for(String s:tempList) {
            result.add(PostURL_FRONT+s.substring(1,16));
        }

        return result;
    }

    public ArrayList<String> getThumbnail(Elements e){
        String temp = "";
        String origin = e.select("img").toString().replace("\n<img src=\"https://recipe1.ezmember.co.kr/img/icon_vod.png\">", "").replace("<img src=\"", "").replace("\">", "");
        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i<origin.length(); i++) {
            if(origin.charAt(i)=='\n' || origin.charAt(i)=='\r') {
                if(temp.equals("")) continue;
                result.add(temp.replace("\n", ""));
                temp = "";
            }
            temp += origin.charAt(i);
        }

        return result;

    }

    public String getMainThumbnail(Elements e) {
        return e.toString().replace("<div class=\"centeredcrop\">", "").replace("", "").replace("</div>", "").replace(" <img id=\"main_thumbs\" src=\"", "").replace("","")
                .replace("\" alt=\"main thumb\">","").replace("\n","").replace("\r","");
    }

    public ArrayList<String> getIngredients(Elements e){
        String temp = "";
        String origin = e.select("a").text().replace(" 구매 ", ",").replace(" 구매","").replace("[재료] ","").replace(" ","");
        origin += ",";
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i<origin.length(); i++) {
            if(origin.charAt(i)==',') {
                result.add(temp.replace(",",""));
                temp = "";
                continue;
            }
            temp += origin.charAt(i);
        }
        return result;
    }

    public ArrayList<String> getDose(Elements e){
        String temp = "";
        String origin = e.select("span").toString().replace("<span class=\"ingre_unit\">","").replace("</span>","").replace("\n",",");
        origin += ",";
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i<origin.length(); i++) {
            if(origin.charAt(i)==',') {
                result.add(temp.replace(",",""));
                temp = "";
            }
            temp += origin.charAt(i);
        }
        return result;
    }

    public ArrayList<String> getDescription(Document doc){
        ArrayList<String> result = new ArrayList<>();

        for(int i = 1;;i++) {
            Elements e = doc.getElementsByClass("view_step_cont media step"+i);
            if(e.text().equals("") || e.text().equals("\n") || e.text().equals("\r")) return result;
            result.add(e.text());
        }

    }

    public ArrayList<String> __getDescImage(Document doc){
        ArrayList<String> result = new ArrayList<>();

        for(int i = 1;;i++) {
            Element e = doc.getElementById("stepimg"+i);
            if(e == null) return result;
            String url = e.toString().replace("<div id=\"stepimg"
                    + i
                    + "\">","").replace("</div>","").replace(" <img src=\"","").replace("\">","").replace("\n","").replace("\r","");
            if(url.contains("<div")) {
                Elements es = e.getElementsByClass("carouItem").select("img");
                url = es.toString().replace("<img src=\"","").replace("\">","").replace("\n",",");
                result.add(url);
                continue;
            }
            if(url == null) url = "없음";
            result.add(url);
        }
    }

    ///////////////////////////////////////////////////////////////
    ////////////////////// 내부 메서드 //////////////////////////////
    
    //페이지 전체 긁어와서 RecipeReq 형태로 저장하는 메서드
    public List<RecipeInsertReq> getRecipe(){
        System.setProperty("https.protocols","TLSv1.2");
        ArrayList<RecipeInsertReq> recipe = new ArrayList<>();
        for (int page = 1;page<2;page++) {

            String URL = "https://www.10000recipe.com/recipe/list.html?q=%EC%A7%91%EB%B0%A5%EB%B0%B1%EC%84%A0%EC%83%9D&order=reco&page="+page;

            Document doc;
            List<String> thumbnail;
            List<String> title;
            List<String> url;
            List<Post> post = new ArrayList<>();
            try {
                doc = Jsoup.connect(URL).get();

                //제목 수집하기
                title = getTitleList(doc.getElementsByClass("common_sp_caption_tit line2"));

                //썸네일 수집하기
                thumbnail = getThumbnail(doc.getElementsByClass("common_sp_thumb"));

                //url 수집하기 common_sp_link
                url = getURL(doc.getElementsByClass("common_sp_link"));




                //글 내부

                String postUrl = "";
                for(int j = 0; j<url.size(); j++) {
                    postUrl = url.get(j);
                    doc = Jsoup.connect(postUrl).get();
                    Post p = new Post(
                            getMainThumbnail(doc.getElementsByClass("centeredcrop")),
                            _array2String(getIngredients(doc.getElementsByClass("ready_ingre3"))),
                            _array2String(getDose(doc.getElementsByClass("ready_ingre3"))),
                            _array2String(getDescription(doc))
                    );
                    p.combine();
                    post.add(p);
                }

                for(int i = 0; i<title.size()-1;i++) {
                    recipe.add(new RecipeInsertReq(
                            title.get(i),"null",thumbnail.get(i),post.get(i).getIngredients(),post.get(i).getDescription(),
                            post.get(i).getFoodImage(),url.get(i)
                    ));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return recipe;
    }
    public String _array2String(List<String> array){
        String result = "";
        for(String s : array) {
            result += s;
            result += ",";
        }
        return result;
    }
}
