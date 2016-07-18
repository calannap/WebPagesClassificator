/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webpagesclassificator;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Lee
 */
public class WebSites implements Serializable
    {
         HashMap<String, Float> count = new HashMap<String, Float>();
         String cat=null;
         String html = null;
         
    public WebSites(HashMap<String, Float> a, String b, String c)
    {
        count = a;
        cat = b;
        html = c ;
    }



    }
