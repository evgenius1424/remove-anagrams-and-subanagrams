import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Solution03Bitset {

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

    static class FrequencyWordPair {
        int[] freq;
        String word;

        FrequencyWordPair(int[] freq, String word) {
            this.freq = freq;
            this.word = word;
        }
    }

    public static List<String> removeAnagramsAndSubAnagramsBitset(List<String> words) {
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
        List<FrequencyWordPair> uniqueGroups = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : groupedByFreq.entrySet()) {
            if (entry.getValue().size() == 1) {
                String freqStr = entry.getKey();
                int[] freq = parseFrequencyVector(freqStr);
                uniqueGroups.add(new FrequencyWordPair(freq, entry.getValue().get(0)));
            }
        }

        // Sort by total character count (descending) for optimal processing
        uniqueGroups.sort((a, b) -> {
            int sumA = Arrays.stream(a.freq).sum();
            int sumB = Arrays.stream(b.freq).sum();
            return Integer.compare(sumB, sumA);
        });

        // Bitset index: bitsets[char][count] = set of indices
        @SuppressWarnings("unchecked")
        Set<Integer>[][] bitsets = new Set[26][17];
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 17; j++) {
                bitsets[i][j] = new HashSet<>();
            }
        }

        List<String> result = new ArrayList<>();

        for (int index = 0; index < uniqueGroups.size(); index++) {
            FrequencyWordPair pair = uniqueGroups.get(index);
            int[] freq = pair.freq;
            String word = pair.word;
            boolean isDominated = false;

            // Check if this frequency vector is dominated
            for (int charIndex = 0; charIndex < 26; charIndex++) {
                int count = freq[charIndex];
                // Check if any existing vector has more of this character
                // and also dominates in all other characters
                for (int higherCount = count + 1; higherCount < 17; higherCount++) {
                    if (!bitsets[charIndex][higherCount].isEmpty()) {
                        // Check if any of these candidates dominates our frequency
                        for (int candidateIndex : bitsets[charIndex][higherCount]) {
                            int[] candidateFreq = uniqueGroups.get(candidateIndex).freq;
                            // Check dominance: candidate must have >= count for all chars
                            boolean dominates = true;
                            for (int otherChar = 0; otherChar < 26; otherChar++) {
                                if (candidateFreq[otherChar] < freq[otherChar]) {
                                    dominates = false;
                                    break;
                                }
                            }
                            if (dominates) {
                                isDominated = true;
                                break;
                            }
                        }
                        if (isDominated) {
                            break;
                        }
                    }
                }
                if (isDominated) {
                    break;
                }
            }

            if (!isDominated) {
                // Add this vector to the result
                result.add(word);
                // Index this vector in bitsets
                for (int charIndex = 0; charIndex < 26; charIndex++) {
                    int count = freq[charIndex];
                    if (count > 0 && count < 17) {
                        bitsets[charIndex][count].add(index);
                    }
                }
            }
        }

        return result;
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
        System.out.println("Java Solution 3: Bitset Indexing");
        System.out.println("Time: O(g · 26 · L), Space: O(26 · L · g/64)");
        System.out.println("Run: javac Solution03Bitset.java && java Solution03Bitset");
        System.out.println();

        List<TestCase> testCases = loadTestCases();
        int passed = 0;
        int failed = 0;

        System.out.println("Running " + testCases.size() + " tests...");
        System.out.println();

        for (TestCase testCase : testCases) {
            List<String> result = removeAnagramsAndSubAnagramsBitset(testCase.input);
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