package com.example.asset.utils;

import java.util.HashMap;
import java.util.Map;

public class RangeUtils {
    public static final Map<String, Integer> rangeMap = new HashMap<>() {{
        put("1S", 7);
        put("1A", 365);
        put("5A", 1825);
        put("Max", 36135);
    }};
}
