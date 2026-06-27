package StreamAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DemoStream {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

        List<Integer> list2 = new ArrayList(List.of(12,32,121,121));

        Stream<Integer> stream = list.stream();
        stream.filter(x -> x>1)
                .filter(x -> x%2==0)
                .map(x -> x*x)
                .forEach(System.out::println);

        list2.stream()
                .map(x -> x/2)
                .filter(x -> x%2==0)
                .findFirst()
                .ifPresent(System.out::println);

    }
}
