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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

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
        int index=0,val=0;
        String text=null;
        LinkExtractor l1 = new LinkExtractor("C://Users/Lee/Desktop/dati.u8");
        Set dati = l1.getLinks();
        
        String categ = dati.cat.get(0);
        
        
        val = getIndex(dati.cat.get(0));
        



        for (int k=0;k<dati.url.size() /*&& categ.equals(dati.cat.get(k))*/;k++){
            
            List<HashMap<String, Integer>> db = new ArrayList<HashMap<String, Integer>>();
            HashMap<String, Integer> h = new HashMap<String, Integer>();

            
            URL oracle = new URL(dati.url.get(k));
            try{ 
                BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    text=text+inputLine;
                in.close();
                Document doc = Jsoup.parse(text); 
                String s = doc.body().text();



                int j=0;
                boolean beenthere=false;
                Stack stack = new Stack();
                Pattern p1 = Pattern.compile("[a-zA-Z0-9'$€-]");
                Pattern p2 = Pattern.compile("[0-9]");
                char [] schr = s.toCharArray();
                char [] token = null;     

                String tmp;

                for (int i=0;i<s.length();i++){

                tmp = s.substring(i,i+1);

                //System.out.println(s.substring(i,i+1));
                if(p1.matcher(tmp).find() && !p2.matcher(tmp).find() )
                    stack.push(schr[i]);
                else if(stack.size()!=0){
                    beenthere=true;
                    //System.out.println("Stack size :"+ stack.size());
                    token = new char [stack.size()];
                    j=0;
                    while(!stack.isEmpty())                     
                        token[j++] = (char) stack.pop();

                    String [] tokens = new String [j];
                    String tmp2 = String.valueOf(token);
                    tmp2 = new StringBuilder(tmp2).reverse().toString();
                    tmp2 = tmp2.toLowerCase();


                    if(h.get(tmp2) == null)
                        h.put(tmp2, 1);
                    else
                        h.replace(tmp2, h.get(tmp2)+1);

                    }
                     
                
                }
           
                          
            if(!categ.equals(dati.cat.get(k).substring(0,val))){
                
                val = getIndex(dati.cat.get(k));
                categ=dati.cat.get(k).substring(0,val);
                db.add(h);
                System.out.println(h); 
                System.out.println(categ);
                System.out.println(dati.url.get(k));
                h.clear();
                h = null;
                h = new HashMap<String, Integer>();

                
            }       



        
        }
        catch(Exception e){e.toString();}
        
            }
        
        }
    


public static int getIndex(String s)
{
    
    int val=0,index=0;
        for ( val=0;val<s.length() && index<3 ;val++)
            if(s.charAt(val)=='/')
                index++;
        
        return val;
}
    

}
