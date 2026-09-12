import java.util.Scanner;

public class PropertyFinderEngine {

    static class Property {
        int id;
        String type;        // e.g., "3BHK", "2BHK", "Villa", "Commercial"
        String title;
        String location;
        int priceInLakhs;
        String description;

        Property(int id, String type, String title, String location, int priceInLakhs, String description) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.location = location;
            this.priceInLakhs = priceInLakhs;
            this.description = description;
        }

        void display() {
            System.out.println("--------------------------------------------------");
            System.out.println("ID: " + id + " | " + title + " (" + type + ")");
            System.out.println("Location: " + location + " | Price: Rs. " + priceInLakhs + " Lakhs");
            System.out.println("Details : " + description);
        }
    }

    // -------------------------------------------------------------
    // MODULE 2: KMP SEARCH (Used for Substring/Type Matching)
    // -------------------------------------------------------------
    private static int[] computeLPS(String pat) {
        int m = pat.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static boolean kmpSearch(String text, String pattern) {
        if (pattern.length() == 0) return true;
        if (text.length() < pattern.length()) return false;

        String t = text.toLowerCase();
        String p = pattern.toLowerCase();

        int[] lps = computeLPS(p);
        int i = 0;
        int j = 0;

        while (i < t.length()) {
            if (t.charAt(i) == p.charAt(j)) {
                i++;
                j++;
            }
            if (j == p.length()) {
                return true;
            } else if (i < t.length() && t.charAt(i) != p.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------
    // MODULE 3: WAGNER-FISCHER LEVENSHTEIN DP (Typo Tolerant Search)
    // -------------------------------------------------------------
    public static int computeEditDistance(String s1, String s2) {
        String str1 = s1.toLowerCase();
        String str2 = s2.toLowerCase();
        int m = str1.length();
        int n = str2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int insertOp = dp[i][j - 1];
                    int deleteOp = dp[i - 1][j];
                    int replaceOp = dp[i - 1][j - 1];

                    int minOp = insertOp;
                    if (deleteOp < minOp) minOp = deleteOp;
                    if (replaceOp < minOp) minOp = replaceOp;

                    dp[i][j] = 1 + minOp;
                }
            }
        }
        return dp[m][n];
    }

    // -------------------------------------------------------------
    // FILTER & SEARCH HANDLERS
    // -------------------------------------------------------------
    public static void filterByTypeOrKeyword(Property[] dataset, String query) {
        System.out.println("\n=== Results matching \"" + query + "\" (via KMP Search) ===");
        boolean found = false;
        for (Property p : dataset) {
            if (kmpSearch(p.type, query) || kmpSearch(p.title, query) || kmpSearch(p.description, query)) {
                p.display();
                found = true;
            }
        }
        if (!found) System.out.println("No properties found matching your query.");
    }

    public static void filterByFuzzyLocation(Property[] dataset, String locQuery) {
        System.out.println("\n=== Results for Location \"" + locQuery + "\" (via Levenshtein DP) ===");
        boolean found = false;
        int threshold = 2; // allows up to 2 typos/mismatches
        for (Property p : dataset) {
            int dist = computeEditDistance(p.location, locQuery);
            if (dist <= threshold) {
                System.out.println("[Matched with edit distance: " + dist + "]");
                p.display();
                found = true;
            }
        }
        if (!found) System.out.println("No locations found matching within typo tolerance.");
    }

    public static void filterByBudget(Property[] dataset, int maxBudget) {
        System.out.println("\n=== Properties under Rs. " + maxBudget + " Lakhs ===");
        boolean found = false;
        for (Property p : dataset) {
            if (p.priceInLakhs <= maxBudget) {
                p.display();
                found = true;
            }
        }
        if (!found) System.out.println("No properties found within this budget.");
    }

    // -------------------------------------------------------------
    // INTERACTIVE CLI
    // -------------------------------------------------------------
    public static void main(String[] args) {
        // Sample Property Database
        Property[] properties = new Property[]{
            new Property(101, "3BHK", "Gated Community Luxury Flat", "Gachibowli", 145, "East facing with club house, gym, and swimming pool"),
            new Property(102, "2BHK", "Standard Highrise Apartment", "Madhapur", 85, "Close to metro station, 24/7 water supply"),
            new Property(103, "3BHK", "Premium Corner Residence", "Kondapur", 160, "Spacious balconies with modular kitchen and covered car parking"),
            new Property(104, "Villa", "Independent Triplex Villa", "Kondapur", 320, "Private garden, solar power backup, and home automation"),
            new Property(105, "1BHK", "Studio Apartment", "Banjara Hills", 55, "Furnished flat suitable for bachelor or working professional"),
            new Property(106, "3BHK", "Skyline Penthouse", "Gachibowli", 240, "Terrace view penthouse with dual car parking and clubhouse access"),
            new Property(107, "2BHK", "Budget Friendly Apartment", "Kukatpally", 65, "Near shopping malls and schools, ready to move in")
        };

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n==========================================");
            System.out.println("      PROPERTY FINDER SEARCH ENGINE       ");
            System.out.println("==========================================");
            System.out.println("1. Search by Type / Amenity (e.g., 3BHK, pool, gym)");
            System.out.println("2. Search by Location (Typo-Tolerant)");
            System.out.println("3. Filter by Maximum Budget");
            System.out.println("4. Exit");
            System.out.print("Select an option (1-4): ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Please enter a valid option number.");
                continue;
            }

            if (choice == 1) {
                System.out.print("Enter property type or keyword (e.g., 3BHK, gym): ");
                String input = scanner.nextLine().trim();
                filterByTypeOrKeyword(properties, input);
            } else if (choice == 2) {
                System.out.print("Enter location name (e.g., Gachbowli, Kondapur): ");
                String input = scanner.nextLine().trim();
                filterByFuzzyLocation(properties, input);
            } else if (choice == 3) {
                System.out.print("Enter maximum budget in Lakhs (e.g., 150): ");
                try {
                    int budget = Integer.parseInt(scanner.nextLine().trim());
                    filterByBudget(properties, budget);
                } catch (Exception e) {
                    System.out.println("Invalid numeric amount.");
                }
            } else if (choice == 4) {
                System.out.println("Exiting search engine. Done.");
                break;
            } else {
                System.out.println("Invalid selection. Choose between 1 and 4.");
            }
        }
        scanner.close();
    }
}