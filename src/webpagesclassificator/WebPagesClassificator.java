/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webpagesclassificator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Socket;
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

        System.out.println(dati.cat.size());
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

        System.out.println("Nel CS ci sono "+CS.cat.size()+" siti");
        System.out.println("Nel TS ci sono "+TS.cat.size()+" siti");

        List<WebSites> CSlist = getTokens(CS,true);
        List<WebSites> TSlist = getTokens(TS,false);

        saveFile(CSlist,"C:\\Users\\Lee\\Desktop\\CS.ser");
        saveFile(TSlist,"C:\\Users\\Lee\\Desktop\\TS.ser");
        
        //List<WebSites> CSlist = loadFile("C:\\Users\\Lee\\Desktop\\CS.ser");
       // List<WebSites> TSlist = loadFile("C:\\Users\\Lee\\Desktop\\TS.ser");
        

        System.out.println("Nel CS ci sono "+CSlist.size()+" siti");
        System.out.println("Nel TS ci sono "+TSlist.size()+" categorie");
        int tr=0;
        boolean truep=false;

        for (int i=0;i<CSlist.size();i++)
        {
         truep= Classify(CSlist.get(i),TSlist);
         if(truep)
             tr++;
        }
        System.out.println("TP: "+tr+" su "+CSlist.size());
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

    public static List<WebSites> getTokens(Set temp, boolean iscs) throws MalformedURLException, ProtocolException, IOException {

        String categ = temp.cat.get(0);
        int val = getIndex(temp.cat.get(0));
        categ = categ.substring(0,val);
        String text = null;
        List<WebSites> db= new ArrayList<WebSites>();;
        for (int k = 0; k < temp.url.size(); k++) {

        try {
            System.out.println(k+" of "+temp.url.size());
            URL oracle = new URL(temp.url.get(k));
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) oracle.openConnection();
            connection.setRequestMethod("HEAD");
            HashMap<String, Float> h = new HashMap<String, Float>();
            System.out.println(temp.url.get(k).substring(7,temp.url.get(k).length()-1));
            if( connection.getResponseCode()==200){

                    
                    System.out.println(oracle);
                    
                    System.out.println("I smell the memems");
                    BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                    System.out.println(in.readLine());
                    System.out.println("I need the memems");
                    String inputLine;
                    
                    if(in.ready())
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Increase text");
                        text = text + inputLine;
                    }
      
                    System.out.println("Fine increase text");
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
                    if (!categ.equals(temp.cat.get(k+1).substring(0, val)) || iscs) {

                        db.add(new WebSites(h,categ));
                        val = getIndex(temp.cat.get(k+1));
                        categ = temp.cat.get(k+1).substring(0, val);
                        h = new HashMap<String, Float>();


                    }
                    }
                } catch (Exception e) {e.toString();}
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
                    
                   // occ=(int) Math.sqrt(occ);   
                    float freqCat = (((TS.get(i).count.get(s)*TS.get(i).count.get(s))/occ)*1/TS.get(i).count.size());
                    // System.out.println("La stringa "+s+" ha frequenza "+freqCat+" appare in "+TS.get(i).cat+" "+ TS.get(i).count.get(s)+" volte, nella sua cat ci sono "+TS.get(i).count.size()+" parole. In tutte le cat appare "+occ+" volte");
                   
                    map.replace(TS.get(i).cat, map.get(TS.get(i).cat)+(freqCat));
                
                }
            }
            occ=0;
        }
        
      
        
        Map.Entry<String, Float> maxEntry1 = null;
        Map.Entry<String, Float> maxEntry2 = null;
        Map.Entry<String, Float> maxEntry3 = null;

        for (Map.Entry<String, Float> entry : map.entrySet())
        {
            if (maxEntry1 == null || entry.getValue().compareTo(maxEntry1.getValue()) > 0)
            {
                maxEntry1 = entry;
                
            }
        }       
        map.remove(maxEntry1.getKey());

        for (Map.Entry<String, Float> entry : map.entrySet())
        {
            if (maxEntry2 == null || entry.getValue().compareTo(maxEntry2.getValue()) > 0)
            {
                maxEntry2 = entry;
                
            }
        }     
        map.remove(maxEntry2.getKey());

        for (Map.Entry<String, Float> entry : map.entrySet())
        {
            if (maxEntry3 == null || entry.getValue().compareTo(maxEntry3.getValue()) > 0)
            {
                maxEntry3 = entry;
                
            }
        }     
        map.remove(maxEntry3.getKey());

        
      //  System.out.println("Predicted: "+maxEntry.getKey()+" Actual category: "+h.cat);     // Print the key with max value
  
        if(maxEntry1.getKey().equals(h.cat) || maxEntry2.getKey().equals(h.cat) || maxEntry3.getKey().equals(h.cat))
            return true;
        
        return false;


    }
    
    public static void saveFile(Object c,String s)
    {
                try{
		   
		FileOutputStream fout = new FileOutputStream(s);
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(c);
		oos.close();
		System.out.println("Done");
		   
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
              
    }
    
    public static List<WebSites> loadFile(String s)
    {
        	   List<WebSites> address;
	 
	   try{
		    
		   FileInputStream fin = new FileInputStream(s);
		   ObjectInputStream ois = new ObjectInputStream(fin);
		   address = (List<WebSites>) ois.readObject();
		   ois.close();
		  
		   return address;
		   
	   }catch(Exception ex){
		   ex.printStackTrace();
		   return null;
	   } 
    
    }
    

}


