package com.poker;

public class Card {
    public enum Suit {
        HEARTS("♥", "red"),
        DIAMONDS("♦", "red"),
        CLUBS("♣", "green"),
        SPADES("♠", "blue");

        public final String symbol;
        public final String colorStyle;
        Suit(String symbol, String colorStyle) {
            this.symbol = symbol;
            this.colorStyle = colorStyle;
        }
    }

    public enum Rank {
        TWO(2, "2"), THREE(3, "3"), FOUR(4, "4"), FIVE(5, "5"),
        SIX(6, "6"), SEVEN(7, "7"), EIGHT(8, "8"), NINE(9, "9"),
        TEN(10, "10"), JACK(11, "J"), QUEEN(12, "Q"),
        KING(13, "K"), ACE(14, "A");

        public final int numericValue;
        public final String displayLabel;
        Rank(int numericValue, String displayLabel) {
            this.numericValue = numericValue;
            this.displayLabel = displayLabel;
        }
    }

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }

    @Override
    public String toString() {
        return rank.displayLabel + suit.symbol;
    }
}