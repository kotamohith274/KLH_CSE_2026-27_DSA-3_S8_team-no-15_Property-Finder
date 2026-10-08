package com.example.propertyfinder.algorithms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class AhoCorasick {

    private static class Node {
        Map<Character, Node> next = new HashMap<>();
        Node fail;
        List<String> output = new ArrayList<>();
    }

    private final Node root = new Node();

    public AhoCorasick(List<String> keywords) {
        // Build Trie
        for (String word : keywords) {
            Node curr = root;
            for (char ch : word.toLowerCase().toCharArray()) {
                curr = curr.next.computeIfAbsent(ch, c -> new Node());
            }
            curr.output.add(word);
        }

        // Build Failure Links (BFS)
        Queue<Node> queue = new LinkedList<>();
        for (Node child : root.next.values()) {
            child.fail = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            for (Map.Entry<Character, Node> entry : curr.next.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();

                Node failNode = curr.fail;
                while (failNode != null && !failNode.next.containsKey(ch)) {
                    failNode = failNode.fail;
                }
                child.fail = (failNode != null) ? failNode.next.get(ch) : root;
                child.output.addAll(child.fail.output);
                queue.add(child);
            }
        }
    }

    public List<String> search(String text) {
        if (text == null) return Collections.emptyList();
        Set<String> matches = new LinkedHashSet<>();
        Node curr = root;
        text = text.toLowerCase();

        for (char ch : text.toCharArray()) {
            while (curr != null && !curr.next.containsKey(ch)) {
                curr = curr.fail;
            }
            curr = (curr == null) ? root : curr.next.get(ch);
            matches.addAll(curr.output);
        }
        return new ArrayList<>(matches);
    }
}