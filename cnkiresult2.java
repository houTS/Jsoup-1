
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 更改数据库
 * 更改搜索词
 * 用户输入搜索词 指定数据库 能得到标题 作者 摘要信息
 * 
 */
public class cnkiresult2 {
	
	//cnki数据库，还不完整。。。还有什么百科 等等库
	  static String  db="CCND";//报纸
	  static String  db1="SCDB";//文献
	  static String  db2="CJFQ";//期刊
	  static String  db3="CIPO";//会议
	  static String  db4="CDMD";//硕博士论文
	  static String  db5="WWJD";//外文文献
	  static String  db6="CYFD";//年鉴
	  static String  db7="SCOD";//专利
	  static String  db8="CISD";//标准
	  static String  db9="CSYD";//统计数据。。。。
	  
	  static String cat=db4;//挑选查询的数据库
	  
	  
    public static void main(String[] args) {
    	
    	//列表url
    	String sousuo="航天";//搜索的词语
    	Map<String,String> cookies = getCookie(sousuo);

    	if(cookies.size() >0){
    		String listUrl = "http://epub.cnki.net/kns/brief/brief.aspx?curpage=1&RecordsPerPage=20&QueryID=0&ID=&turnpage=1&tpagemode=L&dbPrefix="+cat+"&Fields=&DisplayMode=listmode&PageName=ASP.brief_default_result_aspx#J_ORDER";
    		//文章最初链接
        	List<String> articleInitUrls = new ArrayList<String>();
    		Connection conn = Jsoup.connect(listUrl);
    		conn.method(Method.GET);
        	conn.followRedirects(false);
        	conn.timeout(5000);
        	conn.cookies(cookies);
        	try {
				Document doc = conn.get();
				Elements links = doc.select("a.fz14");
				if(links.size() <=  0){
					System.out.println("没有更多文章。");
				}else{
					for(Element link : links){
						//得到的是最初链接，没有后缀的，到nh为止的
						//articleInitUrls.add("http://epub.cnki.net"+link.attr("href"));
						articleInitUrls.add(link.attr("abs:href"));
					}
				}
				
			} catch (IOException e) {
				System.out.println("链接超时了！");
			}
        	
        	if(articleInitUrls.size()<=0){
        		System.out.println("没有文章！");
        	}
        	
        	//articleInitUrl准备存入 重定向的链接了。。。。
        	
        	for(String articleInitUrl : articleInitUrls){
        		Connection conn2 = Jsoup.connect(articleInitUrl);
        		//???
        		conn2.header("Referer", listUrl);
        		conn2.cookies(cookies);
        		conn2.followRedirects(false);
                 try {
    				Document doc = conn2.get();
    				Elements links = doc.select("h2 > a[href]");
    				if(links.size()<=0){
    					System.out.println("最初链接为：【"+articleInitUrl+"】的文章获取实际链接失败！");
    				}else{
    					//文章获取实际链接ing
    					String articleUrl = links.get(0).attr("href");
    					
    					System.out.println("URL链接： "+articleUrl);
    					
    				    String divContent=getContentByJsoup(articleUrl);  
    				    getLinksByJsoup(divContent);  
    				    System.out.println("**********************");
    				    	
    				}
    			} catch (IOException e) {
    				System.out.println("最初链接为：【"+articleInitUrl+"】的文章链接超时！");
    			}
        	}
    	}
    	else {
			System.out.println("获取cookies wrong!");
		}
    	
    }
    
    public static Map<String,String> getCookie(String sousuo) {
    	//生成cookie的url 
    	String cookieUrl = "http://epub.cnki.net/KNS/request/SearchHandler.ashx?action=&NaviCode=*&ua=1.11&PageName=ASP.brief_default_result_aspx&DbPrefix="+cat+"&txt_1_sel=FT%24%25%3D|&txt_1_value1="+sousuo+"&txt_1_special1=%25&his=0&parentdb="+cat;
        Connection conn = Jsoup.connect(cookieUrl);
    	conn.method(Method.GET);
    	conn.followRedirects(false);
    	conn.timeout(5000);
    	Response response;
		try {
			response = conn.execute();
			return response.cookies();  	
		} catch (IOException e) {
			System.out.println("获取cookies的链接超时了。你懂的！");
			return new HashMap<String,String>();
		}
    	
    }
    
    public static String getContentByJsoup(String url){  
    	//解析整个网页
        String content="";  
        try {  
            Document doc=Jsoup.connect(url)  
            .data("jquery", "java")  
            .userAgent("Mozilla")  
            .cookie("auth", "token")  
            .timeout(50000)  
            .get();  
            content=doc.toString();//获取iteye网站的源码html内容  
           // System.out.println(doc.title());//获取iteye网站的标题  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    	//定位到一个mainleft的标签
        String divContent="";  
        Document doc=Jsoup.parse(content);  
        Elements divs=doc.getElementsByClass("mainleft");  
        divContent=divs.toString();  
        return divContent;  
    }  


    public static void getLinksByJsoup(String divContent){  
      //从这个标签获得子标签内容
        Document doc=Jsoup.parse(divContent);  
        
        Element linkStr=doc.getElementsByTag("h1").get(0);  
        String title=linkStr.getElementsByTag("span").text();  
        System.out.println("标题:"+title);  
        
        Element linkStr2=doc.getElementsByTag("div").get(0);  
        String author=linkStr2.getElementsByTag("p").get(0).text();  
        System.out.println("作者:"+author);  
        
        
        Element linkStr3=doc.getElementsByTag("div").get(0);  
        String summary=linkStr3.getElementsByTag("p").get(3).text();  
        System.out.println("摘要:"+summary);  
           
    }  
}
