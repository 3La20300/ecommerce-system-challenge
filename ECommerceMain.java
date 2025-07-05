//Hi,Fawry Team ana Mostafa Alaa
import java.util.*;
import java.time.LocalDate;

// Base Product class
abstract class Product {
    protected String name;
    protected double price;
    protected int quantity;
    
    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    
    public void reduceQuantity(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Not enough quantity available");
        }
        this.quantity -= amount;
    }
    
    public abstract boolean isExpirable();
    public abstract boolean isShippable();
    public abstract double getWeight(); // in kg, returns 0 if not shippable
}

// Expirable Product Interface
interface Expirable {
    LocalDate getExpirationDate();
    boolean isExpired();
}

// Shippable Product Interface
interface Shippable {
    double getWeight();
}

// Non-expirable, Shippable Product (TV)
class ElectronicsProduct extends Product implements Shippable {
    private double weight;
    
    public ElectronicsProduct(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }
    
    @Override
    public boolean isExpirable() { return false; }
    
    @Override
    public boolean isShippable() { return true; }
    
    @Override
    public double getWeight() { return weight; }
}

// Expirable, Shippable Product (Cheese, Biscuits)
class FoodProduct extends Product implements Expirable, Shippable {
    private LocalDate expirationDate;
    private double weight;
    
    public FoodProduct(String name, double price, int quantity, LocalDate expirationDate, double weight) {
        super(name, price, quantity);
        this.expirationDate = expirationDate;
        this.weight = weight;
    }
    
    @Override
    public boolean isExpirable() { return true; }
    
    @Override
    public boolean isShippable() { return true; }
    
    @Override
    public double getWeight() { return weight; }
    
    @Override
    public LocalDate getExpirationDate() { return expirationDate; }
    
    @Override
    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }
}

// Non-expirable, Non-shippable Product (Mobile scratch cards)
class DigitalProduct extends Product {
    public DigitalProduct(String name, double price, int quantity) {
        super(name, price, quantity);
    }
    
    @Override
    public boolean isExpirable() { return false; }
    
    @Override
    public boolean isShippable() { return false; }
    
    @Override
    public double getWeight() { return 0; }
}

// Cart Item class
class CartItem {
    private Product product;
    private int quantity;
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return product.getPrice() * quantity; }
    public double getTotalWeight() { return product.getWeight() * quantity; }
    
    public void addQuantity(int amount) {
        this.quantity += amount;
    }
}

// Shopping Cart class
class ShoppingCart {
    private Map<String, CartItem> items;
    
    public ShoppingCart() {
        this.items = new HashMap<>();
    }
    
    public void add(Product product, int quantity) {
        // Check if product is expired
        if (product instanceof Expirable && ((Expirable) product).isExpired()) {
            throw new IllegalArgumentException("Cannot add expired product: " + product.getName());
        }
        
        // Check quantity availability
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Not enough quantity available for " + product.getName() + 
                ". Available: " + product.getQuantity() + ", Requested: " + quantity);
        }
        
        // Add to cart
        if (items.containsKey(product.getName())) {
            CartItem existingItem = items.get(product.getName());
            int totalQuantity = existingItem.getQuantity() + quantity;
            if (totalQuantity > product.getQuantity()) {
                throw new IllegalArgumentException("Total quantity exceeds available stock for " + product.getName());
            }
            existingItem.addQuantity(quantity);
        } else {
            items.put(product.getName(), new CartItem(product, quantity));
        }
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public Collection<CartItem> getItems() {
        return items.values();
    }
    
    public double getSubtotal() {
        return items.values().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }
    
    public double getTotalWeight() {
        return items.values().stream()
                .filter(item -> item.getProduct().isShippable())
                .mapToDouble(CartItem::getTotalWeight)
                .sum();
    }
    
    public List<CartItem> getShippableItems() {
        return items.values().stream()
                .filter(item -> item.getProduct().isShippable())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}

// Customer class
class Customer {
    private String name;
    private double balance;
    
    public Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }
    
    public String getName() { return name; }
    public double getBalance() { return balance; }
    
    public void deductBalance(double amount) {
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance. Available: " + balance + ", Required: " + amount);
        }
        this.balance -= amount;
    }
}

// E-commerce System class
class ECommerceSystem {
    private static final double SHIPPING_RATE_PER_KG = 10.0; // $10 per kg
    private static final double BASE_SHIPPING_FEE = 20.0; // Base shipping fee
    
    public static void checkout(Customer customer, ShoppingCart cart) {
        try {
            // Check if cart is empty
            if (cart.isEmpty()) {
                throw new IllegalArgumentException("Cart is empty");
            }
            
            // Calculate totals
            double subtotal = cart.getSubtotal();
            double shippingFee = calculateShippingFee(cart);
            double totalAmount = subtotal + shippingFee;
            
            // Check customer balance
            if (customer.getBalance() < totalAmount) {
                throw new IllegalArgumentException("Insufficient balance. Required: " + totalAmount + 
                    ", Available: " + customer.getBalance());
            }
            
            // Process payment
            customer.deductBalance(totalAmount);
            
            // Update product quantities
            for (CartItem item : cart.getItems()) {
                item.getProduct().reduceQuantity(item.getQuantity());
            }
            
            // Print shipment notice
            printShipmentNotice(cart);
            
            // Print checkout receipt
            printCheckoutReceipt(cart, subtotal, shippingFee, totalAmount, customer.getBalance());
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
    
    private static double calculateShippingFee(ShoppingCart cart) {
        double totalWeight = cart.getTotalWeight();
        if (totalWeight == 0) {
            return 0; // No shipping for digital products
        }
        return BASE_SHIPPING_FEE + (totalWeight * SHIPPING_RATE_PER_KG);
    }
    
    private static void printShipmentNotice(ShoppingCart cart) {
        List<CartItem> shippableItems = cart.getShippableItems();
        if (!shippableItems.isEmpty()) {
            System.out.println("** Shipment notice **");
            for (CartItem item : shippableItems) {
                System.out.println(item.getQuantity() + "x " + item.getProduct().getName() + 
                    " " + (int)(item.getTotalWeight() * 1000) + "g");
            }
            System.out.println("Total package weight " + String.format("%.1f", cart.getTotalWeight()) + "kg");
            System.out.println();
        }
    }
    
    private static void printCheckoutReceipt(ShoppingCart cart, double subtotal, double shippingFee, 
                                           double totalAmount, double remainingBalance) {
        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.getItems()) {
            System.out.println(item.getQuantity() + "x " + item.getProduct().getName() + 
                " " + (int)item.getTotalPrice());
        }
        System.out.println();
        System.out.println("---");
        System.out.println();
        System.out.println("Subtotal " + (int)subtotal);
        if (shippingFee > 0) {
            System.out.println("Shipping " + (int)shippingFee);
        }
        System.out.println("Amount " + (int)totalAmount);
        System.out.println("Customer balance after payment: " + (int)remainingBalance);
        System.out.println();
        System.out.println("END.");
    }
}

// Main class with test examples
public class ECommerceMain {
    public static void main(String[] args) {
        System.out.println("=== E-COMMERCE SYSTEM TEST ===\n");
        
        // Create products
        Product cheese = new FoodProduct("Cheese", 100, 10, LocalDate.now().plusDays(7), 0.2);
        Product biscuits = new FoodProduct("Biscuits", 150, 5, LocalDate.now().plusDays(30), 0.7);
        Product tv = new ElectronicsProduct("TV", 500, 3, 15.0);
        Product mobile = new ElectronicsProduct("Mobile", 800, 2, 0.5);
        Product scratchCard = new DigitalProduct("Mobile Scratch Card", 50, 100);
        
        // Create customer
        Customer customer = new Customer("John Doe", 1500);
        
        // Test Case 1: Successful checkout with mixed products
        System.out.println("=== TEST CASE 1: Successful Mixed Cart Checkout ===");
        ShoppingCart cart1 = new ShoppingCart();
        cart1.add(cheese, 2);
        cart1.add(biscuits, 1);
        cart1.add(scratchCard, 1);
        
        ECommerceSystem.checkout(customer, cart1);
        System.out.println();
        
        // Test Case 2: Heavy shipment
        System.out.println("=== TEST CASE 2: Heavy Shipment ===");
        Customer customer2 = new Customer("Jane Smith", 2000);
        ShoppingCart cart2 = new ShoppingCart();
        cart2.add(tv, 1);
        cart2.add(mobile, 1);
        
        ECommerceSystem.checkout(customer2, cart2);
        System.out.println();
        
        // Test Case 3: Digital only (no shipping)
        System.out.println("=== TEST CASE 3: Digital Products Only ===");
        Customer customer3 = new Customer("Bob Wilson", 200);
        ShoppingCart cart3 = new ShoppingCart();
        cart3.add(scratchCard, 3);
        
        ECommerceSystem.checkout(customer3, cart3);
        System.out.println();
        
        // Test Case 4: Empty cart error
        System.out.println("=== TEST CASE 4: Empty Cart Error ===");
        ShoppingCart emptyCart = new ShoppingCart();
        ECommerceSystem.checkout(customer3, emptyCart);
        System.out.println();
        
        // Test Case 5: Insufficient balance error
        System.out.println("=== TEST CASE 5: Insufficient Balance Error ===");
        Customer poorCustomer = new Customer("Poor Customer", 50);
        ShoppingCart expensiveCart = new ShoppingCart();
        expensiveCart.add(tv, 1);
        ECommerceSystem.checkout(poorCustomer, expensiveCart);
        System.out.println();
        
        // Test Case 6: Quantity exceeds stock
        System.out.println("=== TEST CASE 6: Quantity Exceeds Stock ===");
        try {
            ShoppingCart cart6 = new ShoppingCart();
            cart6.add(tv, 5); // Only 3 available
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        System.out.println();
        
        // Test Case 7: Expired product
        System.out.println("=== TEST CASE 7: Expired Product Error ===");
        try {
            Product expiredCheese = new FoodProduct("Expired Cheese", 100, 5, LocalDate.now().minusDays(1), 0.2);
            ShoppingCart cart7 = new ShoppingCart();
            cart7.add(expiredCheese, 1);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        System.out.println();
        
        System.out.println("=== ALL TESTS COMPLETED ===");
    }
}
