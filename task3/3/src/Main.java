//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
interface IProductPart
{
}
interface IProduct
{
    public void installFirstPart(IProductPart productPart);
    public void installSecondPart(IProductPart productPart);
    public void installThirdPart(IProductPart productPart);
}
interface ILineStep
{
    public IProductPart buildProductPart();
}
interface IAssemblyLine
{
    public IProduct assembleProduct(IProduct product);
}



class Chassis implements IProductPart {
    public Chassis() {
        System.out.println("Создан корпус. Серийный номер: " + (new java.util.Random()).nextInt(10000));
    }
}
class Motherboard implements IProductPart {
    public Motherboard() {
        System.out.println("Создана мат. плата. Серийный номер: " + (new java.util.Random()).nextInt(10000));
    }
}
class Monitor implements IProductPart {
    public Monitor() {
        System.out.println("Создан монитор. Серийный номер: " + (new java.util.Random()).nextInt(10000));
    }
}




class Laptop implements IProduct {
    private IProductPart part1;
    private IProductPart part2;
    private IProductPart part3;

    public void installFirstPart(IProductPart part) {
        this.part1 = part;
        System.out.println("В ноутбук установлен - корпус.");
    }
    public void installSecondPart(IProductPart part) {
        this.part2 = part;
        System.out.println("В ноутбук установлена - материнская плата.");
    }
    public void installThirdPart(IProductPart part) {
        this.part3 = part;
        System.out.println("В ноутбук установлен - монитор.");
    }
}





class ChassisStep implements ILineStep {
    public IProductPart buildProductPart() {
        System.out.println("Шаг 1: Подготовка корпуса...");
        return new Chassis();
    }
}
class MotherboardStep implements ILineStep {
    public IProductPart buildProductPart() {
        System.out.println("Шаг 2: Подготовка материнской платы...");
        return new Motherboard();
    }
}
class MonitorStep implements ILineStep {
    public IProductPart buildProductPart() {
        System.out.println("Шаг 3: Подготовка монитора...");
        return new Monitor();
    }
}



class LaptopAssemblyLine implements IAssemblyLine {
    private ILineStep firstStep;
    private ILineStep secondStep;
    private ILineStep thirdStep;

    public LaptopAssemblyLine(ILineStep step1, ILineStep step2, ILineStep step3) {
        this.firstStep = step1;
        this.secondStep = step2;
        this.thirdStep = step3;
        System.out.println("Сборочная линия ноутбуков успешно настроена!\n");
    }

    public IProduct assembleProduct(IProduct product) {
        System.out.println("НАЧАЛО СБОРКИ НОВОГО НОУТБУКА");
        System.out.println("---------------------------------");

        product.installFirstPart(firstStep.buildProductPart());
        System.out.println();

        product.installSecondPart(secondStep.buildProductPart());
        System.out.println();

        product.installThirdPart(thirdStep.buildProductPart());
        System.out.println("---------------------------------");
        System.out.println("СБОРКА УСПЕШНО ЗАВЕРШЕНА!\n");

        return product;
    }
}



public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        IAssemblyLine laptopLine = new LaptopAssemblyLine(new ChassisStep(), new MotherboardStep(), new MonitorStep());
        laptopLine.assembleProduct(new Laptop());
    }
}