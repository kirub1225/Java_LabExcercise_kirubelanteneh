package com.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckEngine {
    private final List<Card> cardPool = new ArrayList<>();

    public DeckEngine() {
        populateAndShuffle();
    }

    public void populateAndShuffle() {
        cardPool.clear();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cardPool.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(cardPool);
    }

    public Card drawFromTop() {
        if (cardPool.isEmpty()) {
            populateAndShuffle();
        }
        return cardPool.remove(cardPool.size() - 1);
    }
}