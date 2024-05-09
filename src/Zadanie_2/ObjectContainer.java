package Zadanie_2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class ObjectContainer<T extends Serializable> implements Serializable {
    public T object;
    public ObjectContainer<T> next;
    private SerializablePredicate<T> condition;

    public ObjectContainer(SerializablePredicate<T> condition) {
        this.condition = condition;
    }

    public ObjectContainer() {
        this.condition = x -> true;
    }

    public boolean add(T obj) {
        if (condition.test(obj)) {
            if (object == null) {
                object = obj;
                return true;
            } else {
                if (next == null) {
                    next = new ObjectContainer<>(condition);
                }
                return next.add(obj);
            }
        }
        return false;
    }

    public List<T> getWithFilter(Predicate<T> filter) {
        List<T> result = new ArrayList<>();
        ObjectContainer<T> current = this;
        while (current != null && current.object != null) {
            if (filter.test(current.object)) {
                result.add(current.object);
            }
            current = current.next;
        }
        return result;
    }

    public void removeIf(Predicate<T> filter) {
        ObjectContainer<T> current = this;
        ObjectContainer<T> prev = null;
        while (current != null && current.object != null) {
            if (filter.test(current.object)) {
                if (prev != null) {
                    prev.next = current.next;
                } else {
                    object = current.next != null ? current.next.object : null;
                    next = current.next != null ? current.next.next : null;
                }
            }
            prev = current;
            current = current.next;
        }
    }

    //Ta funkcja musi posiadać w argumentach Function<T, String> formatter poza filtrem i zapisywac z tym podanym formatterem jej uzycie w Main ma wygladać peopleFromWarsaw.storeToFile("youngPeopleFromWarsaw.txt", p -> p.getAge() < 30, p -> p.getName() + ";" + p.getAge() + ";" + p.getCity());
    //I wlaśnie nie wiem jak to dobrze wywołać w main :/
    public void storeToFile(String filename, Predicate<T> filter, Function<T, String> formatter) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            ObjectContainer<T> current = this;
            while (current != null && current.object != null) {
                if (filter.test(current.object)) {
                    writer.write(formatter.apply(current.object));
                    writer.newLine();
                }
                current = current.next;
            }
        }
    }
    public void storeToFile(String filename, Predicate<T> filter) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            ObjectContainer<T> current = this;
            while (current != null && current.object != null && !filter.test(current.object)) {
                current = current.next;
            }
            outputStream.writeObject(this);
        }
    }


    public void storeToFile(String filename) throws IOException {
        storeToFile(filename, p -> true);
    }

    public static <T extends Serializable> ObjectContainer<T> fromFile(String filename) {
        ObjectContainer<T> result = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            while (true) {
                try {
                    result = (ObjectContainer<T>) in.readObject();
                } catch (EOFException eof) {
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectContainer<?> that = (ObjectContainer<?>) o;

        if (!Objects.equals(object, that.object)) return false;
        if (!Objects.equals(next, that.next)) return false;
        return Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        int result = object != null ? object.hashCode() : 0;
        result = 31 * result + (next != null ? next.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        return result;
    }
}