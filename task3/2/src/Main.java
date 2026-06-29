// Программа - содержащая иерархии товаров для склада. Заполнить склад до предела и высчитать вес хранимого товара.

interface Item {
    int getWeight();
}



class Box implements Item {
    int weight; // вес

    public Box(int w) {
        weight = w;
    }
    public int getWeight() {
        return weight;
    }
}


class Bag implements Item {
    int weight;

    public Bag(int w) {
        weight = w;
    }

    public int getWeight() {
        return weight;
    }
}



class Warehouse {
    Item[] items = new Item[5];
    int count = 0;

    public void add(Item item) {
        if (count < 5) {
            items[count] = item;
            count = count + 1;
        }
    }

    public int calcSum() {
        int sum = 0;
        for (int i = 0; i < count; i = i + 1) {
            sum = sum + items[i].getWeight();
        }
        return sum;
    }
}



public class Main {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();

        for (int i = 0; i < 5; i = i + 1) {
            int randomWeight = (new java.util.Random()).nextInt(100);

            if (i % 2 == 0) {
                Box b = new Box(randomWeight);
                warehouse.add(b);
            }
            else {
                Bag bag = new Bag(randomWeight);
                warehouse.add(bag);
            }
        }

        int total = warehouse.calcSum();
        System.out.println("Общий вес: " + total);
    }
}