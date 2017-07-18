package com.together.raz.together.Enums;

/**
 * Created by Raz on 3/13/2017.
 */
public enum Account {
    CHILD,PSYCHOLOGIST,MANAGER,DEVELOPER;
    @Override
    public String toString(){
        switch(this){
            case CHILD: return "Child";
            case PSYCHOLOGIST: return "Psychologist";
            case MANAGER: return "Manager";
            case DEVELOPER: return "Developer";
            default: return "";
        }
    }
}
