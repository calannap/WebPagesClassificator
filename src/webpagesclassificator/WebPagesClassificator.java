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

import java.util.HashMap;
import java.util.List;
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


        System.out.println(TS.cat.size());
        System.out.println(CS.cat.size());

        List<WebSites> CSlist = getTokens(CS);
        List<WebSites> TSlist = getTokens(TS);
        
        System.out.println(TSlist+" "+CSlist.size());
        
        for ( int i=0;i<TSlist.size();i++)
        {
            for (float f : TSlist.get(i).count.values()) {
                total += f;
                }
            //int j=0;j<TSlist.get(i).count.size();j++

            for ( String s : TSlist.get(i).count.keySet() )
            {
                TSlist.get(i).count.replace(s, (TSlist.get(i).count.get(s)/total));
            }
            total=0;
        }
        
        for (int k=0;k<TSlist.size();k++){
            System.out.println(TSlist.get(k).cat);
                System.out.println(TSlist.get(k).count);
        }
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
                if (!categ.equals(temp.cat.get(k).substring(0, val))) {


                    db.add(new WebSites(h,categ));
                    val = getIndex(temp.cat.get(k));
                    categ = temp.cat.get(k).substring(0, val);
                    h = new HashMap<String, Float>();

                }

            } catch (Exception e) {
                e.toString();
            }

        }
        System.out.println("Grandezza db: "+db.size());
        return db;
    }

}
