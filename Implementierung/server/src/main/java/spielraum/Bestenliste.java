package spielraum;

import spiel.Spieler;

import java.util.HashMap;
import java.util.Map;
/**
 * Eine Bestenliste zur Verwaltung von Spielerpunkteständen.
 */
public class Bestenliste {
    private final Map<Spieler, BestenlisteEintrag> eintraege = new HashMap<>();

    /**
     * Gibt den Bestenlisten-Eintrag für einen bestimmten Spieler zurück.
     * @param player , dessen Eintrag abgerufen werden soll.
     * @return der BestenlisteEintrag des Spielers, oder null falls kein Eintrag existiert.
     */
    public BestenlisteEintrag getEintrag(Spieler player) {
        return eintraege.get(player);
    }
    /**
     * Setzt oder aktualisiert die Punkte für einen Spieler in der Bestenliste.
     * @param player der Spieler, für den die Punkte gesetzt werden sollen
     * @param punkte die neue Punktzahl des Spielers
     */
    public void setEintrag(Spieler player, int punkte) {
        eintraege.putIfAbsent(player, new BestenlisteEintrag(player, 0));
        BestenlisteEintrag eintrag = eintraege.get(player);
        eintrag.setPunkte(punkte);
    }
}

