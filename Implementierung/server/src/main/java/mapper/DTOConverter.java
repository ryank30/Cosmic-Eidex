package mapper;

import account.Account;
import dto.*;
import server.impl.RaumverwaltungImpl;
import spiel.Spiel;
import spiel.Stich;
import spiel.Spieler;
import spiel.Karte;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class for converting domain objects to Data Transfer Objects (DTOs).
 * All conversion methods include null-safety checks and handle edge cases gracefully.
 */
public class DTOConverter {
    /**
     * Converts a Stich (trick) domain object to a StichDTO.
     * @param stich the Stich object to convert, may be null.
     * @return StichDTO containing the trick data, never null.
     */
    public static StichDTO toDTO(Stich stich) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (stich == null || stich.getAllCards() == null) return new StichDTO(map);

        for (Map.Entry<Spieler, Karte> entry : stich.getAllCards()) {
            if (entry == null) continue;

            Spieler spieler = entry.getKey();
            Karte karte = entry.getValue();

            String playerName = (spieler != null && spieler.getName() != null) ? spieler.getName() : "Unknown";
            String cardCode = (karte != null && karte.getId() != null) ? karte.getId() : "null";
            map.put(playerName, cardCode);
        }
        return new StichDTO(map);
    }

    /**
     * Converts a Spieler (player) domain object to a PlayerDTO.
     * @param spieler the spieler object to convert, may be null.
     * @return PlayerDTO containing the player data, or null id input is null.
     */
    public static PlayerDTO toDTO(Spieler spieler) {
        if (spieler == null) return null;

        List<CardDTO> handDTOs = (spieler.getHandkarten() == null)
                ? List.of()
                : spieler.getHandkarten().stream()
                .filter(Objects::nonNull)
                .map(DTOConverter::toDTO)
                .toList();

        List<CardDTO> validMoveDTOs = (spieler.getValidMoves() == null)
                ? List.of()
                : spieler.getValidMoves().stream()
                .filter(Objects::nonNull)
                .map(DTOConverter::toDTO)
                .toList();

        return new PlayerDTO(
                spieler.getName() != null ? spieler.getName() : "Unknown",
                handDTOs,
                validMoveDTOs,
                spieler.getPunkte(),
                spieler.getWinPoint()
        );
    }
    /**
     * Converts a Karte (card) domain object to a CardDTO.
     * @param karte the Karte object to convert, may be null
     * @return CardDTO containing the card data, uses "null" as ID if input is null
     */
    public static CardDTO toDTO(Karte karte) {
        if (karte == null) return new CardDTO("null");
        return new CardDTO(karte.getId());
    }
    /**
     * Converts a Spiel (game) domain object to a GameStateDTO.
     * @param game the Spiel object to convert, may be null
     * @return GameStateDTO containing the complete game state, or null if input is null
     */
    public static GameStateDTO toDTO(Spiel game) {
        if (game == null || game.getSpielerListe() == null) return null;

        List<PlayerDTO> playerDTOs = game.getSpielerListe()
                .stream()
                .filter(s -> s != null)
                .map(DTOConverter::toDTO)
                .toList();

        return new GameStateDTO(
                playerDTOs,
                game.getAktuellerSpielerIndex(),
                game.isAktiv()
        );
    }

    /**
     * Converts an Account domain object to a LeaderboardEntryDTO.
     * @param account the Account object to convert, should not be null
     * @return LeaderboardEntryDTO containing username and win count
     */
    public static LeaderboardEntryDTO toDTo (Account account) {
        return new LeaderboardEntryDTO(account.get_username(), account.get_wins());
    }

}
