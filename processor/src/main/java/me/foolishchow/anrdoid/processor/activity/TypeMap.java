package me.foolishchow.anrdoid.processor.activity;

import java.util.ArrayList;


import javax.lang.model.element.Element;

import me.foolishchow.anrdoid.processor.HelperSavedValues;

public class TypeMap {

    private String name;

    public String getName() {
        return name;
    }

    private ArrayList<Element> mElements;

    public ArrayList<Element> getElements() {
        return mElements;
    }

    public TypeMap(String name) {
        this.name = name;
    }


    public void addElement(Element element){
        if(mElements == null){
            mElements = new ArrayList<>();
        }
        mElements.add(element);
    }
}
