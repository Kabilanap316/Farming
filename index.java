import java.util.*;
import java.util.function.*;

class Farm {
    private int water;
    private int food;
    private Map<String, List<Crop>> crops = new HashMap<>();
    private Map<String, List<Animal>> animals = new HashMap<>();

    public Farm(int water, int food) {
        this.water = water;
        this.food = food;
        addCrop("wheat", 5, 3, 2, 5);
        addAnimal("cow", 2, 5, 3, 2);
    }

    private void addCrop(String type, int quantity, int daysToGrow, int waterRequired, int harvestYield) {
        crops.computeIfAbsent(type, k -> new ArrayList<>());
        for (int i = 0; i < quantity; i++) crops.get(type).add(new Crop(daysToGrow, waterRequired, harvestYield));
    }

    private void addAnimal(String type, int quantity, int daysToGrow, int foodRequired, int productYield) {
        animals.computeIfAbsent(type, k -> new ArrayList<>());
        for (int i = 0; i < quantity; i++) animals.get(type).add(new Animal(daysToGrow, foodRequired, productYield));
    }

    public void dailyManagement(int day) {
        System.out.println("Day " + day);

        distributeResources(crops, "water", Crop::grow, Crop::getWaterRequired, Crop::isMature, Crop::getHarvestYield);
        distributeResources(animals, "food", Animal::grow, Animal::getFoodRequired, Animal::isMature, Animal::getProductYield);

        if (day % 3 == 0) addCrop("wheat", 1, 3, 2, 5);
        if (day % 5 == 0) addAnimal("cow", 1, 5, 3, 2);

        System.out.println("End of Day " + day + " - Water: " + water + ", Food: " + food);
        System.out.println("-".repeat(20));
    }

    private <T> void distributeResources(
        Map<String, List<T>> entities,
        String resourceType,
        Consumer<T> grow,
        ToIntFunction<T> resourceRequired,
        Predicate<T> isMature,
        ToIntFunction<T> yield
    ) {
        for (String type : entities.keySet()) {
            int totalRequired = entities.get(type).stream().mapToInt(resourceRequired).sum();
            if (resourceType.equals("water")) {
                water -= totalRequired;
            } else {
                food -= totalRequired;
            }

            if ((resourceType.equals("water") && water >= 0) || (resourceType.equals("food") && food >= 0)) {
                entities.get(type).forEach(grow);
                System.out.println("Provided " + resourceType + " to " + entities.get(type).size() + " " + type + "(s).");
            } else {
                System.out.println("Not enough " + resourceType + " for " + type + ".");
                if (resourceType.equals("water")) {
                    water += totalRequired; // rollback
                } else {
                    food += totalRequired; // rollback
                }
            }

            entities.get(type).removeIf(entity -> {
                if (isMature.test(entity)) {
                    if (resourceType.equals("water")) {
                        food += yield.applyAsInt(entity);
                    } else {
                        food += yield.applyAsInt(entity);
                    }
                    System.out.println("Collected from 1 " + type + ".");
                    return true;
                }
                return false;
            });
        }
    }

    public static void main(String[] args) {
        Farm farm = new Farm(100, 50);
        for (int day = 1; day <= 10; day++) farm.dailyManagement(day);
    }
}

class Crop {
    private final int daysToGrow, waterRequired, harvestYield;
    private int growthStage;

    public Crop(int daysToGrow, int waterRequired, int harvestYield) {
        this.daysToGrow = daysToGrow;
        this.waterRequired = waterRequired;
        this.harvestYield = harvestYield;
    }

    public void grow() { if (growthStage < daysToGrow) growthStage++; }
    public boolean isMature() { return growthStage >= daysToGrow; }
    public int getWaterRequired() { return waterRequired; }
    public int getHarvestYield() { return harvestYield; }
}

class Animal {
    private final int daysToGrow, foodRequired, productYield;
    private int growthStage;

    public Animal(int daysToGrow, int foodRequired, int productYield) {
        this.daysToGrow = daysToGrow;
        this.foodRequired = foodRequired;
        this.productYield = productYield;
    }

    public void grow() { if (growthStage < daysToGrow) growthStage++; }
    public boolean isMature() { return growthStage >= daysToGrow; }
    public int getFoodRequired() { return foodRequired; }
    public int getProductYield() { return productYield; }
}
