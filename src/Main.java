import java.util.*;

class FlashSaleInventory {

    private HashMap<String, Integer> stock = new HashMap<>();
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList = new HashMap<>();

    public void addProduct(String productId, int quantity) {
        stock.put(productId, quantity);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    public String checkStock(String productId) {
        int available = stock.getOrDefault(productId, 0);
        return available + " units available";
    }

    public synchronized String purchaseItem(String productId, int userId) {
        int available = stock.getOrDefault(productId, 0);

        if (available > 0) {
            stock.put(productId, available - 1);
            return "Success, " + (available - 1) + " units remaining";
        } else {
            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);
            int position = queue.size() + 1;
            queue.put(userId, position);
            return "Added to waiting list, position #" + position;
        }
    }

    public static void main(String[] args) {

        FlashSaleInventory system = new FlashSaleInventory();

        system.addProduct("IPHONE15_256GB", 100);

        System.out.println(system.checkStock("IPHONE15_256GB"));

        System.out.println(system.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 67890));

        for (int i = 0; i < 100; i++) {
            system.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(system.purchaseItem("IPHONE15_256GB", 99999));
    }
}