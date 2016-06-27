/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webpagesclassificator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import java.util.Stack;

import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Lee
 */
public class WebPagesClassificator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        
        float total=0.0f;
        int index = 0, min = 1, max = 100;
        LinkExtractor l1 = new LinkExtractor("C://Users/Lee/Desktop/dati.u8");
        Set dati = l1.getLinks();
        Set CS = new Set();
        Set TS = new Set();

        Random rn = new Random();
        


        for (int i = 0; i < dati.cat.size(); i++) {
            if ((rn.nextInt(max - min + 1) + min) >= 80) {
                CS.cat.add(dati.cat.get(i));
                CS.url.add(dati.url.get(i));
            } else {
                TS.cat.add(dati.cat.get(i));
                TS.url.add(dati.url.get(i));
            }
        }




        List<WebSites> CSlist = getTokens(CS);
        List<WebSites> TSlist = getTokens(TS);

        boolean truep=false;
        for (int i=0;i<CSlist.size();i++)
         truep= Classify(CSlist.get(i),TSlist);
    }

    public static int getIndex(String s) {

        int val = 0, index = 0;
        for (val = 0; val < s.length() && index < 3; val++) {
            if (s.charAt(val) == '/') {
                index++;
            }
        }

        return val;
    }

    public static List<WebSites> getTokens(Set temp) throws MalformedURLException {

        String categ = temp.cat.get(0);
        int val = getIndex(temp.cat.get(0));
        categ = categ.substring(0,val);
        String text = null;
        List<WebSites> db= new ArrayList<WebSites>();;
        for (int k = 0; k < temp.url.size(); k++) {

 
            HashMap<String, Float> h = new HashMap<String, Float>();
            
            URL oracle = new URL(temp.url.get(k));
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    text = text + inputLine;
                }

                in.close();
                Document doc = Jsoup.parse(text);
                String s = doc.body().text();

                int j = 0;
                boolean beenthere = false;
                Stack stack = new Stack();
                Pattern p1 = Pattern.compile("[a-zA-Z0-9'$€-]");
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

                        if (h.get(tmp2) == null) {
                            h.put(tmp2, 1.0f);
                        } else {
                            h.replace(tmp2, h.get(tmp2) + 1);
                        }

                    }

                }


                text = null;
                if (!categ.equals(temp.cat.get(k+1).substring(0, val))) {

                    db.add(new WebSites(h,categ));
                    val = getIndex(temp.cat.get(k+1));
                    categ = temp.cat.get(k+1).substring(0, val);
                    h = new HashMap<String, Float>();
                    

                }

            } catch (Exception e) {
                e.toString();
            }

        }
        return db;
    }
    
    public static boolean Classify(WebSites h, List<WebSites> TS)
    {
        
        HashMap<String, Float> map = new HashMap<String, Float>();
        int occ=0;
        for (int i=0;i<TS.size();i++)
            map.put(TS.get(i).cat, 0.0f);
        

        for (String s : h.count.keySet() )
        {
            for (int i=0;i<TS.size();i++)
            {  
                if(TS.get(i).count.get(s) != null)
                {
                    occ=(int) (occ+TS.get(i).count.get(s));
                }
                
            }
            for (int i=0;i<TS.size();i++)
            {

                if(occ>0 && TS.get(i).count.get(s) != null)
                {
                    
                   // System.out.println("La stringa "+s+" ha frequenza "+(TS.get(i).count.get(s)/occ)*(TS.get(i).count.size())+"appare in questa cat "+ TS.get(i).count.get(s)+" volte, nella sua cat ci sono "+TS.get(i).count.size()+" parole. In tutte le cat "+occ+" parole");
                    map.replace(TS.get(i).cat, map.get(TS.get(i).cat)+(TS.get(i).count.get(s)/occ)*(TS.get(i).count.size()));
                
                }
            }
            occ=0;
        }
        

        
        Map.Entry<String, Float> maxEntry = null;

        for (Map.Entry<String, Float> entry : map.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }       
        
        
        System.out.println("Predicted: "+maxEntry+" Actual category: "+h.cat);     // Print the key with max value
            
        


        return false;
    }

}
