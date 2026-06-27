package StreamAPI;

import java.text.Collator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stream2 {

    public static void main(String[] args) {

        List<String> list = Arrays.asList("AA","BBB","CCCC","D");

        list.stream().forEach(System.out::println);

        Map<Integer, String> map = list.stream()
                                      .collect(Collectors.toMap(
                                              x -> x.length(),
                                              y->y));
        System.out.println(map);
    }
}

