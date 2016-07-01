/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webpagesclassificator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lee
 */
public class LinkExtractor {
 
    String path="";
    public LinkExtractor(String s)
    {
        path=s;
    }
    
    public Set getLinks() 
    {
    
        String temp="0",result=null;
    Set CS = new Set();
    Set finalCS = new Set();
    int j=0;
    BufferedReader br=null;
    FileReader in=null;

        try {
                in = new FileReader(path);
                br = new BufferedReader(in);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LinkExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }


    while (temp!=null) {
        

                
            try {
                temp = br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(LinkExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }

        
        if(temp!=null &&temp.indexOf("  <ExternalPage about=\"")>=0)
        {
            result = temp.substring(temp.indexOf("<ExternalPage about=\"") + 21, temp.indexOf("\">"));
            CS.url.add(result);
            while(temp.indexOf("    <topic>")<0)
                {   
                try {
                    temp = br.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(LinkExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
            if(temp.indexOf("    <topic>")>=0)
                {
                result = temp.substring(temp.indexOf("<topic>") + 7, temp.indexOf( "</topic>"));
                CS.cat.add(result);
                }

        }
        
    }
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(LinkExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    

   

   for( int i=0; i<CS.cat.size();i=i+5000)
   {
       

    String s = CS.cat.get(i);
    int counter = 0;
    /*for( int q=0; q<s.length(); q++ ) {
        if( s.charAt(q) == '/' ) {
            counter++;
        } 
    }
       if(counter<=3){*/
        finalCS.cat.add(CS.cat.get(i));
        finalCS.url.add(CS.url.get(i));
       //}
   }
   

        return finalCS;
    }
}
