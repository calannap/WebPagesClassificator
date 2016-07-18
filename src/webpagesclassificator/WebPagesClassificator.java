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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
       
            List<WebSites> CSlist;
            List<WebSites> TSlist;
            if(!true){

            float total=0.0f;
            int index = 0, min = 1, max = 100;
           LinkExtractor l1 = new LinkExtractor("C://Users/Lee/Desktop/dati.u8");
            Set dati = l1.getLinks();
            Set CS = new Set();
            Set TS = new Set();

            System.out.println(dati.cat.size());
           Random rn = new Random();



            for (int i = 0; i < dati.cat.size(); i++) {
                if ((rn.nextInt(max - min + 1) + min) >= 90) {
                    CS.cat.add(dati.cat.get(i));
                    CS.url.add(dati.url.get(i));
                } else {
                    TS.cat.add(dati.cat.get(i));
                    TS.url.add(dati.url.get(i));
                }
            }

            System.out.println("Nel CS ci sono "+CS.cat.size()+" siti");
            System.out.println("Nel TS ci sono "+TS.cat.size()+" siti");

             CSlist = getTokens(CS,true);
             TSlist = getTokens(TS,false);

            TSlist = parseSites(TSlist);
             saveFile(CSlist,"C:\\Users\\Lee\\Desktop\\CS.ser");
            saveFile(TSlist,"C:\\Users\\Lee\\Desktop\\TS.ser");
        }
        else
        {
            CSlist = loadFile("C:\\Users\\Lee\\Desktop\\CS.ser");
            TSlist = loadFile("C:\\Users\\Lee\\Desktop\\TS.ser");
        }

        System.out.println("Nel CS ci sono "+CSlist.size()+" siti");
        System.out.println("Nel TS ci sono "+TSlist.size()+" categorie");
        for (int i=0;i<TSlist.size();i++)
            System.out.println(TSlist.get(i).cat);
        int tr=0;
        boolean truep=false;
        int corto=0;
        for (int i=0;i<CSlist.size();i++)
        {
            
        if(CSlist.get(i).count.size()>10){
         truep= Classify(CSlist.get(i),TSlist);
         if(truep)
             tr++;
        }
        else
            corto++;
        }
        System.out.println("TP: "+tr+" su "+(CSlist.size()-corto));
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

    public static List<WebSites> getTokens(Set temp, boolean iscs) throws InterruptedException{

        int val1=0,val2=0,count=0;
        int numT=0;
        List<CatCreator> t1 = new ArrayList<CatCreator>();
        String categ = temp.cat.get(0);
        int val = getIndex(temp.cat.get(0));
        categ = categ.substring(0,val);
        List<WebSites> db= new ArrayList<WebSites>();
        for (int k = 0; k < temp.url.size()-1; k++) 
        {
            
      
                t1.add(new CatCreator(temp,iscs,k,k+1));   
                val1=val2;
                numT=0;
            
            val2++;
        

          /*  if ( k==temp.url.size()-1 || (iscs || !categ.equals(temp.cat.get(k+1).substring(0, getIndex(temp.cat.get(k+1)))) )) 
             {
                System.out.println(val1+" "+val2);
                t1.add(new CatCreator(temp,iscs,val1,val2));   
                val1=val2;
                val = getIndex(temp.cat.get(k+1));
                categ = temp.cat.get(k+1).substring(0, val);
             }
            val2++;*/
        }

        int giri = (t1.size() + 30 - 1) / 30;
        for (int x = 0; x < giri; x++) {
           
            for (int i = 30 * x; i < 30 * (x + 1) && i < t1.size(); i++) {
                t1.get(i).start();
               // executor.execute(t1.get(i));
            }
 
            for (int i = 30 * x; i < 30 * (x + 1) && i < t1.size(); i++) {
                t1.get(i).join();
                t1.get(i).interrupt();
            }
            System.gc();
        }
 
        for (int i = 0; i < t1.size(); i++) {
            if (t1.get(i).getValue() != null) {
                db.add(t1.get(i).getValue());
            }
 
        }
 
        System.out.println(db.size());

        return db;
    }
    
    public static boolean Classify(WebSites h, List<WebSites> TS)
    {
        
        HashMap<String, Float> map = new HashMap<String, Float>();
        int occ=0;
        float val=0;
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
                for ( Entry<String, Float> entry: TS.get(i).count.entrySet() )
                {
                    val+= entry.getValue();
                }

                if(occ>0 && TS.get(i).count.get(s) != null)
                {
                    
                   // occ=(int) Math.sqrt(occ);   
                    //val = TS.get(i).count.size();
                    float freqCat = (((TS.get(i).count.get(s))/occ)*1/val);
                    // System.out.println("La stringa "+s+" ha frequenza "+freqCat+" appare in "+TS.get(i).cat+" "+ TS.get(i).count.get(s)+" volte, nella sua cat ci sono "+TS.get(i).count.size()+" parole. In tutte le cat appare "+occ+" volte");
                   
                    map.replace(TS.get(i).cat, map.get(TS.get(i).cat)+(freqCat));
                
                }
                val=0;
            }
            occ=0;
            
        }
        
      
        
        Map.Entry<String, Float> maxEntry1 = null;
        Map.Entry<String, Float> maxEntry2 = null;
        Map.Entry<String, Float> maxEntry3 = null;
      //  System.out.println(map);
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
  
      System.out.println("Predetta: "+maxEntry1.getKey()+" OR "+maxEntry2.getKey()+" OR "+maxEntry3.getKey()+". Actual: "+h.cat);
      System.out.println(h.count+"\n");
      
      
        if(maxEntry1.getKey().equals(h.cat)  )
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
    

    public static List<WebSites> parseSites(List<WebSites> incom)
    {
        for (int i=0;i<incom.size();i++)
            System.out.println(incom.get(i).cat);
        int i=0,j=0;
        List<WebSites> temp = new ArrayList<WebSites>(incom);
        int max=temp.size();
        while (i<max)
        {
            while ( j<max)
            {
               // System.out.println("PRIMA:"+ temp.get(j).cat+" SECONDA: "+ incom.get(i).cat);
            if(i!=j  && temp.get(j).cat.equals(incom.get(i).cat))
                {
                    System.out.println("PRIMA: "+i+" "+incom.get(i).cat +" SECONDA: "+j+" "+ temp.get(j).cat);
                  for ( String s: temp.get(j).count.keySet() )
                  {
                      
                        if (incom.get(i).count.get(s) == null) {
                                incom.get(i).count.put(s, 1.0f);
                                //temp.get(j).count.remove(s);
                            } else {
                                incom.get(i).count.replace(s, incom.get(i).count.get(s)+temp.get(j).count.get(s));
                               // temp.get(j).count.remove(s);
                            }
                  }
                  
                  temp.remove(j);
                  incom.remove(j);
                  max--;
                  if(i>1)
                  i--;
                }
                 j++;   
            }
            j=0;
            i++;
        }
        for (int p=0;p<incom.size();p++)
        {
            if(incom.get(p).count.isEmpty()){
                System.out.println("vuoto");
                incom.remove(p);
            }
        }
        return incom;
    }
}

