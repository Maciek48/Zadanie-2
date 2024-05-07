package Zadanie_2;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        ObjectContainer<Person> peopleFromWarsaw = new ObjectContainer<>((Person p) -> p.getCity().equals("Warsaw"));
        peopleFromWarsaw.add(new Person("Jan", "Warsaw", 30));
        peopleFromWarsaw.add(new Person("Weronika", "Warsaw", 20));
        peopleFromWarsaw.add(new Person("Waldek", "Monaco", 34));

        List<Person> allPeople = peopleFromWarsaw.getWithFilter(p -> true);
        System.out.println("Wszystkie osoby z Warsaw:");
        for (Person person : allPeople) {
            System.out.println(person.getName() + " z " + person.getCity() + ", wiek: " + person.getAge());
        }

        System.out.println("\nLista kobiet z Warsaw:");
        List<Person> females = peopleFromWarsaw.getWithFilter(p -> p.getName().endsWith("a"));
        for (Person female : females) {
            System.out.println(female.getName() + " z " + female.getCity() + ", wiek: " + female.getAge());
        }

        peopleFromWarsaw.removeIf(p -> p.getAge() > 50);
        System.out.println("\nLista osób po usunięciu tych powyżej 50 lat:");
        List<Person> remainingPeople = peopleFromWarsaw.getWithFilter(p -> true);
        for (Person person : remainingPeople) {
            System.out.println(person.getName() + " z " + person.getCity() + ", wiek: " + person.getAge());
        }

        //SerializablePredicate<Person> predicate = p -> p.getAge() < 30;
        //Tutaj jest blad przy probie zapisu danych
        peopleFromWarsaw.storeToFile("youngPeopleFromWarsaw.txt", p -> p.getAge() < 30, p -> p.getName() + ";" + p.getAge() + ";" + p.getCity());
        peopleFromWarsaw.storeToFile("warsawPeople.txt");

        ObjectContainer<Person> youngWarsawPeople = ObjectContainer.fromFile("warsawPeople.txt");
        List<Person> allPeopleFromFIle = youngWarsawPeople.getWithFilter(p -> true);
        System.out.println("\nWszystkie mlode osoby z Warsaw z pliku:");
        for (Person person : allPeopleFromFIle) {
            System.out.println(person.getName() + " z " + person.getCity() + ", wiek: " + person.getAge());
        }

        System.out.println();
        System.out.println("Test serializacji");
        if (peopleFromWarsaw.equals(youngWarsawPeople)) {
            System.out.println("Obiekty są identyczne po serializacji i deserializacji.");
        } else {
            System.out.println("Obiekty różnią się po serializacji i deserializacji.");
        }

        System.out.println();
        System.out.println("Oryginalny Kontener: " + peopleFromWarsaw.getWithFilter(p -> true));
        System.out.println("Zdeserializowany Kontener: " + youngWarsawPeople.getWithFilter(p -> true));
    }
}
