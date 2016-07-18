/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webpagesclassificator;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import static webpagesclassificator.WebPagesClassificator.getIndex;


/**
 *
 * @author Lee
 */
public class CatCreator extends Thread implements Runnable{

    private WebSites value;
    private Set temp;
    private boolean iscs;
    private int start; 
    private int end;
    public static int count=0;
    private static int numsito=0;
    private static int nonan=0;
    private HtmlPage votePage;
    public CatCreator( Set t1, boolean t2, int t3, int t4)
    {
        temp=t1;
        iscs=t2;
        start=t3;
        end=t4;
        count++;
    }
    
    @Override
    public void run() 
    {
        WebClient webClient = new WebClient();
        webClient.getOptions().setRedirectEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setJavaScriptEnabled(false);
//        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setUseInsecureSSL(false);
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        for (int k = start; k < end; k++) 
        {
        String text=null;
        String categ = temp.cat.get(k);
        int val = getIndex(temp.cat.get(k));
        categ = categ.substring(0,val);
        try {
            HashMap<String, Float> h = new HashMap<String, Float>();
       String sito = temp.url.get(k);
            System.out.println((numsito++) + " of " + temp.url.size());
            System.out.println(sito);

             votePage = null;
            try {
                Thread thread = new Thread("New Thread") {
                    @Override
                    public void run() {
                        try {
                            votePage = (HtmlPage) webClient.getPage(sito);
                        } catch (Exception ef) {

                        }
                    }
                };
                thread.start();
                thread.join(10000);
                thread.interrupt();

            } catch (Exception e) {

               continue;
            }
        

                   
                  text = votePage.asText();
                
                
                    Document doc = Jsoup.parse(text);
                    String s = doc.body().text();

                    int j = 0;
                    boolean beenthere = false;
                    Stack stack = new Stack();
                    Pattern p1 = Pattern.compile("[\\p{L}0-9']");
                    Pattern p2 = Pattern.compile("[0-9]");
                    char[] schr = s.toCharArray();
                    char[] token = null;

                    String tmp;

                    for (int i = 0; i < s.length(); i++) {
                        tmp = s.substring(i, i + 1);

                        if (p1.matcher(tmp).find() && !p2.matcher(tmp).find()) {
                            stack.push(schr[i]);
                        } else if (stack.size() != 0) {
                            beenthere = true;
                            token = new char[stack.size()];
                            j = 0;
                            while (!stack.isEmpty()) {
                                token[j++] = (char) stack.pop();
                            }

                            String[] tokens = new String[j];
                            String tmp2 = String.valueOf(token);
                            tmp2 = new StringBuilder(tmp2).reverse().toString();
                            tmp2 = tmp2.toLowerCase();
                            if(tmp2.length()>1 && !tmp.equals("null")){
                            if (h.get(tmp2) == null) {
                                h.put(tmp2, 1.0f);
                            } else {
                                h.replace(tmp2, h.get(tmp2) + 1);
                            }
                            }
                        }

                    }


                    text = null;
                    // rimettere value dentro l'if commentato
                   // if (iscs || !categ.equals(temp.cat.get(k+1).substring(0, val)) ) {}
                   if(k==end-1){
                       value = (new WebSites(h,categ,text));
                   }
                } catch (Exception e) {System.err.println("SITO NUMERO"+ (++nonan)+"NON ANALIZZATO")
                        ;}
        }
        
        }
    
    public WebSites getValue() 
    {
         return value;
    }
    }

    
