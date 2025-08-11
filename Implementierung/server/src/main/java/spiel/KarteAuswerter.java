package spiel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class KarteAuswerter {

    private static final Map<Pair<Rang, Farbe>, Integer> normalPoints = new HashMap<>();
    private static final Map<Rang , Integer> trumpPoints = new HashMap<>();
    private static final Map<Rang, Integer> obenabePoints = new HashMap<>();
    private static final Map<Rang, Integer> undenufePoints = new HashMap<>();


    static class Pair<A, B> {
        final A first;
        final B second;
        Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Pair)) return false;
            Pair<?, ?> p = (Pair<?, ?>) o;
            return Objects.equals(first, p.first) && Objects.equals(second, p.second);
        }
        @Override public int hashCode() {
            return Objects.hash(first, second);
        }
    }

    static {
        for (Farbe color : Farbe.values()) {
            normalPoints.put(new Pair<>(Rang.SIX, color), 0);
            normalPoints.put(new Pair<>(Rang.SEVEN, color), 0);
            normalPoints.put(new Pair<>(Rang.EIGHT, color), 0);
            normalPoints.put(new Pair<>(Rang.NINE, color), 0);
            normalPoints.put(new Pair<>(Rang.TEN, color), 10);
            normalPoints.put(new Pair<>(Rang.JACK, color), 2);
            normalPoints.put(new Pair<>(Rang.QUEEN, color), 3);
            normalPoints.put(new Pair<>(Rang.KING, color), 4);
            normalPoints.put(new Pair<>(Rang.ACE, color), 11);
        }
        trumpPoints.put(Rang.SIX, 0);
        trumpPoints.put(Rang.SEVEN, 0);
        trumpPoints.put(Rang.EIGHT, 0);
        trumpPoints.put(Rang.NINE, 14);
        trumpPoints.put(Rang.TEN, 10);
        trumpPoints.put(Rang.JACK, 20);
        trumpPoints.put(Rang.QUEEN, 3);
        trumpPoints.put(Rang.KING, 4);
        trumpPoints.put(Rang.ACE, 11);

        obenabePoints.put(Rang.ACE, 11);
        obenabePoints.put(Rang.TEN, 10);
        obenabePoints.put(Rang.KING, 4);
        obenabePoints.put(Rang.QUEEN, 3);
        obenabePoints.put(Rang.JACK, 2);
        obenabePoints.put(Rang.NINE, 0);
        obenabePoints.put(Rang.EIGHT, 0);
        obenabePoints.put(Rang.SEVEN, 0);

        undenufePoints.put(Rang.SIX, 11);
        undenufePoints.put(Rang.SEVEN, 0);
        undenufePoints.put(Rang.EIGHT, 8);
        undenufePoints.put(Rang.NINE, 0);
        undenufePoints.put(Rang.JACK, 2);
        undenufePoints.put(Rang.QUEEN, 3);
        undenufePoints.put(Rang.KING, 4);
        undenufePoints.put(Rang.TEN, 10);
        undenufePoints.put(Rang.ACE, 0);
    }

    public static Integer evalCardPointValue(Karte karte, String mode, Farbe trump) {
        Rang rang = karte.getRang();
        Farbe color = karte.getFarbe();

        switch (mode) {
            case "OBENABE":
                return obenabePoints.getOrDefault(rang, 0);
            case "UNDENUFE":
                return undenufePoints.getOrDefault(rang, 0);
            default:
                if (color == trump) {
                    return trumpPoints.getOrDefault(rang, 0);
                } else {
                    return normalPoints.getOrDefault(new Pair<>(rang, color), 0);
                }
        }
    }
}
