package com.example.lab5;

public final class TekstoAnalize {

    // Lietuviškos balsės (mažosios). Didžiosios tikrinamos per toLowerCase.
    private static final String BALSES = "aąeęėiįyouųū";

    private TekstoAnalize() { } // klasės objektų kurti nereikia

    public static int skaiciuotiA(String tekstas) {
        int kiekis = 0;
        for (char c : tekstas.toCharArray()) {
            if (c == 'a' || c == 'A') kiekis++;
        }
        return kiekis;
    }

    public static int skaiciuotiBalses(String tekstas) {
        int kiekis = 0;
        for (char c : tekstas.toCharArray()) {
            if (BALSES.indexOf(Character.toLowerCase(c)) >= 0) kiekis++;
        }
        return kiekis;
    }

    public static int skaiciuotiDidziasias(String tekstas) {
        int kiekis = 0;
        for (char c : tekstas.toCharArray()) {
            if (Character.isUpperCase(c)) kiekis++;
        }
        return kiekis;
    }

    public static int skaiciuotiMazasias(String tekstas) {
        int kiekis = 0;
        for (char c : tekstas.toCharArray()) {
            if (Character.isLowerCase(c)) kiekis++;
        }
        return kiekis;
    }
}