package spielraum;

import spiel.Spieler;

/**
 * Repräsentiert einen einzelnen Eintrag in der Bestenliste.
 */
public class BestenlisteEintrag {
    private final Spieler spieler;
    private int punkte;

    /**
     * Erstellt einen neuen Bestenlisten-Eintrag.
     * @param spieler der Spieler für diesen Eintrag.
     * @param punkte die anfängliche punktzahl des Spielers.
     */
    public BestenlisteEintrag(Spieler spieler, int punkte) {
        this.spieler = spieler;
        this.punkte = punkte;
    }

    /**
     * Gibt den Spieler dieses Eintrags zurück.
     * @return der Spieler, der diesem Eintrag zugeordnet ist.
     */
    public Spieler getSpieler() {
        return spieler;
    }
    /**
     * Gibt den Namen des Spielers zurück.
     * @return der Name des Spielers
     */
    public String getName() {
        return spieler.getName();
    }
    /**
     * Gibt die aktuelle Punktzahl des Spielers zurück.
     * @return die Punktzahl des Spielers
     */
    public int getPunkte() {
        return punkte;
    }
    /**
     * Setzt eine neue Punktzahl für den Spieler.
     * @param punkte die neue Punktzahl
     */
    public void setPunkte(int punkte) {
        this.punkte = punkte;
    }
    /**
     * Gibt eine String-Darstellung dieses Bestenlisten-Eintrags zurück.
     * @return eine formatierte String-Darstellung des Eintrags
     */
    @Override
    public String toString() {
        return getName() + ": " + punkte + " Punkte";
    }
}
