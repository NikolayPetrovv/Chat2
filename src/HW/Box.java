package HW;

import java.util.ArrayList;

public class Box<T extends Fruit> {

    private Float oneFruitWeight = 0.0f;

    private ArrayList<T> arrayList = new ArrayList();

    public void addFruit(T fruit) {
        arrayList.add(fruit);
        oneFruitWeight = fruit.getWeight();
    }

    public Float getWeight() {
        return arrayList.size() * oneFruitWeight;
    }

    public boolean compare(Box box) {
        return this.getWeight().equals(box.getWeight());
    }

    public void pourOut(Box<T> box){
        for (int i = 0; i < arrayList.size(); i++) {
            box.addFruit(arrayList.get(i));
        }
        arrayList.clear();
    }
}
