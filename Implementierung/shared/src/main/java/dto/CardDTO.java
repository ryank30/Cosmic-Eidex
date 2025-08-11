package dto;

import java.io.Serializable;

/**
 * A Date Transfer Object (DTO) representing a playing card.
 */
public class CardDTO implements Serializable {
    private final String id;
    /**
     * Constructs a new CardDTO with the specified identifier.
     *
     * @param id the unique identifier of the card
     */
    public CardDTO(String id) {
        this.id = id;
    }
    /**
     * Returns the unique identifier of this card.
     *
     * @return the card ID
     */
    public String getId() {
        return id;
    }
}