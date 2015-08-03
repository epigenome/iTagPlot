/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Headless;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author SHKim12
 */

public class StringHashMultimapIterator implements Iterator<Entry<String,String>> {
    Iterator<Entry<String,ArrayList<String>>> macroIterator; // iterator over list
    Iterator<String>                          microIterator; // iterator in   list
    String currentKey;
    
    StringHashMultimapIterator( Iterator<Entry<String,ArrayList<String>>> macroItr ) {
        macroIterator = macroItr;
        microIterator = null;
        currentKey    = null;
    }
    
    @Override
    public boolean hasNext() {
        return ( microIterator != null && microIterator.hasNext() ) || macroIterator.hasNext();
    }

    @Override
    public Entry<String, String> next() {
        if( microIterator == null || !microIterator.hasNext() ) {
            Entry<String,ArrayList<String>> entry = macroIterator.next();
            currentKey    = entry.getKey();
            microIterator = entry.getValue().iterator();
        }
        String value = microIterator.next();
        return new SimpleImmutableEntry<String,String>( currentKey, value ) {};
    }
}