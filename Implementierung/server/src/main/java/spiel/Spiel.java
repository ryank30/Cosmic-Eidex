package spiel;

import server.ServerContext;

import java.io.Serializable;
import java.util.*;

/**
 * Represents an active card game session within a specific room.
 * Manages game state, turn order, played tricks, and player actions.
 */
public class Spiel implements Serializable {
    private final String spielraumName;
    private List<Spieler> spielerListe;
    private final Stich stich;
    private final Map<Spieler, List<Karte>> gespielteStiche;
    private boolean aktiv;
    private int aktuellerSpielerIndex;
    private Farbe trumpSuit;
    private boolean isObenabe;
    private boolean isUndenufe;
    private Karte lastCard;
    private String gameMode;
    private Spieler letzterStichGewinner;
    private int letzterStichPunkte;
    private final Map<Spieler, List<Stich>> sticheVonGewinnern;
    Spieler winner;
    private List<Karte> currentValidMoves = new ArrayList<>();
    final Map<Spieler, Karte> pushedCards;
    Boolean isPushCardTrick;
    private Boolean playerHas7WinPoints;


    /**
     * Constructs a new spiel instance with a given room name and player list.
     *
     * @param spielraumName the name of the game room
     * @param spielerListe the list of participating players
     */
    public Spiel(String spielraumName, List<Spieler> spielerListe) {
        this.spielraumName = spielraumName;
        this.spielerListe = spielerListe;
        this.stich = new Stich();
        this.gespielteStiche = new HashMap<>();
        this.aktiv = false;
        this.aktuellerSpielerIndex = 0;
        this.sticheVonGewinnern = new HashMap<>();
        this.pushedCards = new HashMap<>();
        this.isPushCardTrick = true;
        this.playerHas7WinPoints = false;
        for (Spieler s : spielerListe) {
            gespielteStiche.put(s, new ArrayList<>());
            sticheVonGewinnern.put(s, new  ArrayList<>());
        }
    }
    /**
     * creates a dto from the game state data
     */


    public static Karte getCardById(List<Karte> hand, String id) {
        return hand.stream()
                .filter(card -> card.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Karte mit " + id + " nicht in Hand"));
    }
    /**
     * Returns the name of the game room.
     *
     * @return the room name
     */
    public String getSpielraumName() {
        return spielraumName;
    }

    /**
     * Returns the list of players in the game.
     *
     * @return list of Spieler
     */
    public List<Spieler> getSpielerListe() {
        return spielerListe;
    }

    public void setSpielerListe(List<Spieler> spielerListe) {
        this.spielerListe = spielerListe;
    }
    /**
     * Checks whether the game is currently active.
     *
     * @return true if active, false otherwise
     */
    public boolean isAktiv() {
        return aktiv;
    }

    /**
     * Sets the active state of the game.
     *
     * @param aktiv true to activate, false to deactivate
     */
    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    /**
     * Returns the current trick (played cards).
     *
     * @return list of Karten in the current trick
     */
    public Stich getStich() {
        return stich;
    }

    /**
     * Returns the player whose turn it is.
     *
     * @return the current Spieler
     */
    public Spieler getAktuellerSpieler() {
        return spielerListe.get(aktuellerSpielerIndex);
    }

    /**
     * Advances to the next player's turn.
     */
    public void naechsterSpieler() {
        aktuellerSpielerIndex = (aktuellerSpielerIndex + 1) % spielerListe.size();
    }

    public Spieler getLetzterStichGewinner() {
        return letzterStichGewinner;
    }

    public int getLetzterStichPunkte() {
        return letzterStichPunkte;
    }
    /**
     * Adds a played card to the trick and stores it under the player.
     *
     * @param spieler the player who played the card
     * @param karte the card played
     */
    public void karteInStich(Spieler spieler, Karte karte) {
        stich.addCard(spieler, karte);
        gespielteStiche.get(spieler).add(karte);
    }

    /**
     * Returns all played tricks grouped by player.
     *
     * @return map of Spieler to list of Karten
     */
    public Map<Spieler, List<Karte>> getAlleStiche() {
        return gespielteStiche;
    }

    /**
     * Clears the current trick.
     */
    public void resetStich() {
        stich.clear();
    }

    /**
     * Checks if the current trick is the last one (i.e., players have no cards left).
     *
     * @return true if it's the final trick, false otherwise
     */
    public boolean istLetzterStich() {
        return spielerListe.get(0).getHandkarten().isEmpty();
    }

    /**
     * Returns the index of the current player in the list.
     *
     * @return index of current player
     */
    public int getAktuellerSpielerIndex() {
        return aktuellerSpielerIndex;
    }

    /**
     * Sets the current player index manually.
     *
     * @param index the new index (modulo player list size)
     */
    public void setAktuellerSpielerIndex(int index) {
        aktuellerSpielerIndex = index % spielerListe.size();
    }

    /**
     * Initializes the game with cards dealt from the given deck.
     * Determines trump suit or game mode (Obenabe / Undenufe).
     *
     * @param deck the deck to use for card distribution and trump determination
     */
    public void initializeGame(Deck deck) {
        List<List<Karte>> haende = deck.deal();
        for (int i = 0; i < spielerListe.size(); i++) {
            spielerListe.get(i).getHandkarten().clear();
            for (Karte k : haende.get(i)) {
                spielerListe.get(i).addKarte(k);
            }
        }

        lastCard = deck.revealLastCard();
        String mode = Deck.getGameMode(lastCard);

        isObenabe = mode.equals("OBENABE");
        isUndenufe = mode.equals("UNDENUFE");
        trumpSuit = (!isObenabe && !isUndenufe) ? Farbe.valueOf(mode) : null;

        aktiv = true;
        aktuellerSpielerIndex = 0;
    }

    /**
     * Sets the trump suit for this game.
     *
     * @param trumpSuit the trump suit to be used (null if no trump)
     */
    public void setTrumpSuit(Farbe trumpSuit) {
        this.trumpSuit = trumpSuit;
    }

    /**
     * Returns the trump suit if available, otherwise null.
     *
     * @return trump suit (Farbe) or null
     */
    public Farbe getTrumpSuit() {
        return trumpSuit;
    }

    /**
     * Returns true if the game is in Obenabe (top-down) mode.
     *
     * @return true if Obenabe
     */
    public boolean isObenabe() {
        return isObenabe;
    }

    /**
     * Returns true if the game is in Undenufe (bottom-up) mode.
     *
     * @return true if Undenufe
     */
    public boolean isUndenufe() {
        return isUndenufe;
    }

    /**
     * Returns the last card dealt face-up that determined the game mode.
     *
     * @return the last Karte
     */
    public Karte getLastCard() {
        return lastCard;
    }

    /**
     * Checks whether the given card is a valid move for the player based on the lead suit and game mode.
     *
     * @param spieler the player attempting to play the card
     * @param karte   the card being played
     * @param leadCard the card that was led in the trick (null if first)
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(Spieler spieler, Karte karte, Karte leadCard) {
        List<Karte> hand = spieler.getHandkarten();

        if (leadCard == null) return true;

        Farbe leadSuit = leadCard.getFarbe();
        boolean isTrumpCard = karte.getFarbe() == trumpSuit;
        boolean isFollowingSuit = karte.getFarbe() == leadSuit;

        //follow suit or play trump
        boolean hasLeadSuit = hand.stream().anyMatch(k -> k.getFarbe() == leadSuit);
        if (!isFollowingSuit && !isTrumpCard && hasLeadSuit) {
            return false;
        }

        //trump jack special rule
        if (leadSuit == trumpSuit && !isTrumpCard) {
            boolean hasTrumpJack = hand.stream().anyMatch(k -> k.isTrumpJack(trumpSuit));
            if (hasTrumpJack) {
                return true;
            }
        }

        //no "untertrumpfen"
        if (isTrumpCard && trumpSuit != null) {
            Optional<Karte> highestTrump = stich.getCards().stream()
                    .filter(c -> c.getFarbe() == trumpSuit)
                    .max(Comparator.comparingInt(c -> c.getWert(trumpSuit, isObenabe, isUndenufe)));

            if (highestTrump.isPresent()) {
                int currentWert = karte.getWert(trumpSuit, isObenabe, isUndenufe);
                int highestWert = highestTrump.get().getWert(trumpSuit, isObenabe, isUndenufe);

                if (currentWert < highestWert) {
                    boolean hasNonTrump = hand.stream().noneMatch(k -> k.getFarbe() == trumpSuit || k.getFarbe() == leadSuit);
                    return !hasNonTrump;
                }
            }
        }

        return true;
    }



    /**
     * Determines the winner of the current trick based on game rules.
     * Takes trump suit, Obenabe/Undenufe mode, and play order into account.
     *
     * @param trickCards the list of cards played in the trick, in play order
     * @param playerOrder the corresponding list of players who played the cards
     * @return the player who won the trick
     */
    public Spieler determineTrickWinner(List<Karte> trickCards, List<Spieler> playerOrder) {
        Karte leadCard = trickCards.get(0);
        Farbe leadSuit = leadCard.getFarbe();

        int bestIndex = 0;
        Karte bestCard = leadCard;

        for (int i = 1; i < trickCards.size(); i++) {
            Karte currentCard = trickCards.get(i);

            boolean currentIsTrump = trumpSuit != null && currentCard.getFarbe() == trumpSuit;
            boolean bestIsTrump = trumpSuit != null && bestCard.getFarbe() == trumpSuit;

            int currentValue = currentCard.getWert(trumpSuit, isObenabe, isUndenufe);
            int bestValue = bestCard.getWert(trumpSuit, isObenabe, isUndenufe);

            if (currentIsTrump && !bestIsTrump) {
                bestCard = currentCard;
                bestIndex = i;
            } else if (currentCard.getFarbe() == bestCard.getFarbe()) {
                if ((isObenabe || (!isObenabe && !isUndenufe)) && currentValue > bestValue) {
                    bestCard = currentCard;
                    bestIndex = i;
                }
                if (isUndenufe && currentValue < bestValue) {
                    bestCard = currentCard;
                    bestIndex = i;
                }
            }
        }

        return playerOrder.get(bestIndex);
    }

    public void startGame(Deck deck) {
        this.aktiv = true;
        this.lastCard = lastCard;

        List<List<Karte>> hands = deck.deal();
        for (int i = 0; i < spielerListe.size(); i++) {
            spielerListe.get(i).getHandkarten().addAll(hands.get(i));
        }

        Karte lastCard = deck.revealLastCard();
        this.gameMode = Deck.getGameMode(lastCard);
        this.trumpSuit = (gameMode.equals("OBENABE") || gameMode.equals("UNDENUFE")) ? null : Farbe.valueOf(gameMode);
        this.lastCard = lastCard;
    }


    public void playCard(Spieler spieler, Karte karte) {
        if (stich.getCardCount() == 3){
            resetStich();
        }
        if (pushedCards.size() == 3) {
            isPushCardTrick = false;
        }
        if (isPushCardTrick){
            pushedCards.put(spieler, karte);
            spieler.removeKarte(karte);
            naechsterSpieler();
            updateValidMoves(getAktuellerSpieler());
            return;
        }

        // Check turn
        if (!getAktuellerSpieler().equals(spieler)) {
            throw new IllegalStateException("It's not " + spieler.getName() + "'s turn.");
        }

        // Check card exists
        if (!spieler.getHandkarten().contains(karte)) {
            throw new IllegalArgumentException("Card not found in player's hand.");
        }

        Karte leadCard = stich.getFirstCard();
        if (!isValidMove(spieler, karte, leadCard)) {
            throw new IllegalArgumentException("Invalid move according to the rules.");
        }

        // Remove and register card
        spieler.removeKarte(karte);
        stich.addCard(spieler, karte);
        gespielteStiche.get(spieler).add(karte);

        // If 3 cards played -> evaluate trick
        if (stich.getCardCount() == 3) {
            Stich tempStich = new Stich();
            for (int i = 0; i < spielerListe.size(); i++) {
                Spieler s = spielerListe.get((aktuellerSpielerIndex + i) % spielerListe.size());
                Karte k = gespielteStiche.get(s).get(gespielteStiche.get(s).size() - 1);
                tempStich.addCard(s, k);
            }

            boolean isUndenufe = "UNDENUFE".equalsIgnoreCase(gameMode);
            boolean isObenabe = "OBENABE".equalsIgnoreCase(gameMode);

            Spieler winner = tempStich.getWinner(trumpSuit, isUndenufe, isObenabe);
            int points = tempStich.getTotalPoints(gameMode, trumpSuit);
            if (alleHandkartenLeer()) {
                winner.addPunkte(points + 5);
            } else {
                winner.addPunkte(points);
            }
            letzterStichGewinner = winner;
            letzterStichPunkte = points;

            for (Spieler s : spielerListe) {
                System.out.println("  - " + s.getName() + ": " + s.getPunkte() + " Punkte");
            }

            sticheVonGewinnern.get(winner).add(tempStich);

            setAktuellerSpielerIndex(spielerListe.indexOf(winner));
            updateValidMoves(getAktuellerSpieler());
            //resetStich();
        } else {
            naechsterSpieler();
            updateValidMoves(getAktuellerSpieler());
        }

        if (alleHandkartenLeer()) {
            addPushedCardValues();
            ditributeWinpoints();
            eval7WinPoints();
            if (playerHas7WinPoints) {
                setWinner();
                aktiv = false;
            } else {
                startGame();
            }
        }
    }

    public boolean alleHandkartenLeer() {
        for (Spieler spieler : spielerListe) {
            if (!spieler.getHandkarten().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void ditributeWinpoints() {

        for (Spieler spieler : spielerListe) {
            if (sticheVonGewinnern.get(spieler).size() == 11) {
                System.out.println("Match, Adding 2 winpoints to: " + spieler.getName());
                spieler.setWinPoint(spieler.getWinPoint() + 2);
                return;
            }
        }

        for (Spieler spieler : spielerListe) {
            if (spieler.getPunkte() >= 100) {
                System.out.println("Player " + spieler.getName() + " has 100+ points, no winpoints for them.");

                List<Spieler> others = spielerListe.stream()
                        .filter(p -> !p.equals(spieler))
                        .toList();

                boolean bothOthersAt6 = others.get(0).getWinPoint() == 6 && others.get(1).getWinPoint() == 6;

                if (!bothOthersAt6) {
                    for (Spieler other : others) {
                        other.setWinPoint(other.getWinPoint() + 1);
                        System.out.println("Added 1 winpoint to: " + other.getName());
                    }
                } else {
                    if (others.get(0).getPunkte() > others.get(1).getPunkte()) {
                        others.get(1).setWinPoint(others.get(1).getWinPoint() - 1);
                        System.out.println("removed one winpoint from: " + others.get(1).getName());
                    } else  if  (others.get(0).getPunkte() < others.get(1).getPunkte()) {
                        others.get(0).setWinPoint(others.get(0).getWinPoint() - 1);
                        System.out.println("removed one winpoint from: " + others.get(2).getName());
                    }
                }
                return;
            }
        }
        if (spielerListe.get(0).getPunkte() == spielerListe.get(1).getPunkte()) {
            spielerListe.get(2).setWinPoint(spielerListe.get(2).getWinPoint() + 2);
            System.out.println("added two winpoints to: " + spielerListe.get(2).getName());
            return;
        }
        if (spielerListe.get(0).getPunkte() == spielerListe.get(2).getPunkte()) {
            spielerListe.get(1).setWinPoint(spielerListe.get(1).getWinPoint() + 2);
            System.out.println("added two winpoints to: " + spielerListe.get(1).getName());
            return;
        }
        if (spielerListe.get(1).getPunkte() == spielerListe.get(2).getPunkte()) {
            spielerListe.get(0).setWinPoint(spielerListe.get(0).getWinPoint() + 2);
            System.out.println("added two winpoints to: " + spielerListe.get(0).getName());
            return;
        }
        List<Integer> pointsPlayers = new ArrayList<>();
        for (Spieler spieler : spielerListe) {
            pointsPlayers.add(spieler.getPunkte());
        }
        int maxVal = Collections.max(pointsPlayers);
        int maxIndex = pointsPlayers.indexOf(maxVal);
        int minVal = Collections.min(pointsPlayers);
        int minIndex = pointsPlayers.indexOf(minVal);
        if (spielerListe.get(maxIndex).getWinPoint() == 6 && spielerListe.get(minIndex).getWinPoint() == 6) {
            if (spielerListe.get(maxIndex).getPunkte() > spielerListe.get(minIndex).getPunkte()) {
                spielerListe.get(minIndex).setWinPoint(spielerListe.get(minIndex).getWinPoint() - 1);
                System.out.println("removed one winpoint from: " + spielerListe.get(minIndex).getName());
            } else {
                spielerListe.get(maxIndex).setWinPoint(spielerListe.get(maxIndex).getWinPoint() - 1);
                System.out.println("removed one winpoint from: " + spielerListe.get(maxIndex).getName());
            }
            return;
        }
        spielerListe.get(maxIndex).setWinPoint(spielerListe.get(maxIndex).getWinPoint() + 1);
        System.out.println("Added one winpoints to: " + spielerListe.get(maxIndex).getName());
        spielerListe.get(minIndex).setWinPoint(spielerListe.get(minIndex).getWinPoint() + 1);
        System.out.println("Added one winpoints to: " + spielerListe.get(minIndex).getName());
    }

    /**
     * Starts the game by shuffling a deck and dealing cards to players. is also used to start the next game round.
     * Also sets the trump suit and game mode from the last card.
     */
    public void startGame() {
        Deck deck = new Deck();
        List<List<Karte>> hands = deck.deal();

        for (int i = 0; i < spielerListe.size(); i++) {
            spielerListe.get(i).getHandkarten().clear();
            spielerListe.get(i).getHandkarten().addAll(hands.get(i));
            //restart game logic addition
            gespielteStiche.get(spielerListe.get(i)).clear();
            spielerListe.get(i).addPunkte(-(spielerListe.get(i).getPunkte()));
        }

        Karte lastCard = deck.revealLastCard();
        this.gameMode = Deck.getGameMode(lastCard);

        if (!gameMode.equals("OBENABE") && !gameMode.equals("UNDENUFE")) {
            this.trumpSuit = Farbe.valueOf(gameMode);
        } else {
            this.trumpSuit = null;
        }

        this.aktiv = true;
        this.aktuellerSpielerIndex = 0;
        isPushCardTrick = true;
        System.out.println(getAktuellerSpieler().getName());
        updateValidMoves(getAktuellerSpieler());
    }

    /**
     * Evaluates the current trick, assigns points, and sets up the next trick.
     * Should be called only after a complete trick.
     */
    public void evaluateCurrentTrick() {
        Stich tempStich = new Stich();
        for (Map.Entry<Spieler, Karte> entry : stich.getAllCards()) {
            tempStich.addCard(entry.getKey(), entry.getValue());
        }

        boolean isObenabe = "OBENABE".equals(gameMode);
        boolean isUndenufe = "UNDENUFE".equals(gameMode);

        Spieler winner = tempStich.getWinner(trumpSuit, isUndenufe, isObenabe);
        int points = tempStich.getTotalPoints(gameMode, trumpSuit);
        winner.addPunkte(points);

        setAktuellerSpielerIndex(spielerListe.indexOf(winner));
        stich.clear();
    }

    public String  getMode () {
        return gameMode;
    }

    public String getTrumpSuitString () {
        return trumpSuit.name();
    }

    public void playBotMove() throws InterruptedException {
        Thread.sleep(500);
        Bot.playTurn(this);
    }
    public Spieler setWinner() {
        List<Integer> pointsPlayers = new ArrayList<>();
        for (Spieler spieler : spielerListe) {
            pointsPlayers.add(spieler.getWinPoint());
        }
        int maxVal = Collections.max(pointsPlayers);
        int maxIndex = pointsPlayers.indexOf(maxVal);
        this.winner = spielerListe.get(maxIndex);
        System.out.println("winner set to" + winner);
        ServerContext.getServerContext().getAccount(winner.getName()).add_wins();
        return winner;
    }

    public void updateValidMoves(Spieler player) {
        Karte leadCard = stich.getFirstCard();
        List<Karte> hand = player.getHandkarten();
        List<Karte> validMoves = new ArrayList<>();
        if (stich.getCardCount()==3) {
            validMoves.addAll(hand);
        } else {
            for (Karte card : hand) {
                if (isValidMove(player, card, leadCard)) {
                    validMoves.add(card);
                }
            }
        }

        currentValidMoves = validMoves;

        player.setValidMoves(validMoves);
    }

    public List<Karte> getCurrentValidMoves() {
        return currentValidMoves;
    }

    void eval7WinPoints(){
       for (Spieler spieler : spielerListe){
           if (spieler.getWinPoint() == 7){
               playerHas7WinPoints = true;
               return;
           }
       }
    }

    private void addPushedCardValues(){
        for (Spieler spieler : spielerListe) {
            System.out.println("Adding pushed card to " + spieler.getName() +" with Value " + KarteAuswerter.evalCardPointValue(pushedCards.get(spieler), getMode(), getTrumpSuit()));
            spieler.addPunkte(KarteAuswerter.evalCardPointValue(pushedCards.get(spieler), getMode(), getTrumpSuit()));
        }
    }
}
