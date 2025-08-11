package spiel;

/**
 * Represents a playing card with a suit, rank, and point value.
 */
public class Karte {
    private Farbe farbe;
    private Rang rang;
    private String id;

    /**
     * Constructs a new card with the specified suit, rank, and value.
     *
     * @param farbe the suit of the card (e.g. HEARTS)
     * @param rang  the rank of the card (e.g. JACK, ACE)
     */
    public Karte(Farbe farbe, Rang rang) {
        this.farbe = farbe;
        this.rang = rang;
        this.id = generateCardId(farbe.name(), rang.name());
    }

    private String generateCardId(String farbe, String rang) {
        return "card-" + farbe.toLowerCase() + "-" + rang.toLowerCase();
    }


    public String getId() {
        return id;
    }
    /**
     * Returns the suit of the card.
     *
     * @return the suit (Farbe)
     */
    public Farbe getFarbe() {
        return farbe;
    }

    /**
     * Returns the rank of the card.
     *
     * @return the rank (Rang)
     */
    public Rang getRang() {
        return rang;
    }

    /**
     * Returns the point value of this card depending on the game mode.
     *
     * @param trumpSuit     the trump suit, or null if there is no trump
     * @param isObenabe     true if the game is in Obenabe (topdown) mode
     * @param isUndenufe    true if the game is in Undenufe (bottomup) mode
     * @return              the point value of this card
     */
    public int getWert(Farbe trumpSuit, boolean isObenabe, boolean isUndenufe) {
        if (isUndenufe) {
            // Undenufe (bottomup) scoring
            return switch (rang) {
                case ACE -> 0;
                case KING -> 4;
                case QUEEN -> 3;
                case JACK -> 2;
                case TEN -> 10;
                case NINE -> 0;
                case EIGHT -> 8;
                case SEVEN -> 0;
                case SIX -> 11;
            };
        }

        if (trumpSuit != null && farbe == trumpSuit) {
            // Trump suit scoring
            return switch (rang) {
                case JACK -> 20;
                case NINE -> 14;
                case ACE -> 11;
                case KING -> 4;
                case QUEEN -> 3;
                case TEN -> 10;
                case EIGHT, SEVEN, SIX -> 0;
            };
        }

        if (isObenabe) {
            // Obenabe (topdown) scoring
            return switch (rang) {
                case ACE -> 11;
                case KING -> 4;
                case QUEEN -> 3;
                case JACK -> 2;
                case TEN -> 10;
                case NINE -> 0;
                case EIGHT -> 8;
                case SEVEN, SIX -> 0;
            };
        }

        // Normal game (non-trump, not obenabe or undenufe)
        return switch (rang) {
            case ACE -> 11;
            case KING -> 4;
            case QUEEN -> 3;
            case JACK -> 2;
            case TEN -> 10;
            case NINE, EIGHT, SEVEN, SIX -> 0;
        };
    }
    @Override
    public String toString() {
        return farbe + " " + rang;
    }

    public boolean isTrumpJack(Farbe trumpSuit) {
        return this.getFarbe() == trumpSuit && this.getRang() == Rang.JACK;
    }

}
