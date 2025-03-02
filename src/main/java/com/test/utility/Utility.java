package com.test.utility;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class Utility {
    public static int getProcessId() {
        Random random = new Random();
        // Generates a random number between 10000 (inclusive) and 100000 (exclusive)
        return random.nextInt(90000) + 10000;
    }
}
