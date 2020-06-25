package HW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HW<T>{

    private static String[] arr = new String[]{"first","second"};

    public static void main(String[] args) {
        HW obj = new HW<>();
        System.out.println(arr[0] + " " + arr[1]);
        obj.change(arr);
        System.out.println(arr[0] + " " + arr[1]);
        ArrayList arrayList = obj.convert(arr);


        Box<Apple> appleBox = new Box<Apple>();
        Box<Orange> orangeBox = new Box<Orange>();
        appleBox.addFruit(new Apple());
        appleBox.addFruit(new Apple());
        appleBox.addFruit(new Apple());
        orangeBox.addFruit(new Orange());
        orangeBox.addFruit(new Orange());

        System.out.println(appleBox.getWeight());
        System.out.println(orangeBox.getWeight());
        System.out.println(appleBox.compare(orangeBox));

        Box<Apple> appleBox2 = new Box<Apple>();
        appleBox2.addFruit(new Apple());

        System.out.println(appleBox.getWeight());
        System.out.println(appleBox2.getWeight());
        appleBox.pourOut(appleBox2);
        System.out.println(appleBox.getWeight());
        System.out.println(appleBox2.getWeight());


    }

    public void change(T[] arr) {
       T tmp = arr[0];
       arr[0] = arr[1];
       arr[1] = tmp;
    }

    public ArrayList<T> convert(T[] array) {
        ArrayList<T> result = new ArrayList<>(Arrays.asList(array));
        return result;
    }
}
