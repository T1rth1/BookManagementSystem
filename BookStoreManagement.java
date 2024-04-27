import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class BookStoreManagement {
    static final String CSV_FILE_PATH = "Book1.csv"; // CSV file path
    static String ADMIN_PASSWORD = ""; // Admin password

    static final int MAX_CART_SIZE = 10;
     static Book[] cart = new Book[MAX_CART_SIZE];
    static int cartIndex = 0;
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the password!");
        ADMIN_PASSWORD = sc.nextLine();
        while (true) {
            System.out.println("Enter Choice: \n" + "1 -> Search The Particular Book\n" + "2 -> Add The Book Into Store\n" + "3 -> Checkout The Book From The Cart");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 1) {
                searchBook(sc);
            } else if (choice == 2) {
                adminAddBook(sc); // Admin add book functionality
            } else if (choice == 3) {
                checkoutBook(sc);
            }
        }
    }

    public static void searchBook(Scanner sc) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            System.out.print("Enter book name to search: ");
            String nam = sc.nextLine();
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(nam)) {
                    found = true;
                    System.out.println("Book name: " + data[0]);
                    System.out.println("Quantity: " + data[1]);
                    System.out.println("Price: " + data[2]);

                    System.out.println("Want to add this book in your cart: Y/N");
                    String choice = sc.nextLine();
                    if (choice.equalsIgnoreCase("Y")) {
                        System.out.print("Enter quantity to add to cart: ");
                        int qtyToAdd = sc.nextInt();
                        sc.nextLine();

                        int availableQty = Integer.parseInt(data[1]);
                        if (qtyToAdd > availableQty) {
                            System.out.println("Sorry, only " + availableQty + " copies available.");
                        } else {
                            // Update quantity in CSV file
                            int updatedQty = availableQty - qtyToAdd;
                            updateQuantityInCSV(nam, updatedQty);

                            // Add to cart
                            addToCart(nam, qtyToAdd, Integer.parseInt(data[2]));
                            System.out.println("Book added to cart successfully!");
                        }
                    }
                    break;
                }
            }
            if (!found) {
                System.out.println("Book not found!");
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
    }
public static void addToCart(String name, int quantity, int pricePerItem) {
        // Check if cartIndex is within bounds
        if (cartIndex < MAX_CART_SIZE) {
            // Create a new Book object and add it to the cart array
            cart[cartIndex++] = new Book(name, quantity, pricePerItem * quantity);
        } else {
            System.out.println("Cart is full. Cannot add more items.");
        }
    }

    public static void checkoutBook(Scanner sc) {
        int total=0;
                System.out.println("----------------------------------------------\n");
                System.out.println("                    Bill                      \n");
                System.out.println("----------------------------------------------\n");
                System.out.printf("|%-25s|%-10s|%-7s|%n","Book name","Quantity","Price");
                System.out.println("----------------------------------------------\n");
                for(int i=0;i<cartIndex;i++)
                {
                    System.out.printf("|%-25s|%-10s|%-7s|%n",cart[i].name ,cart[i].quantity,cart[i].price);
                    total+=cart[i].price;
                }
                System.out.println("----------------------------------------------\n");
                System.out.printf("|%-25s|%-10s|%-7s|%n","","Total",total);
                cartIndex=0;
    }
    public static void updateQuantityInCSV(String bookName, int newQuantity) {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            StringBuilder updatedData = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(bookName)) {
                    line = data[0] + "," + newQuantity + "," + data[2];
                }
                updatedData.append(line).append(System.lineSeparator());
            }

            try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
                writer.write(updatedData.toString());
            }
        } catch (IOException e) {
            System.out.println("Error updating quantity in CSV file: " + e.getMessage());
        }
    }
    public static void adminAddBook(Scanner sc) {
        System.out.print("Enter Admin password: ");
        String pass = sc.nextLine();
        if (pass.equals(ADMIN_PASSWORD)) {
            try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
                StringBuilder updatedData = new StringBuilder();
                System.out.println("Enter Book detail: ");
                System.out.print("Enter name of the Book: ");
                String name = sc.nextLine();
                System.out.print("Enter quantity: ");
                int qty = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter price of the Book: ");
                int price = sc.nextInt();
                sc.nextLine();
                String newLine = name + "," + qty + "," + price;

                String line;
                boolean found = false;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(name)) {
                        found = true;
                        int existingQty = Integer.parseInt(data[1]);
                        qty += existingQty; // Update quantity by adding new quantity to existing quantity
                        line = data[0] + "," + qty + "," + data[2];
                    }
                    updatedData.append(line).append(System.lineSeparator());
                }

                if (!found) {
                    updatedData.append(newLine).append(System.lineSeparator());
                }

                try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
                    writer.write(updatedData.toString());
                    System.out.println("Book added/updated successfully in CSV file.");
                } catch (IOException e) {
                    System.out.println("Error writing to CSV file: " + e.getMessage());
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error processing book addition/update: " + e.getMessage());
            }
        } else {
            System.out.println("Incorrect password. Access denied.");
        }
    }
}
