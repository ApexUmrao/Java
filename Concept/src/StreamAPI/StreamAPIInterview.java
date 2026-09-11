package StreamAPI;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class StreamAPIInterview {

    // ─────────────────────────────────────────────
    // Q1. Find all even numbers from a list
    // ─────────────────────────────────────────────
    static void q1() {
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> evens = nums.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("Q1 Even numbers: " + evens); // [2, 4, 6, 8, 10]
    }

    // ─────────────────────────────────────────────
    // Q2. Find the first non-repeated character in a String
    // ─────────────────────────────────────────────
    static void q2() {
        String str = "swiss";
        Optional<Character> result = str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst();
        System.out.println("Q2 First non-repeated char: " + result.orElse(null)); // w
    }

    // ─────────────────────────────────────────────
    // Q3. Count occurrences of each character in a String
    // ─────────────────────────────────────────────
    static void q3() {
        String str = "interview";
        Map<Character, Long> freq = str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("Q3 Char frequency: " + freq);
    }

    // ─────────────────────────────────────────────
    // Q4. Find the second highest number in a list
    // ─────────────────────────────────────────────
    static void q4() {
        List<Integer> nums = Arrays.asList(5, 2, 8, 1, 9, 3, 7);
        Optional<Integer> second = nums.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst();
        System.out.println("Q4 Second highest: " + second.orElse(-1)); // 8
    }

    // ─────────────────────────────────────────────
    // Q5. Group employees by department
    // ─────────────────────────────────────────────
    record Employee(String name, String dept, double salary) {}

    static void q5() {
        List<Employee> emps = List.of(
                new Employee("Alice", "IT", 80000),
                new Employee("Bob", "HR", 60000),
                new Employee("Charlie", "IT", 90000),
                new Employee("David", "HR", 70000),
                new Employee("Eve", "Finance", 75000)
        );
        Map<String, List<Employee>> grouped = emps.stream()
                .collect(Collectors.groupingBy(Employee::dept));
        grouped.forEach((dept, list) ->
                System.out.println("Q5 " + dept + ": " + list.stream().map(Employee::name).toList()));
    }

    // ─────────────────────────────────────────────
    // Q6. Find highest salary per department
    // ─────────────────────────────────────────────
    static void q6() {
        List<Employee> emps = List.of(
                new Employee("Alice", "IT", 80000),
                new Employee("Bob", "HR", 60000),
                new Employee("Charlie", "IT", 90000),
                new Employee("David", "HR", 70000)
        );
        Map<String, Optional<Employee>> maxSalary = emps.stream()
                .collect(Collectors.groupingBy(Employee::dept,
                        Collectors.maxBy(Comparator.comparingDouble(Employee::salary))));
        maxSalary.forEach((dept, emp) ->
                System.out.println("Q6 " + dept + " top earner: " + emp.map(Employee::name).orElse("N/A")));
    }

    // ─────────────────────────────────────────────
    // Q7. Flatten a list of lists
    // ─────────────────────────────────────────────
    static void q7() {
        List<List<Integer>> nested = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5),
                Arrays.asList(6, 7, 8, 9)
        );
        List<Integer> flat = nested.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        System.out.println("Q7 Flattened: " + flat);
    }

    // ─────────────────────────────────────────────
    // Q8. Remove duplicates from a list preserving order
    // ─────────────────────────────────────────────
    static void q8() {
        List<Integer> nums = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);
        List<Integer> distinct = nums.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Q8 Distinct ordered: " + distinct);
    }

    // ─────────────────────────────────────────────
    // Q9. Sort a list of strings by length, then alphabetically
    // ─────────────────────────────────────────────
    static void q9() {
        List<String> words = Arrays.asList("banana", "apple", "fig", "date", "kiwi");
        List<String> sorted = words.stream()
                .sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
                .collect(Collectors.toList());
        System.out.println("Q9 Sorted: " + sorted);
    }

    // ─────────────────────────────────────────────
    // Q10. Sum of all salaries using reduce
    // ─────────────────────────────────────────────
    static void q10() {
        List<Double> salaries = Arrays.asList(50000.0, 60000.0, 75000.0, 90000.0);
        double total = salaries.stream()
                .reduce(0.0, Double::sum);
        System.out.println("Q10 Total salary: " + total);
    }

    // ─────────────────────────────────────────────
    // Q11. Partition employees into high/low salary
    // ─────────────────────────────────────────────
    static void q11() {
        List<Employee> emps = List.of(
                new Employee("Alice", "IT", 80000),
                new Employee("Bob", "HR", 60000),
                new Employee("Charlie", "IT", 90000),
                new Employee("David", "HR", 55000)
        );
        Map<Boolean, List<Employee>> partitioned = emps.stream()
                .collect(Collectors.partitioningBy(e -> e.salary() >= 70000));
        System.out.println("Q11 High earners: " + partitioned.get(true).stream().map(Employee::name).toList());
        System.out.println("Q11 Low  earners: " + partitioned.get(false).stream().map(Employee::name).toList());
    }

    // ─────────────────────────────────────────────
    // Q12. Convert a list to a comma-separated String
    // ─────────────────────────────────────────────
    static void q12() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        String result = names.stream()
                .collect(Collectors.joining(", "));
        System.out.println("Q12 Joined: " + result);
    }

    // ─────────────────────────────────────────────
    // Q13. Find duplicate elements in a list
    // ─────────────────────────────────────────────
    static void q13() {
        List<Integer> nums = Arrays.asList(1, 2, 3, 2, 4, 3, 5);
        Set<Integer> seen = new HashSet<>();
        List<Integer> duplicates = nums.stream()
                .filter(n -> !seen.add(n))
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Q13 Duplicates: " + duplicates); // [2, 3]
    }

    // ─────────────────────────────────────────────
    // Q14. Check if all / any / none match a condition
    // ─────────────────────────────────────────────
    static void q14() {
        List<Integer> nums = Arrays.asList(2, 4, 6, 8, 10);
        System.out.println("Q14 allMatch even : " + nums.stream().allMatch(n -> n % 2 == 0));  // true
        System.out.println("Q14 anyMatch > 5  : " + nums.stream().anyMatch(n -> n > 5));        // true
        System.out.println("Q14 noneMatch odd : " + nums.stream().noneMatch(n -> n % 2 != 0)); // true
    }

    // ─────────────────────────────────────────────
    // Q15. Convert List<String> to Map<String, Integer> (name → length)
    // ─────────────────────────────────────────────
    static void q15() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        Map<String, Integer> nameLen = names.stream()
                .collect(Collectors.toMap(Function.identity(), String::length));
        System.out.println("Q15 Name-Length map: " + nameLen);
    }

    // ─────────────────────────────────────────────
    // Q16. Find top 3 highest paid employees
    // ─────────────────────────────────────────────
    static void q16() {
        List<Employee> emps = List.of(
                new Employee("Alice", "IT", 80000),
                new Employee("Bob", "HR", 60000),
                new Employee("Charlie", "IT", 95000),
                new Employee("David", "HR", 70000),
                new Employee("Eve", "Finance", 85000)
        );
        List<String> top3 = emps.stream()
                .sorted(Comparator.comparingDouble(Employee::salary).reversed())
                .limit(3)
                .map(Employee::name)
                .collect(Collectors.toList());
        System.out.println("Q16 Top 3 earners: " + top3);
    }

    // ─────────────────────────────────────────────
    // Q17. Infinite stream — first 10 Fibonacci numbers
    // ─────────────────────────────────────────────
    static void q17() {
        Stream.iterate(new long[]{0, 1}, f -> new long[]{f[1], f[0] + f[1]})
                .limit(10)
                .map(f -> f[0])
                .forEach(n -> System.out.print(n + " "));
        System.out.println("\nQ17 First 10 Fibonacci numbers ↑");
    }

    // ─────────────────────────────────────────────
    // Q18. Parallel stream — sum of large range
    // ─────────────────────────────────────────────
    static void q18() {
        long sum = LongStream.rangeClosed(1, 1_000_000)
                .parallel()
                .sum();
        System.out.println("Q18 Sum 1..1M (parallel): " + sum);
    }

    // ─────────────────────────────────────────────
    // Q19. Word frequency count from a sentence
    // ─────────────────────────────────────────────
    static void q19() {
        String sentence = "to be or not to be that is the question to be";
        Map<String, Long> freq = Arrays.stream(sentence.split(" "))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        freq.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.println("Q19 " + e.getKey() + " → " + e.getValue()));
    }

    // ─────────────────────────────────────────────
    // Q20. Average salary of each department
    // ─────────────────────────────────────────────
    static void q20() {
        List<Employee> emps = List.of(
                new Employee("Alice", "IT", 80000),
                new Employee("Bob", "HR", 60000),
                new Employee("Charlie", "IT", 90000),
                new Employee("David", "HR", 70000)
        );
        Map<String, Double> avgSalary = emps.stream()
                .collect(Collectors.groupingBy(Employee::dept,
                        Collectors.averagingDouble(Employee::salary)));
        System.out.println("Q20 Avg salary by dept: " + avgSalary);
    }

    // ─────────────────────────────────────────────
    // MAIN
    // ─────────────────────────────────────────────
    public static void main(String[] args) {
        q1(); q2(); q3(); q4(); q5();
        q6(); q7(); q8(); q9(); q10();
        q11(); q12(); q13(); q14(); q15();
        q16(); q17(); q18(); q19(); q20();
    }
}