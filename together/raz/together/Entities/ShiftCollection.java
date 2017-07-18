package com.together.raz.together.Entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raz on 4/11/2017.
 */
public class ShiftCollection {
    private List<Shift> shifts;

    public ShiftCollection() {
        shifts = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ShiftCollection{" +
                "shifts=" + shifts.toString() +
                '}';
    }

    public ShiftCollection add(Shift shift) {
        //add if not exists:
        Boolean exists = false;
        for(Shift s: shifts){
            if(s.getId().equals(shift.getId())){
                exists = true;
            }
        }
        if(!exists){
            shifts.add(shift);
        }
        return this;
    }

    public List<Shift> getShifts() {
        return shifts;
    }
}