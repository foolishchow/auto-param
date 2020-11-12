package me.foolishchow.anrdoid.processor.utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public class ElementUtils {

    public static TypeElement getClassElement(Element element){
        return (TypeElement) element.getEnclosingElement();
    }
}
