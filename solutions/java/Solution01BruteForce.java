import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Solution01BruteForce {

    static class TestCase {
        public int id;
        public String category;
        public List<String> input;
        public List<String> expected;
        public String explanation;
    }

    static class TestData {
        @JsonProperty("test_cases")
        public List<TestCase> testCases;
    }

    private static boolean isAnagram(String word1, String word2) {
        char[] chars1 = word1.toCharArray();
        char[] chars2 = word2.toCharArray();
        Arrays.sort(chars1);
        Arrays.sort(chars2);
        return Arrays.equals(chars1, chars2);
    }

    private static boolean isSubAnagram(String smaller, String larger) {
        if (smaller.length() >= larger.length()) {
            return false;
        }

        Map<Character, Integer> smallerCounts = new HashMap<>();
        Map<Character, Integer> largerCounts = new HashMap<>();

        for (char c : smaller.toCharArray()) {
            smallerCounts.put(c, smallerCounts.getOrDefault(c, 0) + 1);
        }

        for (char c : larger.toCharArray()) {
            largerCounts.put(c, largerCounts.getOrDefault(c, 0) + 1);
        }

        for (Map.Entry<Character, Integer> entry : smallerCounts.entrySet()) {
            char c = entry.getKey();
            int count = entry.getValue();
            if (largerCounts.getOrDefault(c, 0) < count) {
                return false;
            }
        }

        return true;
    }

    public static List<String> removeAnagramsAndSubAnagramsBruteForce(List<String> words) {
        if (words.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Integer> toRemove = new HashSet<>();

        for (int i = 0; i < words.size(); i++) {
            for (int j = 0; j < words.size(); j++) {
                if (i != j) {
                    if (isAnagram(words.get(i), words.get(j))) {
                        toRemove.add(i);
                        toRemove.add(j);
                    } else if (isSubAnagram(words.get(i), words.get(j))) {
                        toRemove.add(i);
                    }
                }
            }
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if (!toRemove.contains(i)) {
                result.add(words.get(i));
            }
        }

        return result;
    }

    private static List<TestCase> loadTestCases() {
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get("../../testcases/cases.json")));
            ObjectMapper mapper = new ObjectMapper();
            TestData testData = mapper.readValue(jsonString, TestData.class);
            return testData.testCases;
        } catch (Exception e) {
            System.out.println("Could not load test cases from JSON, using embedded cases");
            return getEmbeddedTestCases();
        }
    }

    private static List<TestCase> getEmbeddedTestCases() {
        List<TestCase> cases = new ArrayList<>();

        TestCase case1 = new TestCase();
        case1.id = 1;
        case1.category = "basic";
        case1.input = Arrays.asList("a", "ab", "ba", "abc", "abcd");
        case1.expected = Arrays.asList("abcd");
        case1.explanation = "";
        cases.add(case1);

        TestCase case2 = new TestCase();
        case2.id = 2;
        case2.category = "basic";
        case2.input = Arrays.asList("abc", "def", "ghi");
        case2.expected = Arrays.asList("abc", "def", "ghi");
        case2.explanation = "";
        cases.add(case2);

        TestCase case3 = new TestCase();
        case3.id = 3;
        case3.category = "basic";
        case3.input = Arrays.asList("a", "aa", "aaa");
        case3.expected = Arrays.asList("aaa");
        case3.explanation = "";
        cases.add(case3);

        TestCase case4 = new TestCase();
        case4.id = 4;
        case4.category = "basic";
        case4.input = Arrays.asList("cat", "act", "dog");
        case4.expected = Arrays.asList("dog");
        case4.explanation = "";
        cases.add(case4);

        TestCase case5 = new TestCase();
        case5.id = 5;
        case5.category = "basic";
        case5.input = Arrays.asList("listen", "silent", "enlist");
        case5.expected = new ArrayList<>();
        case5.explanation = "";
        cases.add(case5);

        return cases;
    }

    public static void main(String[] args) {
        System.out.println("Java Solution 1: Brute Force");
        System.out.println("Time: O(n² · m), Space: O(n · m)");
        System.out.println("Run: javac Solution01BruteForce.java && java Solution01BruteForce");
        System.out.println();

        List<TestCase> testCases = loadTestCases();
        int passed = 0;
        int failed = 0;

        System.out.println("Running " + testCases.size() + " tests...");
        System.out.println();

        for (TestCase testCase : testCases) {
            List<String> result = removeAnagramsAndSubAnagramsBruteForce(testCase.input);
            List<String> resultSorted = new ArrayList<>(result);
            List<String> expectedSorted = new ArrayList<>(testCase.expected);
            Collections.sort(resultSorted);
            Collections.sort(expectedSorted);

            if (resultSorted.equals(expectedSorted)) {
                System.out.println("✓ Test " + testCase.id + ": " + testCase.category);
                passed++;
            } else {
                System.out.println("✗ Test " + testCase.id + ": " + testCase.category);
                System.out.println("  Input: " + testCase.input);
                System.out.println("  Expected: " + testCase.expected);
                System.out.println("  Got: " + result);
                failed++;
            }
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("Results: " + passed + " passed, " + failed + " failed");
    }
}