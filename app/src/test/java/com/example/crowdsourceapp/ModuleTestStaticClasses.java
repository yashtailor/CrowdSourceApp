package com.example.crowdsourceapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModuleTestStaticClasses {

    @Test
    public void testUser(){
        String fName = "Aditya";
        String lName = "Ambekar";
        String gender = "Male";
        String dob = "22/10/2000";
        String aadharNum = "333344445555";
        String hasSubmitted = "false";
        String isVerified = "true";
        String message = "";
        String userId = "";
        User voter = new User(fName,lName,gender,dob,aadharNum,hasSubmitted,isVerified,message,userId);
        assertEquals(fName,voter.getfName());
        assertEquals(lName,voter.getlName());
        assertEquals(gender,voter.getGender());
        assertEquals(aadharNum,voter.getAadharNum());
        assertEquals(hasSubmitted,voter.getHasSubmitted());
        assertEquals(isVerified,voter.getIsVerified());
        assertEquals(message,voter.getMessage());
        assertEquals(userId,voter.getUserId());
    }

}
