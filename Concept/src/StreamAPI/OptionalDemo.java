package StreamAPI;

import java.util.Optional;
import java.util.stream.Stream;

public class OptionalDemo {

    public static void main(String[] args) {
        Optional<String> name = getName();

        System.out.println(name.isPresent());

        name.ifPresent(System.out::println);
       // System.out.println(name.get()); // throws nosuchelemntexcetion
    }

    public static Optional<String> getName(){
     //   return Optional.of(null); //throws exception
      //  return Optional.empty();
        return Optional.ofNullable(null);  //no Exception
    }

     class User{
        public Address address;

    }

     class Address{
        public String city;
    }

}
