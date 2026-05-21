package com.poker;

import java.util.Arrays;
import java.util.List;

public class HandRanker {
    public static final int RANK_HIGH_CARD = 1;
    public static final int RANK_PAIR = 2;
    public static final int RANK_TWO_PAIR = 3;
    public static final int RANK_THREE_OF_A_KIND = 4;
    public static final int RANK_STRAIGHT = 5;
    public static final int RANK_FLUSH = 6;
    public static final int RANK_FULL_HOUSE = 7;
    public static final int RANK_FOUR_OF_A_KIND = 8;
    public static final int RANK_STRAIGHT_FLUSH = 9;

    public static int evaluateHandStrength(List<Card> hand) {
        if (hand == null || hand.size() != 5) return 0;

        int[] rankDistribution = new int[15];
        int[] suitDistribution = new int[4];

        for (Card card : hand) {
            rankDistribution[card.getRank().numericValue]++;
            suitDistribution[card.getSuit().ordinal()]++;
        }

        boolean looksLikeFlush = Arrays.stream(suitDistribution).anyMatch(count -> count == 5);

        int consecutiveChain = 0;
        int maxChain = 0;
        for (int count : rankDistribution) {
            if (count > 0) {
                consecutiveChain++;
                maxChain = Math.max(maxChain, consecutiveChain);
            } else {
                consecutiveChain = 0;
            }
        }
        boolean looksLikeStraight = (maxChain == 5);

        int totalPairs = 0;
        boolean hasTrips = false;
        boolean hasQuads = false;

        for (int occurrence : rankDistribution) {
            if (occurrence == 2) totalPairs++;
            if (occurrence == 3) hasTrips = true;
            if (occurrence == 4) hasQuads = true;
        }

        if (looksLikeStraight && looksLikeFlush) return RANK_STRAIGHT_FLUSH;
        if (hasQuads) return RANK_FOUR_OF_A_KIND;
        if (hasTrips && totalPairs == 1) return RANK_FULL_HOUSE;
        if (looksLikeFlush) return RANK_FLUSH;
        if (looksLikeStraight) return RANK_STRAIGHT;
        if (hasTrips) return RANK_THREE_OF_A_KIND;
        if (totalPairs == 2) return RANK_TWO_PAIR;
        if (totalPairs == 1) return RANK_PAIR;

        return RANK_HIGH_CARD;
    }

    public static String convertStrengthToString(int strengthScore) {
        switch (strengthScore) {
            case RANK_STRAIGHT_FLUSH: return "Straight Flush";
            case RANK_FOUR_OF_A_KIND: return "Four of a Kind";
            case RANK_FULL_HOUSE: return "Full House";
            case RANK_FLUSH: return "Flush";
            case RANK_STRAIGHT: return "Straight";
            case RANK_THREE_OF_A_KIND: return "Three of a Kind";
            case RANK_TWO_PAIR: return "Two Pair";
            case RANK_PAIR: return "One Pair";
            default: return "High Card";
        }
    }
}