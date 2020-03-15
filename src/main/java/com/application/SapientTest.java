package com.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SapientTest {
    public static void main(String[] args) {
        // To get all the zeros towards the end of the list keeping the other sequence as is is

        List<Integer> dataList = new ArrayList<>(Arrays.asList(1, 9, 8, 4, 0, 0, 2, 7, 0, 6, 0));
        List<Integer> zeroIntegerList = dataList.stream().filter(u -> u == 0).collect(Collectors.toList());
        dataList.removeIf(u -> zeroIntegerList.contains(u));
        dataList.addAll(zeroIntegerList);
        System.out.println(dataList);

        // ["abc","adc","xyz","aef","jif"] --> "abc#adc#aef"
        List<String> list = new ArrayList<>(Arrays.asList("abc","adc","xyz","aef","jif"));
        String finalString = list.stream().filter(u -> u.startsWith("a")).collect(Collectors.joining("#"));
        System.out.println(finalString);

    }


}
