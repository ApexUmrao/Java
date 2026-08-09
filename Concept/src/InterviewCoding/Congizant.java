package InterviewCoding;

import java.util.stream.Collectors;

public class Congizant {

    public static void main (String[] args) {

        String str = "abracadabra";
            String result = str.chars()
                    .distinct()
                    .mapToObj(c -> String.valueOf((char) c))
                    .collect(Collectors.joining());

            System.out.println(result);
        }
    }

