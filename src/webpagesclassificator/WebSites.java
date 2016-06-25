/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webpagesclassificator;

import java.util.HashMap;

/**
 *
 * @author Lee
 */
public class WebSites 
    {
         HashMap<String, Float> count = new HashMap<String, Float>();
         String cat=null;
         
    public WebSites(HashMap<String, Float> a, String b)
    {
        count = a;
        cat = b;
    }



    }
