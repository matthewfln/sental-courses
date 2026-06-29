// Программа - выводящая на экран случайно сгенерированное трёхзначное натуральное число и сумму его цифр

public class Main {
    public static void main(String[] args) {
        int number = (new java.util.Random()).nextInt(900) + 100;

        System.out.println("сгенерированное число: " + number);

        int one = number / 100;
        int two = (number / 10) % 10;
        int three = number % 10;

        int sum = one + two + three;

        System.out.println("сумма цифр: " + sum);
    }
}