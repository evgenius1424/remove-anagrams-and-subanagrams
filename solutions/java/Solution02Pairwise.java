import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Solution02Pairwise {

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

    private static int[] getFrequencyVector(String word) {
        int[] freq = new int[26];
        for (char c : word.toCharArray()) {
            freq[c - 'a']++;
        }
        return freq;
    }

    private static boolean isDominatedBy(int[] smaller, int[] larger) {
        int smallerSum = Arrays.stream(smaller).sum();
        int largerSum = Arrays.stream(larger).sum();

        if (smallerSum >= largerSum) {
            return false;
        }

        for (int i = 0; i < 26; i++) {
            if (smaller[i] > larger[i]) {
                return false;
            }
        }

        return true;
    }

    public static List<String> removeAnagramsAndSubAnagramsPairwise(List<String> words) {
        if (words.isEmpty()) {
            return new ArrayList<>();
        }

        // Group words by frequency vector
        Map<String, List<String>> groupedByFreq = new HashMap<>();
        for (String word : words) {
            int[] freq = getFrequencyVector(word);
            String freqKey = Arrays.toString(freq);
            groupedByFreq.computeIfAbsent(freqKey, k -> new ArrayList<>()).add(word);
        }

        // Keep only groups with single words (no anagrams)
        List<Map.Entry<int[], String>> uniqueGroups = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : groupedByFreq.entrySet()) {
            if (entry.getValue().size() == 1) {
                String freqStr = entry.getKey();
                int[] freq = parseFrequencyVector(freqStr);
                uniqueGroups.add(new AbstractMap.SimpleEntry<>(freq, entry.getValue().get(0)));
            }
        }

        // Find maximal (non-dominated) frequency vectors
        List<String> maximalGroups = new ArrayList<>();
        for (Map.Entry<int[], String> candidate : uniqueGroups) {
            int[] candidateFreq = candidate.getKey();
            String candidateWord = candidate.getValue();
            boolean isDominated = false;

            for (Map.Entry<int[], String> other : uniqueGroups) {
                int[] otherFreq = other.getKey();
                if (!Arrays.equals(candidateFreq, otherFreq) && isDominatedBy(candidateFreq, otherFreq)) {
                    isDominated = true;
                    break;
                }
            }

            if (!isDominated) {
                maximalGroups.add(candidateWord);
            }
        }

        return maximalGroups;
    }

    private static int[] parseFrequencyVector(String freqStr) {
        // Parse "[1, 2, 0, ...]" format
        freqStr = freqStr.substring(1, freqStr.length() - 1);
        String[] parts = freqStr.split(", ");
        int[] result = new int[26];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
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
        System.out.println("Java Solution 2: Frequency Vectors + Pairwise");
        System.out.println("Time: O(g² · 26), Space: O(g · 26)");
        System.out.println("Run: javac Solution02Pairwise.java && java Solution02Pairwise");
        System.out.println();

        List<TestCase> testCases = loadTestCases();
        int passed = 0;
        int failed = 0;

        System.out.println("Running " + testCases.size() + " tests...");
        System.out.println();

        for (TestCase testCase : testCases) {
            List<String> result = removeAnagramsAndSubAnagramsPairwise(testCase.input);
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