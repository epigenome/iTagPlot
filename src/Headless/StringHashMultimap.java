/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Headless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author SHKim12
 */

public class StringHashMultimap implements Iterable<Entry<String,String>> {
    private HashMap<String,ArrayList<String>> map;
    private long numItems;
    
    public StringHashMultimap() {
        map = new HashMap<>();
        numItems = 0;
    }
    
    @Override
    public Iterator<Entry<String, String>> iterator() {
        return new StringHashMultimapIterator( map.entrySet().iterator() );
    }
    
    public HashMap<String,ArrayList<String>>  getHashMap() {
        return map;
    }
    
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    public void put( String key, String value ) {
        if( map.containsKey( key ) ) {
            map.get( key ).add( value );
        } else {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add( value );
            map.put( key, arrayList );
        }
        ++numItems;
    }
}
