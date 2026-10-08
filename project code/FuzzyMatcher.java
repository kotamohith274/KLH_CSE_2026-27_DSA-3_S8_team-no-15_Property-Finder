package com.example.propertyfinder.algorithms;
public class FuzzyMatcher {

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase().trim();
        s2 = s2.toLowerCase().trim();
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j - 1], // substitution
                        Math.min(dp[i - 1][j], // deletion
                                 dp[i][j - 1]) // insertion
                    );
                }
            }
        }
        return dp[m][n];
    }

    public static boolean isFuzzyMatch(String target, String query, int maxDistance) {
        if (query == null || query.isBlank()) return true;
        if (target == null) return false;

        // Exact match check
        if (target.equalsIgnoreCase(query)) return true;

        // Whole string edit distance check
        if (editDistance(target, query) <= maxDistance) return true;

        // Substring / word-level fuzzy match
        for (String word : target.split("\\s+")) {
            if (editDistance(word, query) <= maxDistance) {
                return true;
            }
        }
        return false;
    }
}