package com.atypon.nosql;

import com.google.gson.JsonArray;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sandbox {
    public static void main(String[] args) {
        String input = "khalid(test,  testAgain, test2)";
        Matcher matcher = Pattern.compile("^([^\\W\\d]+)(\\(((\\w+)(, *\\w+)*)\\))?$").matcher(input);
        System.out.println(matcher.groupCount());

        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println(matcher.group(i));
            }
        }
    }
}
