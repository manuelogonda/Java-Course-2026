package manu.oop;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class FoodOrderingSystem {
    void main(String[] args) { new ManuEats(); }

    enum FoodCategory {
        ALL,
        CHICKEN,
        KENYAN,
        GRILLS,
        PIZZA,
        BURGERS,
        BAKERY,
        DAIRY,
        SIDES,
        DRINKS,
        DESSERTS
    }

    enum OrderType { IN_PERSON, ONLINE }
    enum OrderMode { EAT_IN, PICKUP, DELIVERY }
    enum PaymentMethod{ MPESA, CASH, CARD }

    enum OrderStatus {
        OPEN, PLACED, CONFIRMED, PREPARING, READY, DELIVERED, PAID, CANCELLED
    }

    class MenuItem {

        private static int idCounter = 0;
        private int  itemId;
        private String name;
        private double  price;
        private FoodCategory category;
        private String  description;
        private boolean  available;

        public MenuItem(String name, double price,
                        FoodCategory category, String description) {
            idCounter++;
            this.itemId = idCounter;
            this.name  = name;
            this.price = price;
            this.category = category;
            this.description = description;
            this.available = true;
        }

        public MenuItem(String name, double price, FoodCategory category) {
            this(name, price, category, "");
        }

        public int  getItemId()  { return itemId; }
        public String getName()  { return name;  }
        public double getPrice() { return price; }
        public FoodCategory getCategory()  { return category;}
        public String  getDescription()  { return description; }
        public boolean  isAvailable() { return available;   }
        public void  setAvailable(boolean v) { this.available = v; }

        @Override
        public String toString() { return name + " — KES " + (int) price; }
    }

    class OrderItem {

        private MenuItem menuItem;
        private int      quantity;
        public OrderItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = Math.max(1, quantity);
        }

        public double  getLineTotal() { return menuItem.getPrice() * quantity; }
        public void incrementQuantity() { quantity++; }
        public boolean decrementQuantity(){
            if (quantity <= 1) return false;
            quantity--; return true;
        }

        public String toOrderLine() {
            return String.format("%dx  %-22s KES %,.0f",
                    quantity, menuItem.getName(), getLineTotal());
        }

        public MenuItem getMenuItem() { return menuItem; }
        public int      getQuantity() { return quantity; }
        public void     setQuantity(int q) { quantity = Math.max(1, q); }

        @Override
        public String toString() {
            return quantity + "x " + menuItem.getName()
                    + " — KES " + (int) getLineTotal();
        }
    }

    class Customer {

        private static int idCounter = 0;
        private String customerId;
        private String name;
        private String phone;

        public Customer(String name, String phone) {
            idCounter++;
            this.customerId = String.format("CUST-%04d", idCounter);
            this.name       = name;
            this.phone      = phone;
        }

        public String getCustomerId() { return customerId; }
        public String getName()       { return name;       }
        public String getPhone()      { return phone;      }

        @Override
        public String toString() { return name + " (" + phone + ")"; }
    }


    class DeliveryAddress {

        private String street;
        private String area;
        private String landmark;

        public DeliveryAddress(String street, String area, String landmark) {
            this.street   = street;
            this.area     = area;
            this.landmark = landmark;
        }

        public String getStreet()   { return street;   }
        public String getArea()     { return area;     }
        public String getLandmark() { return landmark; }

        @Override
        public String toString() {
            String s = street + ", " + area;
            if (!landmark.isEmpty()) s += " (near " + landmark + ")";
            return s;
        }
    }

    class DeliveryZone {

        private String  zoneName;
        private int  deliveryFee;
        private ArrayList<String> areas;

        public DeliveryZone(String zoneName, int deliveryFee) {
            this.zoneName  = zoneName;
            this.deliveryFee = deliveryFee;
            this.areas = new ArrayList<>();
        }

        public void addArea(String area) { areas.add(area.toLowerCase().trim()); }
        public boolean containsArea(String area) {
            return areas.contains(area.toLowerCase().trim());
        }
        public String getZoneName() { return zoneName; }
        public int getDeliveryFee() { return deliveryFee; }

        @Override
        public String toString() { return zoneName + " — KES " + deliveryFee; }
    }

    class Order {

        private static int orderCounter = 0;
        private String   orderId;
        private OrderType orderType;
        private OrderMode  orderMode;
        private OrderStatus status;
        private ArrayList<OrderItem> items;
        private Customer customer;
        private DeliveryAddress deliveryAddress;
        private int deliveryFee;
        private LocalDateTime createdAt;
        private static final double VAT_RATE = 0.16;

        public Order() {
            this(OrderType.IN_PERSON, OrderMode.EAT_IN, null);
        }

        public Order(OrderType type, OrderMode mode, Customer customer) {
            orderCounter++;
            this.orderId = String.format("ORD-%04d", orderCounter);
            this.orderType = type;
            this.orderMode = mode;
            this.customer = customer;
            this.status = OrderStatus.OPEN;
            this.items = new ArrayList<>();
            this.deliveryFee = 0;
            this.createdAt = LocalDateTime.now();
        }

        public void addItem(MenuItem menuItem) {
            if (status != OrderStatus.OPEN) return;
            for (OrderItem existing : items) {
                if (existing.getMenuItem().getItemId() == menuItem.getItemId()) {
                    existing.incrementQuantity();
                    return;
                }
            }
            items.add(new OrderItem(menuItem, 1));
        }

        public boolean removeItem(OrderItem orderItem) {
            if (status != OrderStatus.OPEN) return false;
            return items.remove(orderItem);
        }

        public void decrementItem(OrderItem orderItem) {
            if (status != OrderStatus.OPEN) return;
            if (!orderItem.decrementQuantity()) items.remove(orderItem);
        }

        public void clearOrder() {
            if (status != OrderStatus.OPEN) return;
            items.clear();
        }

        public double getSubtotal() {
            double t = 0;
            for (OrderItem i : items) t += i.getLineTotal();
            return t;
        }

        public double getVatAmount() { return Math.round(getSubtotal() * VAT_RATE); }
        public double getGrandTotal() { return getSubtotal() + getVatAmount() + deliveryFee; }

        public int getTotalItems() {
            int c = 0;
            for (OrderItem i : items) c += i.getQuantity();
            return c;
        }

        public boolean isEmpty() { return items.isEmpty(); }
        public void place() { status = OrderStatus.PLACED; }
        public void confirm() { status = OrderStatus.CONFIRMED;  }
        public void startPrep(){ status = OrderStatus.PREPARING;  }
        public void markReady(){ status = OrderStatus.READY; }
        public void markDelivered(){ status = OrderStatus.DELIVERED;  }
        public void markPaid() { status = OrderStatus.PAID; }
        public void cancel(){ status = OrderStatus.CANCELLED;  }

        public String getOrderId() { return orderId; }
        public OrderType getOrderType() { return orderType; }
        public OrderMode  getOrderMode() { return orderMode; }
        public OrderStatus getStatus() { return status; }
        public ArrayList<OrderItem> getItems() { return items; }
        public Customer getCustomer() { return customer; }
        public DeliveryAddress getDeliveryAddress() { return deliveryAddress; }
        public int getDeliveryFee() { return deliveryFee; }
        public LocalDateTime  getCreatedAt() { return createdAt;  }

        public void setOrderMode(OrderMode m) { this.orderMode = m; }
        public void setCustomer(Customer c) { this.customer = c; }
        public void setDeliveryAddress(DeliveryAddress a){ this.deliveryAddress = a; }
        public void setDeliveryFee(int fee) { this.deliveryFee = fee; }
    }

    class Receipt {

        private static int receiptCounter = 0;
        private String  receiptId;
        private Order  order;
        private PaymentMethod paymentMethod;
        private String transactionCode;
        private LocalDateTime issuedAt;

        public Receipt(Order order, PaymentMethod method, String txCode) {
            receiptCounter++;
            this.receiptId = String.format("RCP-%04d", receiptCounter);
            this.order = order;
            this.paymentMethod = method;
            this.transactionCode = txCode;
            this.issuedAt = LocalDateTime.now();
        }

        public String getFullReceipt() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            StringBuilder sb = new StringBuilder();

            sb.append("ManuEats\n");
            sb.append("Order Smart, Eat Well\n");
            sb.append(String.format(" %-16s %s\n", "Receipt:",receiptId));
            sb.append(String.format("%-16s %s\n", "Order:", order.getOrderId()));
            sb.append(String.format("%-16s %s\n", "Type:",
                    order.getOrderType() + " — " + order.getOrderMode()));
            sb.append(String.format("  %-16s %s\n", "Date:", issuedAt.format(fmt)));

            if (order.getCustomer() != null) {
                sb.append(String.format("%-16s %s\n", "Customer:",
                        order.getCustomer().getName()));
                sb.append(String.format("%-16s %s\n", "Phone:",
                        order.getCustomer().getPhone()));
            }
            if (order.getDeliveryAddress() != null) {
                sb.append(String.format("  %-16s %s\n", "Deliver to:",
                        order.getDeliveryAddress().toString()));
            }

            for (OrderItem item : order.getItems()) {
                sb.append(String.format("%-26s KES %,.0f\n",
                        item.getQuantity() + "x " + item.getMenuItem().getName(),
                        item.getLineTotal()));
            }

            sb.append(String.format("%-26s KES %,.0f\n", "Subtotal:", order.getSubtotal()));
            sb.append(String.format("%-26s KES %,.0f\n", "VAT (16%):", order.getVatAmount()));

            if (order.getDeliveryFee() > 0) {
                sb.append(String.format("%-26s KES %d\n", "Delivery:", order.getDeliveryFee()));
            }

            sb.append(String.format("%-26s KES %,.0f\n", "TOTAL:", order.getGrandTotal()));
            sb.append(String.format("%-16s %s\n", "Payment:",  paymentMethod));
            sb.append(String.format("%-16s %s\n", "Ref:", transactionCode));
            sb.append("Thank you, Enjoy your meal! \n");
            sb.append("Powered by ManuEats \n");
            return sb.toString();
        }

        public String  getReceiptId() { return receiptId; }
        public Order getOrder() { return order; }
        public String getTransactionCode() { return transactionCode; }
        public PaymentMethod getPaymentMethod()   { return paymentMethod; }
    }





    class MenuService {

        private ArrayList<MenuItem> allItems;

        public MenuService() {
            allItems = new ArrayList<>();
            loadMenu();
        }

        private void loadMenu() {

            add("1/4 Grilled Chicken", 550, FoodCategory.CHICKEN, "Flame-grilled quarter chicken, peri peri sauce");
            add("1/2 Grilled Chicken", 950, FoodCategory.CHICKEN, "Flame-grilled half chicken");
            add("Whole Grilled Chicken",1800, FoodCategory.CHICKEN, "Full grilled chicken feeds 2-3");
            add("Chicken Wings 6pc", 620, FoodCategory.CHICKEN, "Spicy grilled wings");
            add("Chicken Wings 12pc",1150, FoodCategory.CHICKEN, "Family wings platter");
            add("Fried Chicken 2pc",380, FoodCategory.CHICKEN, "Crispy fried chicken pieces");
            add("Fried Chicken 3pc",540, FoodCategory.CHICKEN, "Three crispy pieces");
            add("Chicken Strips",420, FoodCategory.CHICKEN, "Crispy chicken strips with dip");
            add("Chicken Livers",320, FoodCategory.CHICKEN, "Spicy peri peri chicken livers");
            add("Chicken Nuggets 6pc",280, FoodCategory.CHICKEN, "Golden chicken nuggets");

            add("Ugali + Beef Stew",  350, FoodCategory.KENYAN,  "Soft ugali with slow-cooked beef stew");
            add("Ugali + Chicken",380, FoodCategory.KENYAN,  "Soft ugali with grilled chicken");
            add("Ugali + Fish", 420, FoodCategory.KENYAN,  "Ugali with tilapia or omena");
            add("Ugali + Sukuma Wiki",250, FoodCategory.KENYAN,  "Ugali with sauteed sukuma wiki");
            add("Pilau (Regular)",280, FoodCategory.KENYAN,  "Spiced pilau rice with beef");
            add("Pilau (Large)", 420, FoodCategory.KENYAN,  "Large portion pilau rice with beef");
            add("Pilau + Kachumbari",320, FoodCategory.KENYAN,  "Pilau with fresh kachumbari salsa");
            add("Plain Rice + Stew",280, FoodCategory.KENYAN,  "Steamed rice with beef or chicken stew");
            add("Mukimo + Beef",350, FoodCategory.KENYAN,  "Mashed potato-peas-corn with beef");
            add("Githeri Special",250, FoodCategory.KENYAN,  "Boiled maize and beans with veggies");
            add("Matumbo Fry",300, FoodCategory.KENYAN,  "Fried matumbo with onions and spices");
            add("Mbuzi Curry", 480, FoodCategory.KENYAN,  "Slow-cooked goat curry with rice");
            add("Viazi Karai", 180, FoodCategory.KENYAN,  "Crispy fried potatoes with spice coating");
            add("Chapati 1pc", 60, FoodCategory.KENYAN,  "Soft layered chapati");
            add("Chapati 3pc", 160, FoodCategory.KENYAN,  "Three soft chapatis");

            add("Nyama Choma 250g",550, FoodCategory.GRILLS,  "Kenyan-style roasted goat or beef");
            add("Nyama Choma 500g",980, FoodCategory.GRILLS,  "Half kilo roasted meat");
            add("Beef Ribs Half Rack",850, FoodCategory.GRILLS,  "Slow-cooked beef ribs, BBQ glaze");
            add("Beef Steak 200g",680, FoodCategory.GRILLS,  "Grilled beef steak with chips");
            add("Beef Sausages 3pc", 320, FoodCategory.GRILLS,  "Grilled beef sausages");
            add("Mixed Grill Platter",1400, FoodCategory.GRILLS,  "Chicken, beef, sausage + 2 sides");
            add("Smokies 3pc",220, FoodCategory.GRILLS,  "Grilled smokie sausages");
            add("Mutura",200, FoodCategory.GRILLS,  "Kenyan-style blood sausage");

            add("Margherita Regular",650, FoodCategory.PIZZA,   "Classic tomato + mozzarella");
            add("Margherita Large",1050, FoodCategory.PIZZA,   "Classic tomato + mozzarella");
            add("BBQ Chicken Regular",850, FoodCategory.PIZZA,   "BBQ sauce, chicken, peppers");
            add("BBQ Chicken Large",1350, FoodCategory.PIZZA,   "BBQ sauce, chicken, peppers");
            add("Nyama Choma Pizza Reg",950, FoodCategory.PIZZA,   "Kenyan special — nyama choma topping");
            add("Nyama Choma Pizza Large",1550, FoodCategory.PIZZA,   "Kenyan special — nyama choma topping");
            add("Pepperoni Regular", 900, FoodCategory.PIZZA,   "Double pepperoni");
            add("Pepperoni Large",1450, FoodCategory.PIZZA,   "Double pepperoni");
            add("Veggie Supreme Regular",750, FoodCategory.PIZZA,   "Garden vegetables");
            add("Meat Feast Regular",980, FoodCategory.PIZZA,   "All the meats");
            add("Meat Feast Large",1580, FoodCategory.PIZZA,   "All the meats");

            // ── BURGERS ───────────────────────────────────────────────
            add("Classic Beef Burger",        450, FoodCategory.BURGERS, "Beef patty, lettuce, tomato, cheese");
            add("Double Beef Burger",         620, FoodCategory.BURGERS, "Double beef patty");
            add("Crispy Chicken Burger",      420, FoodCategory.BURGERS, "Crispy fried chicken fillet");
            add("Grilled Chicken Burger",     440, FoodCategory.BURGERS, "Flame-grilled chicken fillet");
            add("Spicy Beef Burger",          480, FoodCategory.BURGERS, "Beef patty with jalapeños");
            add("Veggie Burger",              380, FoodCategory.BURGERS, "Plant-based patty");
            add("Club Sandwich",              520, FoodCategory.BURGERS, "Triple-decker club sandwich");
            add("Chicken Wrap",               400, FoodCategory.BURGERS, "Grilled chicken in a tortilla wrap");

            // ── BAKERY ────────────────────────────────────────────────
            add("Chocolate Cake Slice", 280, FoodCategory.BAKERY,  "Rich moist chocolate cake");
            add("Carrot Cake Slice", 260, FoodCategory.BAKERY,  "Spiced carrot cake with cream cheese");
            add("Red Velvet Slice", 300, FoodCategory.BAKERY,  "Classic red velvet with frosting");
            add("Banana Cake Slice", 240, FoodCategory.BAKERY,  "Moist banana cake");
            add("Whole Chocolate Cake",2200, FoodCategory.BAKERY,  "Full 8-inch chocolate cake");
            add("Whole Carrot Cake", 2000, FoodCategory.BAKERY,  "Full 8-inch carrot cake");
            add("Custom Celebration Cake", 3500, FoodCategory.BAKERY,  "Order 24hrs ahead — customisable");
            add("Mandazi 3pc",80, FoodCategory.BAKERY,  "Classic Kenyan mandazi");
            add("Mahamri 3pc", 90, FoodCategory.BAKERY,  "Swahili-style coconut doughnuts");
            add("Scones 2pc", 120, FoodCategory.BAKERY,  "Buttery plain or fruit scones");
            add("Croissant",180, FoodCategory.BAKERY,  "Butter croissant");
            add("Garlic Bread",150, FoodCategory.BAKERY,  "Toasted garlic butter bread");
            add("Cheese Scone",140, FoodCategory.BAKERY,  "Savoury cheese scone");

            add("Plain Yoghurt 200ml",120, FoodCategory.DAIRY,   "Fresh plain yoghurt");
            add("Strawberry Yoghurt 200ml",140, FoodCategory.DAIRY,   "Strawberry flavoured yoghurt");
            add("Mango Yoghurt 200ml",140, FoodCategory.DAIRY,   "Mango flavoured yoghurt");
            add("Mixed Berry Yoghurt 200ml",  140, FoodCategory.DAIRY,   "Mixed berry yoghurt");
            add("Yoghurt Parfait",280, FoodCategory.DAIRY,   "Layered yoghurt, granola, fruits");
            add("Vanilla Milkshake", 280, FoodCategory.DAIRY,   "Thick vanilla milkshake");
            add("Chocolate Milkshake",280, FoodCategory.DAIRY,   "Thick chocolate milkshake");
            add("Strawberry Milkshake",280, FoodCategory.DAIRY,   "Thick strawberry milkshake");
            add("Mango Smoothie",300, FoodCategory.DAIRY,   "Fresh mango blended smooth");
            add("Mixed Fruit Smoothie",320, FoodCategory.DAIRY,   "Seasonal fruit blend");
            add("Lassi (Sweet)",180, FoodCategory.DAIRY,   "Indian-inspired yoghurt drink");

            add("Chips Regular",180, FoodCategory.SIDES,"Crispy golden chips");
            add("Chips Large",260, FoodCategory.SIDES,"Large portion crispy chips");
            add("Onion Rings",200, FoodCategory.SIDES,"Crispy battered onion rings");
            add("Coleslaw", 80, FoodCategory.SIDES,"Creamy coleslaw");
            add("Kachumbari",80, FoodCategory.SIDES,"Fresh tomato-onion-coriander salsa");
            add("Garden Salad",180, FoodCategory.SIDES,"Mixed greens, tomato, cucumber");
            add("Steamed Rice",150, FoodCategory.SIDES,"Plain steamed rice");
            add("Mashed Potatoes",150, FoodCategory.SIDES,"Creamy buttered mash");
            add("Garlic Naan",160, FoodCategory.SIDES,"Soft garlic naan bread");
            add("Pasta Salad",220, FoodCategory.SIDES,"Cold pasta with vegetables");

            add("Coke 500ml",80, FoodCategory.DRINKS,"Coca-Cola");
            add("Fanta Orange 500ml", 80, FoodCategory.DRINKS,"Fanta Orange");
            add("Sprite 500ml",80, FoodCategory.DRINKS, "Sprite");
            add("Stoney 500ml",80, FoodCategory.DRINKS,"Stoney Tangawizi");
            add("Water 500ml",50, FoodCategory.DRINKS,"Mineral water");
            add("Water 1 Litre",80, FoodCategory.DRINKS,"Mineral water litre");
            add("Fresh Orange Juice",220, FoodCategory.DRINKS,"Freshly squeezed OJ");
            add("Fresh Passion Juice",220, FoodCategory.DRINKS,"Fresh passion fruit juice");
            add("Mango Juice 300ml",150, FoodCategory.DRINKS,"Mango fruit juice");
            add("Masala Tea",100, FoodCategory.DRINKS,"Spiced Kenyan masala chai");
            add("Black Tea",60, FoodCategory.DRINKS,"Classic Kenyan black tea");
            add("Americano",200, FoodCategory.DRINKS,"Black espresso coffee");
            add("Cappuccino",250, FoodCategory.DRINKS,"Espresso with steamed milk");
            add("Latte",270, FoodCategory.DRINKS,"Espresso with lots of steamed milk");
            add("Dawa Cocktail",350, FoodCategory.DRINKS,"Kenyan honey-lime non-alcoholic");

            add("Ice Cream 1 Scoop",120, FoodCategory.DESSERTS,"Vanilla, chocolate or strawberry");
            add("Ice Cream 2 Scoops",200, FoodCategory.DESSERTS,"Choose two flavours");
            add("Chocolate Brownie",250, FoodCategory.DESSERTS,"Warm fudge brownie");
            add("Cheesecake Slice",320, FoodCategory.DESSERTS,"New York cheesecake");
            add("Waffles + Syrup",380, FoodCategory.DESSERTS,"Belgian waffles, maple syrup");
            add("Waffles + Ice Cream",450, FoodCategory.DESSERTS,"Belgian waffles with 2 scoops");
            add("Fruit Salad",200, FoodCategory.DESSERTS,"Seasonal fresh fruits");
            add("Bread Pudding",220, FoodCategory.DESSERTS,"Warm bread pudding, custard");
            add("Doughnut 2pc",150, FoodCategory.DESSERTS,"Glazed or chocolate doughnuts");
        }

        private void add(String name, double price,
                         FoodCategory cat, String desc) {
            allItems.add(new MenuItem(name, price, cat, desc));
        }

        public ArrayList<MenuItem> getByCategory(FoodCategory category) {
            if (category == FoodCategory.ALL) return allItems;
            ArrayList<MenuItem> filtered = new ArrayList<>();
            for (MenuItem item : allItems) {
                if (item.getCategory() == category) filtered.add(item);
            }
            return filtered;
        }

        public ArrayList<MenuItem> search(String query) {
            ArrayList<MenuItem> results = new ArrayList<>();
            String lower = query.toLowerCase().trim();
            for (MenuItem item : allItems) {
                if (item.getName().toLowerCase().contains(lower)
                        || item.getDescription().toLowerCase().contains(lower)) {
                    results.add(item);
                }
            }
            return results;
        }

        public ArrayList<MenuItem> getAllItems() { return allItems; }
    }

    class DeliveryService {
        private ArrayList<DeliveryZone> zones;
        private static final int DEFAULT_FEE = 350;

        public DeliveryService() {
            zones = new ArrayList<>();
            loadZones();
        }

        private void loadZones() {
            DeliveryZone z1 = new DeliveryZone("Zone 1 — CBD", 100);
            z1.addArea("cbd"); z1.addArea("tom mboya"); z1.addArea("moi avenue");
            z1.addArea("river road"); z1.addArea("ngara"); z1.addArea("city centre");
            z1.addArea("downtown"); z1.addArea("haile selassie");
            zones.add(z1);

            DeliveryZone z2 = new DeliveryZone("Zone 2 — Inner Suburbs", 200);
            z2.addArea("westlands"); z2.addArea("parklands"); z2.addArea("upper hill");
            z2.addArea("hurlingham"); z2.addArea("south b"); z2.addArea("south c");
            z2.addArea("kilimani"); z2.addArea("lavington"); z2.addArea("kileleshwa");
            z2.addArea("milimani"); z2.addArea("ngong road"); z2.addArea("upperhill");
            z2.addArea("valley arcade"); z2.addArea("adams arcade");
            zones.add(z2);

            DeliveryZone z3 = new DeliveryZone("Zone 3 — Middle Suburbs", 300);
            z3.addArea("karen"); z3.addArea("langata"); z3.addArea("gigiri");
            z3.addArea("runda"); z3.addArea("kasarani"); z3.addArea("thika road");
            z3.addArea("ruaraka"); z3.addArea("garden city"); z3.addArea("muthaiga");
            z3.addArea("spring valley"); z3.addArea("rosslyn"); z3.addArea("loresho");
            zones.add(z3);

            DeliveryZone z4 = new DeliveryZone("Zone 4 — Outer Nairobi", 450);
            z4.addArea("rongai"); z4.addArea("ruaka"); z4.addArea("ruiru");
            z4.addArea("kitengela"); z4.addArea("ngong"); z4.addArea("athi river");
            z4.addArea("syokimau"); z4.addArea("mlolongo"); z4.addArea("embakasi");
            z4.addArea("pipeline"); z4.addArea("donholm"); z4.addArea("buruburu");
            zones.add(z4);
        }

        public int getFeeForArea(String area) {
            for (DeliveryZone zone : zones) {
                if (zone.containsArea(area)) return zone.getDeliveryFee();
            }
            return DEFAULT_FEE;
        }

        public String getZoneForArea(String area) {
            for (DeliveryZone zone : zones) {
                if (zone.containsArea(area)) return zone.toString();
            }
            return "Outside zones — KES " + DEFAULT_FEE;
        }

        public ArrayList<DeliveryZone> getZones() { return zones; }
    }

    class OrderService {
        private Order currentOrder;
        private ArrayList<Receipt> completedReceipts;

        public OrderService() {
            this.currentOrder = null;
            this.completedReceipts = new ArrayList<>();
        }

        public Order startInPersonOrder() {
            currentOrder = new Order();
            return currentOrder;
        }

        public Order startOnlineOrder(OrderMode mode, Customer customer) {
            currentOrder = new Order(OrderType.ONLINE, mode, customer);
            return currentOrder;
        }

        public void addItem(MenuItem item) {
            if (currentOrder != null) currentOrder.addItem(item);
        }
        public void removeItem(OrderItem orderItem) {
            if (currentOrder != null) currentOrder.removeItem(orderItem);
        }
        public void decrementItem(OrderItem orderItem) {
            if (currentOrder != null) currentOrder.decrementItem(orderItem);
        }
        public void clearOrder() {
            if (currentOrder != null) currentOrder.clearOrder();
        }
        public Receipt checkout(PaymentMethod method, String txCode) {
            if (currentOrder == null || currentOrder.isEmpty()) return null;
            currentOrder.confirm();
            currentOrder.markPaid();
            Receipt receipt = new Receipt(currentOrder, method, txCode);
            completedReceipts.add(receipt);
            currentOrder = null;
            return receipt;
        }

        public Order getCurrentOrder() { return currentOrder; }
        public ArrayList<Receipt> getCompletedReceipts() { return completedReceipts; }
        public boolean hasActiveOrder() { return currentOrder != null; }
    }

    class PaymentService {
        public boolean simulateMPesa(String phone, double amount) {
            System.out.println("MPesa STK => " + phone + " | KES " + (int) amount);
            return Math.random() < 0.90;
        }

        public String generateCode() {
            String c = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++)
                sb.append(c.charAt((int)(Math.random() * c.length())));
            return sb.toString();
        }
    }


    class PaymentDialog extends Dialog implements ActionListener {

        private Order  order;
        private PaymentService paymentService;
        private TextField phoneField;
        private TextField cashField;
        private Button mpesaBtn, cashBtn, cardBtn;
        private Button confirmBtn, cancelBtn;
        private Label statusLabel;
        private TextArea summaryArea;
        private PaymentMethod  selectedMethod  = PaymentMethod.MPESA;
        private String transactionCode = null;
        private boolean  paid = false;

        public PaymentDialog(Frame parent, Order order, PaymentService ps) {
            super(parent, "Checkout — ManuEats", true);
            this.order = order;
            this.paymentService = ps;
            buildUI();
            setSize(460, 560);
            setLocationRelativeTo(parent);
            setResizable(false);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(8, 8));
            setBackground(new Color(252, 252, 250));

            Panel hdr = new Panel();
            hdr.setBackground(new Color(220, 80, 20));
            Label htl = new Label("  ManuEats — Checkout", Label.LEFT);
            htl.setFont(new Font("SansSerif", Font.BOLD, 15));
            htl.setForeground(Color.WHITE); htl.setBackground(new Color(220, 80, 20));
            hdr.add(htl); add(hdr, BorderLayout.NORTH);

            Panel center = new Panel(new GridBagLayout());
            center.setBackground(new Color(252, 252, 250));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 14, 5, 14);
            c.fill = GridBagConstraints.HORIZONTAL;
            int row = 0;

            c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
            summaryArea = new TextArea("", 10, 38, TextArea.SCROLLBARS_NONE);
            summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            summaryArea.setEditable(false);
            summaryArea.setBackground(new Color(245, 252, 245));
            summaryArea.setText(buildSummary());
            center.add(summaryArea, c);

            c.gridy = row++;
            Label div = new Label("--");
            div.setForeground(new Color(200, 200, 200)); center.add(div, c);

            c.gridy = row++;
            Label ml = new Label("Payment method:");
            ml.setFont(new Font("SansSerif", Font.BOLD, 12)); center.add(ml, c);

            c.gridy = row++;
            Panel mRow = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            mRow.setBackground(new Color(252, 252, 250));
            mpesaBtn = mBtn("MPesa", new Color(0, 160, 70));
            cashBtn = mBtn("Cash",  new Color(140, 100, 0));
            cardBtn = mBtn("Card",  new Color(40, 80, 180));
            mpesaBtn.addActionListener(this); cashBtn.addActionListener(this);
            cardBtn.addActionListener(this);
            mRow.add(mpesaBtn); mRow.add(cashBtn); mRow.add(cardBtn);
            center.add(mRow, c);

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            Label pl = new Label("MPesa Phone:"); pl.setFont(new Font("SansSerif", Font.BOLD, 12));
            center.add(pl, c); c.gridx = 1;
            String pre = (order.getCustomer() != null) ? order.getCustomer().getPhone() : "07";
            phoneField = new TextField(pre);
            phoneField.setBackground(new Color(255, 255, 230)); center.add(phoneField, c); row++;

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            Label cl = new Label("Cash tendered:"); cl.setFont(new Font("SansSerif", Font.BOLD, 12));
            center.add(cl, c); c.gridx = 1;
            cashField = new TextField(String.valueOf((int) order.getGrandTotal()));
            cashField.setBackground(new Color(255, 255, 230));
            cashField.setEnabled(false); center.add(cashField, c); row++;

            c.gridy = row; c.gridx = 0; c.gridwidth = 2;
            statusLabel = new Label("  Ready to process payment.", Label.LEFT);
            statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            statusLabel.setForeground(new Color(0, 100, 160)); center.add(statusLabel, c);
            add(center, BorderLayout.CENTER);

            Panel bot = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            bot.setBackground(new Color(252, 252, 250));
            confirmBtn = mkBtn("CONFIRM PAYMENT", new Color(0, 140, 60));
            cancelBtn  = mkBtn("CANCEL",          new Color(180, 50, 50));
            confirmBtn.addActionListener(this); cancelBtn.addActionListener(this);
            bot.add(confirmBtn); bot.add(cancelBtn);
            add(bot, BorderLayout.SOUTH);

            highlight(mpesaBtn);
        }

        private String buildSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("ManuEats — " + order.getOrderType()
                    + " / " + order.getOrderMode() + "\n");
            sb.append("Order: " + order.getOrderId() + "\n");
            if (order.getCustomer() != null)
                sb.append("Customer: " + order.getCustomer().getName() + "\n");
            sb.append("----\n");
            for (OrderItem item : order.getItems()) {
                sb.append(String.format("%-24s KES %,.0f\n",
                        item.getQuantity() + "x " + item.getMenuItem().getName(),
                        item.getLineTotal()));
            }
            sb.append("----\n");
            sb.append(String.format("%-24s KES %,.0f\n", "Subtotal:", order.getSubtotal()));
            sb.append(String.format("%-24s KES %,.0f\n", "VAT (16%):", order.getVatAmount()));
            if (order.getDeliveryFee() > 0)
                sb.append(String.format("%-24s KES %d\n", "Delivery:", order.getDeliveryFee()));
            sb.append(String.format("%-24s KES %,.0f\n", "TOTAL DUE:", order.getGrandTotal()));
            return sb.toString();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.equals("MPesa")) selectMethod(PaymentMethod.MPESA, mpesaBtn);
            else if (cmd.equals("Cash")) selectMethod(PaymentMethod.CASH,  cashBtn);
            else if (cmd.equals("Card")) selectMethod(PaymentMethod.CARD,  cardBtn);
            else if (cmd.equals("CONFIRM PAYMENT")) confirmPayment();
            else if (cmd.equals("CANCEL")) dispose();
        }

        private void selectMethod(PaymentMethod method, Button btn) {
            selectedMethod = method;
            phoneField.setEnabled(method == PaymentMethod.MPESA);
            cashField.setEnabled(method == PaymentMethod.CASH);
            highlight(btn);
            String hint = method == PaymentMethod.MPESA ? "Enter customer MPesa number."
                    : method == PaymentMethod.CASH  ? "Enter cash amount received."
                    : "Swipe or tap customer card.";
            setStatus(hint, new Color(0, 100, 160));
        }

        private void confirmPayment() {
            confirmBtn.setEnabled(false);
            if (selectedMethod == PaymentMethod.MPESA) {
                if (phoneField.getText().trim().length() < 9) {
                    setStatus("Enter a valid phone number.", new Color(180, 0, 0));
                    confirmBtn.setEnabled(true); return;
                }
                boolean ok = paymentService.simulateMPesa(
                        phoneField.getText().trim(), order.getGrandTotal());
                if (ok) {
                    transactionCode = paymentService.generateCode();
                    setStatus("CONFIRMED — " + transactionCode, new Color(0, 130, 0));
                    paid = true; closeAfter(1500);
                } else {
                    setStatus("MPesa FAILED — retry or switch to Cash.", new Color(180, 0, 0));
                    confirmBtn.setEnabled(true);
                }
            } else if (selectedMethod == PaymentMethod.CASH) {
                double tendered = parseCash();
                double change   = tendered - order.getGrandTotal();
                transactionCode = "CASH";
                setStatus("Received. Change: KES " + Math.max(0, (int) change),
                        new Color(0, 130, 0));
                paid = true; closeAfter(1500);
            } else {
                transactionCode = "CARD-" + paymentService.generateCode().substring(0, 6);
                setStatus("Card approved — " + transactionCode, new Color(0, 130, 0));
                paid = true; closeAfter(1500);
            }
        }

        private double parseCash() {
            try { return Double.parseDouble(cashField.getText().trim()); }
            catch (NumberFormatException ex) { return order.getGrandTotal(); }
        }

        private void closeAfter(int ms) {
            new Thread(() -> {
                try { Thread.sleep(ms); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                dispose();
            }).start();
        }

        private void highlight(Button active) {
            for (Button b : new Button[]{mpesaBtn, cashBtn, cardBtn})
                b.setFont(new Font("SansSerif",
                        b == active ? Font.BOLD : Font.PLAIN, 12));
        }

        private void setStatus(String t, Color c) {
            statusLabel.setText("  " + t); statusLabel.setForeground(c);
        }

        private Button mkBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE); b.setFont(new Font("SansSerif", Font.BOLD, 13));
            return b;
        }

        private Button mBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE); b.setFont(new Font("SansSerif", Font.BOLD, 12));
            return b;
        }

        public boolean isPaid() { return paid; }
        public String getTransactionCode(){ return transactionCode; }
        public PaymentMethod getMethod() { return selectedMethod;  }
    }

    class InPersonPanel extends Panel implements ActionListener {

        private MenuService menuService;
        private OrderService orderService;
        private PaymentService paymentService;
        private Frame parentFrame;

        private FoodCategory currentCategory = FoodCategory.ALL;

        private TextField searchField;
        private Panel categoryPanel;
        private Panel menuGridPanel;
        private java.awt.List  orderList;
        private Label subtotalLbl, vatLbl, totalLbl, countLbl;
        private Button checkoutBtn, clearBtn, removeBtn, minusBtn;
        private TextArea receiptArea;

        private static final Color BRAND  = new Color(220, 80, 20);
        private static final Color BRAND2 = new Color(240, 100, 30);

        public InPersonPanel(Frame parent, MenuService ms,
                             OrderService os, PaymentService ps) {
            this.parentFrame = parent;
            this.menuService = ms;
            this.orderService = os;
            this.paymentService = ps;

            setLayout(new BorderLayout(4, 4));
            setBackground(new Color(245, 244, 240));
            buildUI();
            orderService.startInPersonOrder();
        }

        private void buildUI() {
            Panel hdr = new Panel(new GridLayout(2, 1));
            hdr.setBackground(BRAND); hdr.setPreferredSize(new Dimension(0, 50));
            Label h = new Label("  ManuEats  —  IN-PERSON ORDER", Label.LEFT);
            h.setFont(new Font("SansSerif", Font.BOLD, 18));
            h.setForeground(Color.WHITE); h.setBackground(BRAND);
            Label ht = new Label("  Walk-in • Cashier POS", Label.LEFT);
            ht.setFont(new Font("SansSerif", Font.PLAIN, 11));
            ht.setForeground(new Color(255, 210, 190)); ht.setBackground(BRAND);
            hdr.add(h); hdr.add(ht);
            add(hdr, BorderLayout.NORTH);

            Panel center = new Panel(new BorderLayout(4, 4));
            center.setBackground(new Color(245, 244, 240));

            Panel topBar = new Panel(new GridLayout(2, 1, 0, 2));
            topBar.setBackground(new Color(245, 244, 240));

            Panel sRow = new Panel(new BorderLayout(4, 0));
            sRow.setBackground(new Color(245, 244, 240));
            Label sLbl = new Label("Search: "); sLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            searchField = new TextField();
            searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
            searchField.addTextListener(e -> onSearch());
            Button clrS = new Button("X");
            clrS.addActionListener(e -> { searchField.setText(""); refreshGrid(); });
            sRow.add(sLbl, BorderLayout.WEST);
            sRow.add(searchField, BorderLayout.CENTER);
            sRow.add(clrS, BorderLayout.EAST);

            categoryPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            categoryPanel.setBackground(new Color(245, 244, 240));
            buildCategoryTabs();

            topBar.add(sRow); topBar.add(categoryPanel);
            center.add(topBar, BorderLayout.NORTH);

            menuGridPanel = new Panel(new GridLayout(0, 3, 5, 5));
            menuGridPanel.setBackground(new Color(245, 244, 240));
            Panel gWrap = new Panel(new BorderLayout());
            gWrap.setBackground(new Color(245, 244, 240));
            gWrap.add(menuGridPanel, BorderLayout.NORTH);
            ScrollPane scroll = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
            scroll.add(gWrap); center.add(scroll, BorderLayout.CENTER);
            add(center, BorderLayout.CENTER);

            Panel right = buildOrderPanel();
            add(right, BorderLayout.EAST);

            Panel btm = new Panel(new BorderLayout(4, 4));
            btm.setBackground(new Color(245, 244, 240));
            Label rLbl = new Label("  RECEIPT"); rLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            btm.add(rLbl, BorderLayout.NORTH);
            receiptArea = new TextArea("", 7, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
            receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            receiptArea.setEditable(false);
            receiptArea.setBackground(new Color(245, 252, 245));
            btm.add(receiptArea, BorderLayout.CENTER);
            add(btm, BorderLayout.SOUTH);

            refreshGrid();
        }

        private Panel buildOrderPanel() {
            Panel right = new Panel(new BorderLayout(4, 4));
            right.setBackground(new Color(250, 252, 250));
            right.setPreferredSize(new Dimension(270, 0));
            Label ol = new Label("  CURRENT ORDER");
            ol.setFont(new Font("SansSerif", Font.BOLD, 12));
            ol.setForeground(BRAND); right.add(ol, BorderLayout.NORTH);
            orderList = new java.awt.List(10, false);
            orderList.setFont(new Font("Monospaced", Font.PLAIN, 11));
            orderList.setBackground(new Color(255, 252, 248));
            right.add(orderList, BorderLayout.CENTER);

            Panel totals = new Panel(new GridBagLayout());
            totals.setBackground(new Color(250, 252, 250));
            GridBagConstraints tc = new GridBagConstraints();
            tc.insets = new Insets(3, 8, 3, 8); tc.fill = GridBagConstraints.HORIZONTAL;
            int r = 0;

            tc.gridy = r++; tc.gridx = 0; tc.gridwidth = 2;
            Panel mRow = new Panel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            mRow.setBackground(new Color(250, 252, 250));
            minusBtn  = sBtn("-1", new Color(150, 90, 0));
            removeBtn = sBtn("Remove", new Color(180, 50, 50));
            minusBtn.addActionListener(this); removeBtn.addActionListener(this);
            mRow.add(new Label("Selected: ")); mRow.add(minusBtn); mRow.add(removeBtn);
            totals.add(mRow, tc);

            tc.gridy = r++;
            Label dv = new Label("----");
            dv.setForeground(new Color(200,200,200)); totals.add(dv, tc);

            r = tRow(totals, tc, r, "Items:", countLbl = new Label("0", Label.RIGHT));
            r = tRow(totals, tc, r, "Subtotal:",subtotalLbl = new Label("KES 0", Label.RIGHT));
            r = tRow(totals, tc, r, "VAT (16%):", vatLbl = new Label("KES 0", Label.RIGHT));

            tc.gridy = r; tc.gridx = 0; tc.gridwidth = 1;
            Label tl = new Label("TOTAL:"); tl.setFont(new Font("SansSerif", Font.BOLD, 14));
            totals.add(tl, tc); tc.gridx = 1;
            totalLbl = new Label("KES 0", Label.RIGHT);
            totalLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            totalLbl.setForeground(BRAND); totals.add(totalLbl, tc); r++;

            tc.gridy = r++; tc.gridx = 0; tc.gridwidth = 2;
            checkoutBtn = mkBtn("CHECKOUT", new Color(0, 150, 60));
            checkoutBtn.addActionListener(this); totals.add(checkoutBtn, tc);

            tc.gridy = r;
            clearBtn = mkBtn("CLEAR ORDER", new Color(160, 160, 160));
            clearBtn.addActionListener(this); totals.add(clearBtn, tc);

            right.add(totals, BorderLayout.SOUTH);
            return right;
        }

        private void buildCategoryTabs() {
            categoryPanel.removeAll();
            Button allTab = catTab("ALL", true);
            allTab.addActionListener(e -> {
                currentCategory = FoodCategory.ALL;
                searchField.setText(""); refreshGrid(); highlightTab(allTab);
            });
            categoryPanel.add(allTab);

            for (FoodCategory cat : FoodCategory.values()) {
                if (cat == FoodCategory.ALL) continue;
                if (menuService.getByCategory(cat).isEmpty()) continue;
                Button tab = catTab(cat.name(), false);
                tab.addActionListener(e -> {
                    currentCategory = cat; searchField.setText("");
                    refreshGrid(); highlightTab(tab);
                });
                categoryPanel.add(tab);
            }
            categoryPanel.revalidate(); categoryPanel.repaint();
        }

        private Button catTab(String label, boolean active) {
            Button b = new Button(label);
            b.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 11));
            b.setBackground(active ? BRAND : new Color(220, 218, 215));
            b.setForeground(active ? Color.WHITE : new Color(50, 50, 50));
            return b;
        }

        private void highlightTab(Button active) {
            for (Component c : categoryPanel.getComponents()) {
                if (!(c instanceof Button)) continue;
                Button b = (Button) c;
                boolean on = (b == active);
                b.setBackground(on ? BRAND : new Color(220, 218, 215));
                b.setForeground(on ? Color.WHITE : new Color(50, 50, 50));
                b.setFont(new Font("SansSerif", on ? Font.BOLD : Font.PLAIN, 11));
            }
        }

        private void refreshGrid() {
            buildMenuBtns(menuService.getByCategory(currentCategory));
        }

        private void onSearch() {
            String q = searchField.getText().trim();
            menuGridPanel.removeAll();
            if (q.isEmpty()) { refreshGrid(); return; }
            ArrayList<MenuItem> results = menuService.search(q);
            if (results.isEmpty())
                menuGridPanel.add(new Label("  No items found for \"" + q + "\""));
            else buildMenuBtns(results);
            menuGridPanel.revalidate(); menuGridPanel.repaint(); validate();
        }

        private void buildMenuBtns(ArrayList<MenuItem> items) {
            menuGridPanel.removeAll();
            for (MenuItem item : items) {
                if (!item.isAvailable()) continue;
                Button btn = new Button(item.getName()
                        + "  KES " + (int) item.getPrice());
                btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
                btn.setBackground(new Color(255, 255, 252));
                btn.addActionListener(e -> {
                    orderService.addItem(item); refreshOrder();
                    btn.setBackground(new Color(255, 210, 180));
                    new Thread(() -> {
                        try { Thread.sleep(300); }
                        catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        btn.setBackground(new Color(255, 255, 252));
                    }).start();
                });
                menuGridPanel.add(btn);
            }
            menuGridPanel.revalidate(); menuGridPanel.repaint(); validate();
        }

        void refreshOrder() {
            orderList.removeAll();
            Order cur = orderService.getCurrentOrder();
            if (cur == null || cur.isEmpty()) {
                orderList.add("  (empty — tap items above to add)");
                subtotalLbl.setText("KES 0"); vatLbl.setText("KES 0");
                totalLbl.setText("KES 0"); countLbl.setText("0"); return;
            }
            for (OrderItem item : cur.getItems()) orderList.add(item.toOrderLine());
            subtotalLbl.setText(fmt(cur.getSubtotal()));
            vatLbl.setText(fmt(cur.getVatAmount()));
            totalLbl.setText(fmt(cur.getGrandTotal()));
            countLbl.setText(String.valueOf(cur.getTotalItems()));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand().trim()) {
                case "CHECKOUT": doCheckout();  break;
                case "CLEAR ORDER":  doClear();     break;
                case "-1": doDecrement(); break;
                case "Remove": doRemove();    break;
            }
        }

        private void doCheckout() {
            Order cur = orderService.getCurrentOrder();
            if (cur == null || cur.isEmpty()) {
                receiptArea.setText("Add items before checking out."); return;
            }
            PaymentDialog dlg = new PaymentDialog(parentFrame, cur, paymentService);
            dlg.setVisible(true);
            if (dlg.isPaid()) {
                Receipt r = orderService.checkout(dlg.getMethod(), dlg.getTransactionCode());
                if (r != null) {
                    receiptArea.setText(r.getFullReceipt());
                    orderService.startInPersonOrder();
                    refreshOrder(); refreshGrid();
                }
            }
        }

        private void doClear() {
            orderService.clearOrder(); refreshOrder();
            receiptArea.setText(" Order cleared.");
        }

        private void doDecrement() {
            int idx = orderList.getSelectedIndex();
            Order cur = orderService.getCurrentOrder();
            if (idx < 0 || cur == null || idx >= cur.getItems().size()) return;
            orderService.decrementItem(cur.getItems().get(idx)); refreshOrder();
        }

        private void doRemove() {
            int idx = orderList.getSelectedIndex();
            Order cur = orderService.getCurrentOrder();
            if (idx < 0 || cur == null || idx >= cur.getItems().size()) return;
            orderService.removeItem(cur.getItems().get(idx)); refreshOrder();
        }

        private int tRow(Panel p, GridBagConstraints c,
                         int r, String lbl, Label val) {
            c.gridy = r; c.gridx = 0; c.gridwidth = 1;
            p.add(new Label(lbl), c); c.gridx = 1;
            val.setFont(new Font("SansSerif", Font.BOLD, 12)); p.add(val, c);
            return r + 1;
        }

        private String fmt(double v) { return String.format("KES %,.0f", v); }

        private Button mkBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setPreferredSize(new Dimension(200, 32));
            return b;
        }

        private Button sBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE); b.setFont(new Font("SansSerif", Font.BOLD, 11));
            return b;
        }
    }

    class OnlinePanel extends Panel implements ActionListener {

        private MenuService menuService;
        private OrderService orderService;
        private DeliveryService deliveryService;
        private PaymentService  paymentService;
        private Frame parentFrame;

        private FoodCategory    currentCategory = FoodCategory.ALL;

        private TextField custNameField, custPhoneField;
        private Choice modeChoice;
        private TextField streetField, areaField, landmarkField;
        private Label zoneLabel, feeLabel;
        private Panel deliveryPanel;
        private TextField searchField;
        private Panel categoryPanel, menuGridPanel;
        private java.awt.List orderList;
        private Label subtotalLbl, vatLbl, deliveryFeeLbl, totalLbl;
        private Button  checkoutBtn, clearBtn, removeBtn, minusBtn;
        private TextArea receiptArea;

        private static final Color BRAND = new Color(30, 80, 180);

        public OnlinePanel(Frame parent, MenuService ms, OrderService os,
                           DeliveryService ds, PaymentService ps) {
            this.parentFrame = parent;
            this.menuService = ms;
            this.orderService = os;
            this.deliveryService = ds;
            this.paymentService  = ps;

            setLayout(new BorderLayout(4, 4));
            setBackground(new Color(240, 245, 255));
            buildUI();
            orderService.startOnlineOrder(OrderMode.PICKUP, null);
        }

        private void buildUI() {
            Panel hdr = new Panel(new GridLayout(2, 1));
            hdr.setBackground(BRAND); hdr.setPreferredSize(new Dimension(0, 50));
            Label h = new Label("  ManuEats  —  ONLINE ORDER", Label.LEFT);
            h.setFont(new Font("SansSerif", Font.BOLD, 18));
            h.setForeground(Color.WHITE); h.setBackground(BRAND);
            Label ht = new Label("  Pickup & Delivery", Label.LEFT);
            ht.setFont(new Font("SansSerif", Font.PLAIN, 11));
            ht.setForeground(new Color(180, 210, 255)); ht.setBackground(BRAND);
            hdr.add(h); hdr.add(ht);
            add(hdr, BorderLayout.NORTH);

            Panel left = new Panel(new GridBagLayout());
            left.setBackground(new Color(245, 248, 255));
            left.setPreferredSize(new Dimension(220, 0));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 8, 4, 8); c.fill = GridBagConstraints.HORIZONTAL;
            int row = 0;

            c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
            Label cs = new Label("CUSTOMER INFO");
            cs.setFont(new Font("SansSerif", Font.BOLD, 12));
            cs.setForeground(BRAND); left.add(cs, c);

            row = addField(left, c, row, "Name:",  custNameField  = new TextField());
            row = addField(left, c, row, "Phone:", custPhoneField = new TextField("07"));

            c.gridy = row; c.gridx = 0; c.gridwidth = 1;
            left.add(boldLbl("Mode:"), c); c.gridx = 1;
            modeChoice = new Choice();
            modeChoice.add("PICKUP"); modeChoice.add("DELIVERY");
            modeChoice.addItemListener(e -> onModeChanged());
            left.add(modeChoice, c); row++;

            c.gridy = row++; c.gridx = 0; c.gridwidth = 2;
            deliveryPanel = new Panel(new GridBagLayout());
            deliveryPanel.setBackground(new Color(240, 245, 255));
            GridBagConstraints dc = new GridBagConstraints();
            dc.insets = new Insets(3, 0, 3, 0); dc.fill = GridBagConstraints.HORIZONTAL;
            int dr = 0;

            dc.gridy = dr++; dc.gridx = 0; dc.gridwidth = 2;
            Label dlvHdr = new Label("DELIVERY ADDRESS");
            dlvHdr.setFont(new Font("SansSerif", Font.BOLD, 11));
            dlvHdr.setForeground(BRAND); deliveryPanel.add(dlvHdr, dc);

            dr = addField(deliveryPanel, dc, dr, "Street:", streetField   = new TextField());
            dr = addField(deliveryPanel, dc, dr, "Area:",   areaField     = new TextField());
            dr = addField(deliveryPanel, dc, dr, "Near:",   landmarkField = new TextField("optional"));
            areaField.addTextListener(e -> updateDeliveryFee());

            dc.gridy = dr; dc.gridx = 0; dc.gridwidth = 1;
            deliveryPanel.add(new Label("Zone:"), dc); dc.gridx = 1;
            zoneLabel = new Label("—", Label.RIGHT);
            zoneLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            zoneLabel.setForeground(BRAND); deliveryPanel.add(zoneLabel, dc); dr++;

            dc.gridy = dr; dc.gridx = 0; dc.gridwidth = 1;
            deliveryPanel.add(new Label("Delivery:"), dc); dc.gridx = 1;
            feeLabel = new Label("KES 0", Label.RIGHT);
            feeLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            feeLabel.setForeground(new Color(180, 80, 0)); deliveryPanel.add(feeLabel, dc);

            deliveryPanel.setVisible(false);
            left.add(deliveryPanel, c);

            add(left, BorderLayout.WEST);

            Panel center = new Panel(new BorderLayout(4, 4));
            center.setBackground(new Color(240, 245, 255));

            Panel topBar = new Panel(new GridLayout(2, 1, 0, 2));
            topBar.setBackground(new Color(240, 245, 255));

            Panel sRow = new Panel(new BorderLayout(4, 0));
            sRow.setBackground(new Color(240, 245, 255));
            Label sLbl = new Label("Search: "); sLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            searchField = new TextField();
            searchField.addTextListener(e -> onSearch());
            Button clrS = new Button("X");
            clrS.addActionListener(e -> { searchField.setText(""); refreshGrid(); });
            sRow.add(sLbl, BorderLayout.WEST);
            sRow.add(searchField, BorderLayout.CENTER);
            sRow.add(clrS, BorderLayout.EAST);

            categoryPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            categoryPanel.setBackground(new Color(240, 245, 255));
            buildCategoryTabs();

            topBar.add(sRow); topBar.add(categoryPanel);
            center.add(topBar, BorderLayout.NORTH);

            menuGridPanel = new Panel(new GridLayout(0, 3, 5, 5));
            menuGridPanel.setBackground(new Color(240, 245, 255));
            Panel gWrap = new Panel(new BorderLayout());
            gWrap.setBackground(new Color(240, 245, 255));
            gWrap.add(menuGridPanel, BorderLayout.NORTH);
            ScrollPane scroll = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
            scroll.add(gWrap); center.add(scroll, BorderLayout.CENTER);
            add(center, BorderLayout.CENTER);

            add(buildOrderPanel(), BorderLayout.EAST);

            Panel btm = new Panel(new BorderLayout(4, 4));
            btm.setBackground(new Color(240, 245, 255));
            Label rLbl = new Label("  RECEIPT / ORDER CONFIRMATION");
            rLbl.setFont(new Font("SansSerif", Font.BOLD, 11)); btm.add(rLbl, BorderLayout.NORTH);
            receiptArea = new TextArea("", 7, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
            receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            receiptArea.setEditable(false);
            receiptArea.setBackground(new Color(245, 248, 255));
            btm.add(receiptArea, BorderLayout.CENTER);
            add(btm, BorderLayout.SOUTH);

            refreshGrid();
        }

        private Panel buildOrderPanel() {
            Panel right = new Panel(new BorderLayout(4, 4));
            right.setBackground(new Color(248, 250, 255));
            right.setPreferredSize(new Dimension(270, 0));
            Label ol = new Label("  ONLINE ORDER");
            ol.setFont(new Font("SansSerif", Font.BOLD, 12));
            ol.setForeground(BRAND); right.add(ol, BorderLayout.NORTH);
            orderList = new java.awt.List(10, false);
            orderList.setFont(new Font("Monospaced", Font.PLAIN, 11));
            orderList.setBackground(new Color(245, 248, 255));
            right.add(orderList, BorderLayout.CENTER);

            Panel totals = new Panel(new GridBagLayout());
            totals.setBackground(new Color(248, 250, 255));
            GridBagConstraints tc = new GridBagConstraints();
            tc.insets = new Insets(3, 8, 3, 8); tc.fill = GridBagConstraints.HORIZONTAL;
            int r = 0;

            tc.gridy = r++; tc.gridx = 0; tc.gridwidth = 2;
            Panel mRow = new Panel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            mRow.setBackground(new Color(248, 250, 255));
            minusBtn  = sBtn("-1", new Color(150, 90, 0));
            removeBtn = sBtn("Remove", new Color(180, 50, 50));
            minusBtn.addActionListener(this); removeBtn.addActionListener(this);
            mRow.add(new Label("Selected: ")); mRow.add(minusBtn); mRow.add(removeBtn);
            totals.add(mRow, tc);

            tc.gridy = r++;
            Label dv = new Label("----");
            dv.setForeground(new Color(200,200,200)); totals.add(dv, tc);

            r = tRow(totals, tc, r, "Subtotal:",  subtotalLbl    = new Label("KES 0", Label.RIGHT));
            r = tRow(totals, tc, r, "VAT (16%):", vatLbl         = new Label("KES 0", Label.RIGHT));
            r = tRow(totals, tc, r, "Delivery:",  deliveryFeeLbl = new Label("KES 0", Label.RIGHT));

            tc.gridy = r; tc.gridx = 0; tc.gridwidth = 1;
            Label tl = new Label("TOTAL:"); tl.setFont(new Font("SansSerif", Font.BOLD, 14));
            totals.add(tl, tc); tc.gridx = 1;
            totalLbl = new Label("KES 0", Label.RIGHT);
            totalLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            totalLbl.setForeground(BRAND); totals.add(totalLbl, tc); r++;

            tc.gridy = r++; tc.gridx = 0; tc.gridwidth = 2;
            checkoutBtn = mkBtn("PLACE & PAY ORDER", BRAND);
            checkoutBtn.addActionListener(this); totals.add(checkoutBtn, tc);

            tc.gridy = r;
            clearBtn = mkBtn("CLEAR ORDER", new Color(160, 160, 160));
            clearBtn.addActionListener(this); totals.add(clearBtn, tc);

            right.add(totals, BorderLayout.SOUTH);
            return right;
        }

        private void onModeChanged() {
            boolean isDel = modeChoice.getSelectedItem().equals("DELIVERY");
            deliveryPanel.setVisible(isDel);
            validate();
            refreshTotals();
        }

        private void updateDeliveryFee() {
            String area = areaField.getText().trim();
            Order cur = orderService.getCurrentOrder();
            if (area.isEmpty()) {
                zoneLabel.setText("—"); feeLabel.setText("KES 0");
                if (cur != null) cur.setDeliveryFee(0);
                refreshTotals(); return;
            }
            int fee = deliveryService.getFeeForArea(area);
            String zone = deliveryService.getZoneForArea(area);
            zoneLabel.setText(zone.length() > 22 ? zone.substring(0, 22) : zone);
            feeLabel.setText("KES " + fee);
            if (cur != null) cur.setDeliveryFee(fee);
            refreshTotals();
        }

        private void buildCategoryTabs() {
            categoryPanel.removeAll();
            Button allTab = catTab("ALL", true);
            allTab.addActionListener(e -> {
                currentCategory = FoodCategory.ALL; searchField.setText("");
                refreshGrid(); highlightTab(allTab);
            });
            categoryPanel.add(allTab);
            for (FoodCategory cat : FoodCategory.values()) {
                if (cat == FoodCategory.ALL) continue;
                if (menuService.getByCategory(cat).isEmpty()) continue;
                Button tab = catTab(cat.name(), false);
                tab.addActionListener(e -> {
                    currentCategory = cat; searchField.setText("");
                    refreshGrid(); highlightTab(tab);
                });
                categoryPanel.add(tab);
            }
            categoryPanel.revalidate(); categoryPanel.repaint();
        }

        private Button catTab(String label, boolean active) {
            Button b = new Button(label);
            b.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 11));
            b.setBackground(active ? BRAND : new Color(220, 225, 240));
            b.setForeground(active ? Color.WHITE : new Color(50, 50, 80));
            return b;
        }

        private void highlightTab(Button active) {
            for (Component c : categoryPanel.getComponents()) {
                if (!(c instanceof Button)) continue;
                Button b = (Button) c; boolean on = (b == active);
                b.setBackground(on ? BRAND : new Color(220, 225, 240));
                b.setForeground(on ? Color.WHITE : new Color(50, 50, 80));
                b.setFont(new Font("SansSerif", on ? Font.BOLD : Font.PLAIN, 11));
            }
        }

        private void refreshGrid() {
            menuGridPanel.removeAll();
            buildMenuBtns(menuService.getByCategory(currentCategory));
        }

        private void onSearch() {
            String q = searchField.getText().trim();
            menuGridPanel.removeAll();
            if (q.isEmpty()) { refreshGrid(); return; }
            ArrayList<MenuItem> results = menuService.search(q);
            if (results.isEmpty())
                menuGridPanel.add(new Label("  No items for \"" + q + "\""));
            else buildMenuBtns(results);
            menuGridPanel.revalidate(); menuGridPanel.repaint(); validate();
        }

        private void buildMenuBtns(ArrayList<MenuItem> items) {
            for (MenuItem item : items) {
                if (!item.isAvailable()) continue;
                Button btn = new Button(item.getName()
                        + "  KES " + (int) item.getPrice());
                btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
                btn.setBackground(new Color(252, 252, 255));
                btn.addActionListener(e -> {
                    orderService.addItem(item); refreshTotals();
                    btn.setBackground(new Color(180, 200, 255));
                    new Thread(() -> {
                        try { Thread.sleep(300); }
                        catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        btn.setBackground(new Color(252, 252, 255));
                    }).start();
                });
                menuGridPanel.add(btn);
            }
            menuGridPanel.revalidate(); menuGridPanel.repaint(); validate();
        }

        void refreshTotals() {
            orderList.removeAll();
            Order cur = orderService.getCurrentOrder();
            if (cur == null || cur.isEmpty()) {
                orderList.add("  (empty — tap items above to add)");
                subtotalLbl.setText("KES 0"); vatLbl.setText("KES 0");
                deliveryFeeLbl.setText("KES 0"); totalLbl.setText("KES 0"); return;
            }
            for (OrderItem item : cur.getItems()) orderList.add(item.toOrderLine());
            subtotalLbl.setText(fmt(cur.getSubtotal()));
            vatLbl.setText(fmt(cur.getVatAmount()));
            deliveryFeeLbl.setText("KES " + cur.getDeliveryFee());
            totalLbl.setText(fmt(cur.getGrandTotal()));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand().trim()) {
                case "PLACE & PAY ORDER": doCheckout();  break;
                case "CLEAR ORDER": doClear();     break;
                case "-1": doDecrement(); break;
                case "Remove": doRemove();    break;
            }
        }

        private void doCheckout() {
            String name  = custNameField.getText().trim();
            String phone = custPhoneField.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                receiptArea.setText("  Enter customer name and phone first."); return;
            }
            Order cur = orderService.getCurrentOrder();
            if (cur == null || cur.isEmpty()) {
                receiptArea.setText("  Add items before placing order."); return;
            }

            Customer customer = new Customer(name, phone);
            OrderMode mode = modeChoice.getSelectedItem().equals("DELIVERY")
                    ? OrderMode.DELIVERY : OrderMode.PICKUP;
            cur.setOrderMode(mode); cur.setCustomer(customer);

            if (mode == OrderMode.DELIVERY) {
                String street = streetField.getText().trim();
                String area   = areaField.getText().trim();
                if (street.isEmpty() || area.isEmpty()) {
                    receiptArea.setText("  Enter street and area for delivery."); return;
                }
                cur.setDeliveryAddress(new DeliveryAddress(
                        street, area, landmarkField.getText().trim()));
                cur.setDeliveryFee(deliveryService.getFeeForArea(area));
            }

            PaymentDialog dlg = new PaymentDialog(parentFrame, cur, paymentService);
            dlg.setVisible(true);

            if (dlg.isPaid()) {
                Receipt r = orderService.checkout(dlg.getMethod(), dlg.getTransactionCode());
                if (r != null) {
                    receiptArea.setText(r.getFullReceipt());
                    orderService.startOnlineOrder(OrderMode.PICKUP, null);
                    refreshTotals(); refreshGrid();
                    custNameField.setText(""); custPhoneField.setText("07");
                    streetField.setText(""); areaField.setText("");
                    landmarkField.setText("optional");
                    zoneLabel.setText("—"); feeLabel.setText("KES 0");
                }
            }
        }

        private void doClear() {
            orderService.clearOrder(); refreshTotals();
            receiptArea.setText("  Order cleared.");
        }

        private void doDecrement() {
            int idx = orderList.getSelectedIndex();
            Order cur = orderService.getCurrentOrder();
            if (idx < 0 || cur == null || idx >= cur.getItems().size()) return;
            orderService.decrementItem(cur.getItems().get(idx)); refreshTotals();
        }

        private void doRemove() {
            int idx = orderList.getSelectedIndex();
            Order cur = orderService.getCurrentOrder();
            if (idx < 0 || cur == null || idx >= cur.getItems().size()) return;
            orderService.removeItem(cur.getItems().get(idx)); refreshTotals();
        }

        private int addField(Panel p, GridBagConstraints c,
                             int r, String lbl, TextField tf) {
            c.gridy = r; c.gridx = 0; c.gridwidth = 1;
            p.add(boldLbl(lbl), c); c.gridx = 1; p.add(tf, c); return r + 1;
        }

        private int tRow(Panel p, GridBagConstraints c,
                         int r, String lbl, Label val) {
            c.gridy = r; c.gridx = 0; c.gridwidth = 1;
            p.add(new Label(lbl), c); c.gridx = 1;
            val.setFont(new Font("SansSerif", Font.BOLD, 12)); p.add(val, c);
            return r + 1;
        }

        private String fmt(double v) { return String.format("KES %,.0f", v); }

        private Label boldLbl(String text) {
            Label l = new Label(text); l.setFont(new Font("SansSerif", Font.BOLD, 12));
            return l;
        }

        private Button mkBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setPreferredSize(new Dimension(200, 32));
            return b;
        }

        private Button sBtn(String l, Color bg) {
            Button b = new Button(l); b.setBackground(bg);
            b.setForeground(Color.WHITE); b.setFont(new Font("SansSerif", Font.BOLD, 11));
            return b;
        }
    }

    public class ManuEats extends Frame {

        private MenuService  menuService;
        private OrderService orderService;
        private DeliveryService deliveryService;
        private PaymentService paymentService;

        private InPersonPanel inPersonPanel;
        private OnlinePanel onlinePanel;
        private Button inPersonTab, onlineTab;

        public ManuEats() {
            super("ManuEats — Order Smart, Eat Well");

            // All services created once
            menuService = new MenuService();
            orderService = new OrderService();
            deliveryService = new DeliveryService();
            paymentService = new PaymentService();

            buildUI();
            setSize(1100, 760);
            setResizable(true);
            setVisible(true);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { System.exit(0); }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(0, 0));
            setBackground(new Color(245, 244, 240));

            Panel tabBar = new Panel(new BorderLayout());
            tabBar.setBackground(new Color(20, 20, 20));
            tabBar.setPreferredSize(new Dimension(0, 40));

            Panel tabBtns = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            tabBtns.setBackground(new Color(20, 20, 20));
            inPersonTab = tabBtn("  IN-PERSON ORDER  ", true);
            onlineTab = tabBtn("  ONLINE ORDER  ",   false);
            inPersonTab.addActionListener(e -> showInPerson());
            onlineTab.addActionListener(e -> showOnline());
            tabBtns.add(inPersonTab); tabBtns.add(onlineTab);
            tabBar.add(tabBtns, BorderLayout.WEST);

            Label brand = new Label("ManuEats  —  Order Smart, Eat Well  ", Label.RIGHT);
            brand.setFont(new Font("SansSerif", Font.BOLD, 13));
            brand.setForeground(new Color(220, 80, 20));
            brand.setBackground(new Color(20, 20, 20));
            tabBar.add(brand, BorderLayout.EAST);
            add(tabBar, BorderLayout.NORTH);

            inPersonPanel = new InPersonPanel(this, menuService,
                    orderService, paymentService);
            onlinePanel   = new OnlinePanel(this, menuService, orderService,
                    deliveryService, paymentService);

            add(inPersonPanel, BorderLayout.CENTER);
            showInPerson();
        }

        private void showInPerson() {
            getContentPane_().remove(onlinePanel);
            add(inPersonPanel, BorderLayout.CENTER);
            inPersonPanel.setVisible(true);
            onlinePanel.setVisible(false);
            highlightTab(inPersonTab);
            validate(); repaint();
        }

        private void showOnline() {
            getContentPane_().remove(inPersonPanel);
            add(onlinePanel, BorderLayout.CENTER);
            onlinePanel.setVisible(true);
            inPersonPanel.setVisible(false);
            highlightTab(onlineTab);
            validate(); repaint();
        }

        private Container getContentPane_() { return this; }
        private void highlightTab(Button active) {
            for (Button b : new Button[]{inPersonTab, onlineTab}) {
                boolean on = (b == active);
                b.setBackground(on ? new Color(220, 80, 20) : new Color(55, 55, 55));
                b.setForeground(Color.WHITE);
                b.setFont(new Font("SansSerif", on ? Font.BOLD : Font.PLAIN, 12));
            }
        }

        private Button tabBtn(String label, boolean active) {
            Button b = new Button(label);
            b.setBackground(active ? new Color(220, 80, 20) : new Color(55, 55, 55));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 12));
            b.setPreferredSize(new Dimension(180, 40));
            return b;
        }
    }
}
