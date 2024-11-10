import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class CarStoreApp {

    private static final List<Car> cars = new ArrayList<>();
     private static final List<Customer> customers = new ArrayList<>();
    private static final List<Sale> sales = new ArrayList<>();
    
    private static final String CAR_CSV_FILE = "cars.csv";
    private static final String CUSTOMER_CSV_FILE = "customers.csv";
    private static final String SALE_CSV_FILE = "sales.csv";
   
    private static boolean loggedIn = false;

    public static void main(String[] args) {
        //addInitialData();
        loadCarsFromCSV(); 
        loadCustomersFromCSV();
        loadSalesFromCSV();
        SwingUtilities.invokeLater(CarStoreApp::createAndShowGUI);
    }

    private static void loadCarsFromCSV() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CAR_CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    String id = data[0];
                    String make = data[1];
                    String model = data[2];
                    double price = Double.parseDouble(data[3]);
                    String feature = data[4];
                    cars.add(new Car(id, make, model, price, feature));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading cars from CSV: " + e.getMessage());
        }
    }
    
     private static void saveCarsToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CAR_CSV_FILE))) {
            for (Car car : cars) {
                writer.write(String.join(",", car.getId(), car.getMake(), car.getModel(), 
                                          String.valueOf(car.getPrice()), car.getFeature()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving cars to CSV: " + e.getMessage());
        }
    }

    private static void loadCustomersFromCSV() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CUSTOMER_CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    String id = data[0];
                    String name = data[1];
                    String email = data[2];
                    String phone = data[3];
                    customers.add(new Customer(id, name, email, phone));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading customers from CSV: " + e.getMessage());
        }
    }
    
    private static void saveCustomersToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CUSTOMER_CSV_FILE))) {
            for (Customer customer : customers) {
                writer.write(String.join(",", customer.getId(), customer.getName(), 
                                          customer.getEmail(), customer.getPhone()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving customers to CSV: " + e.getMessage());
        }
    }
    
    private static void loadSalesFromCSV() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(SALE_CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    String saleId = data[0];
                    String carId = data[1];
                    String customerId = data[2];
                    double totalPrice = Double.parseDouble(data[3]);
                    sales.add(new Sale(saleId, carId, customerId, totalPrice));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading sales from CSV: " + e.getMessage());
        }
    }

    private static void saveSalesToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SALE_CSV_FILE))) {
            for (Sale sale : sales) {
                writer.write(String.join(",", sale.getSaleId(), sale.getCarId(), 
                                          sale.getCustomerId(), String.valueOf(sale.getTotalPrice())));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving sales to CSV: " + e.getMessage());
        }
    }
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Car Store Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);

        CardLayout cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);

        Color primaryColor = new Color(0, 0, 102); 
        Color secondaryColor = new Color(255, 255, 255); 
        Color accentColor = new Color(204, 0, 0); 

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(secondaryColor);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(primaryColor);
        JLabel headerLabel = new JLabel("Car Store Management System");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel navPanel = new JPanel(new GridLayout(1, 0, 10, 0));
        navPanel.setBackground(primaryColor);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] navItems = {"Dashboard", "Cars", "Customers", "Sales", "Customize", "Reports", "Logout"};
        for (String item : navItems) {
            JButton button = new JButton(item);
            button.setFont(new Font("Arial", Font.PLAIN, 18));
            button.setForeground(Color.WHITE);
            button.setBackground(accentColor);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)));
            button.addActionListener(e -> {
                if (loggedIn || item.equals("Logout")) {
                    cardLayout.show(contentPanel, item);
                    if (item.equals("Dashboard")) {
                        updateDashboardPanel(contentPanel);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please log in first.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                }
            });
            navPanel.add(button);
        }
        mainPanel.add(navPanel, BorderLayout.SOUTH);

        contentPanel.add(createLoginPanel(cardLayout, contentPanel), "Login");
        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createCarManagementPanel(), "Cars");
        contentPanel.add(createCustomerManagementPanel(), "Customers");
        contentPanel.add(createSalesManagementPanel(), "Sales");
        contentPanel.add(createCustomizationsPanel(), "Customize");
        contentPanel.add(createReportsPanel(), "Reports");
        contentPanel.add(createLogoutPanel(cardLayout, contentPanel), "Logout");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        cardLayout.show(contentPanel, "Login");
        frame.setVisible(true);
    }

    private static JPanel createLoginPanel(CardLayout cardLayout, JPanel contentPanel) {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(248, 249, 250));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel logoLabel = new JLabel("Car Store Management System", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginPanel.add(logoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField usernameField = new JTextField(20);
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        JPasswordField passwordField = new JPasswordField(20);
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if ("admin".equals(username) && "12345".equals(password)) {
                loggedIn = true;
                cardLayout.show(contentPanel, "Dashboard");
                updateDashboardPanel(contentPanel);
            } else {
                JOptionPane.showMessageDialog(loginPanel, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginPanel.add(loginButton, gbc);

        return loginPanel;
    }

    private static JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(new Color(248, 249, 250));

        JLabel titleLabel = new JLabel("Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        dashboardPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        statsPanel.setBackground(new Color(248, 249, 250));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.add(createStatPanel("Available Cars", String.valueOf(cars.size())));
        statsPanel.add(createStatPanel("Customers", String.valueOf(customers.size())));
        statsPanel.add(createStatPanel("Total Sales", String.valueOf(sales.size())));
        statsPanel.add(createStatPanel("Revenue", "$" + sales.stream().mapToDouble(Sale::getTotalPrice).sum()));
        statsPanel.add(createStatPanel("New Listings", "10"));
        statsPanel.add(createStatPanel("Inquiries Today", "5"));

        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        return dashboardPanel;
    }

    private static void updateDashboardPanel(JPanel contentPanel) {
        JPanel dashboardPanel = (JPanel) contentPanel.getComponent(1);
        JPanel statsPanel = (JPanel) dashboardPanel.getComponent(1);

        ((JLabel) ((JPanel) statsPanel.getComponent(0)).getComponent(1)).setText(String.valueOf(cars.size()));
        ((JLabel) ((JPanel) statsPanel.getComponent(1)).getComponent(1)).setText(String.valueOf(customers.size()));
        ((JLabel) ((JPanel) statsPanel.getComponent(2)).getComponent(1)).setText(String.valueOf(sales.size()));
        ((JLabel) ((JPanel) statsPanel.getComponent(3)).getComponent(1)).setText("$" + sales.stream().mapToDouble(Sale::getTotalPrice).sum());
        ((JLabel) ((JPanel) statsPanel.getComponent(4)).getComponent(1)).setText("10");
        ((JLabel) ((JPanel) statsPanel.getComponent(5)).getComponent(1)).setText("5");
    }

    private static JPanel createStatPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createCarManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        JLabel label = new JLabel("Car Management", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel carPanel = new JPanel(new BorderLayout());
        carPanel.setBackground(new Color(248, 249, 250));

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        carPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Make", "Model", "Price", "Feature"};
        Object[][] data = new Object[cars.size()][5];
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            data[i] = new Object[]{car.getId(), car.getMake(), car.getModel(), car.getPrice(), car.getFeature()};
        }
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable carTable = new JTable(tableModel);
        carPanel.add(new JScrollPane(carTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(e -> addCar(tableModel));
        editButton.addActionListener(e -> editCar(carTable, tableModel));
        deleteButton.addActionListener(e -> deleteCar(carTable, tableModel));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        carPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(carPanel, BorderLayout.CENTER);
        return panel;
    }

   private static void addCar(DefaultTableModel tableModel) {
        String id = JOptionPane.showInputDialog("Enter ID:");
        String make = JOptionPane.showInputDialog("Enter Make:");
        String model = JOptionPane.showInputDialog("Enter Model:");
        String price = JOptionPane.showInputDialog("Enter Price:");
        String feature = JOptionPane.showInputDialog("Enter Feature:");
        Car car = new Car(id, make, model, Double.parseDouble(price), feature);
        tableModel.addRow(new Object[]{id, make, model, price, feature});
        cars.add(car);
        saveCarsToCSV(); // Save the updated car list
    }

    private static void editCar(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String id = JOptionPane.showInputDialog("Edit ID:", tableModel.getValueAt(selectedRow, 0));
            String make = JOptionPane.showInputDialog("Edit Make:", tableModel.getValueAt(selectedRow, 1));
            String model = JOptionPane.showInputDialog("Edit Model:", tableModel.getValueAt(selectedRow, 2));
            String price = JOptionPane.showInputDialog("Edit Price:", tableModel.getValueAt(selectedRow, 3));
            String feature = JOptionPane.showInputDialog("Edit Feature:", tableModel.getValueAt(selectedRow, 4));
            Car car = new Car(id, make, model, Double.parseDouble(price), feature);
            tableModel.setValueAt(id, selectedRow, 0);
            tableModel.setValueAt(make, selectedRow, 1);
            tableModel.setValueAt(model, selectedRow, 2);
            tableModel.setValueAt(price, selectedRow, 3);
            tableModel.setValueAt(feature, selectedRow, 4);
            cars.set(selectedRow, car);
            saveCarsToCSV(); // Save the updated car list
        } else {
            JOptionPane.showMessageDialog(table, "Please select a car to edit.");
        }
    }


    private static void deleteCar(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            cars.remove(selectedRow);
            saveCarsToCSV(); // Save the updated car list
        } else {
            JOptionPane.showMessageDialog(table, "Please select a car to delete.");
        }
    }


    private static JPanel createCustomerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        JLabel label = new JLabel("Customer Management", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBackground(new Color(248, 249, 250));

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        customerPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Email", "Phone"};
        Object[][] data = new Object[customers.size()][4];
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            data[i] = new Object[]{customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone()};
        }
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable customerTable = new JTable(tableModel);
        customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(e -> addCustomer(tableModel));
        editButton.addActionListener(e -> editCustomer(customerTable, tableModel));
        deleteButton.addActionListener(e -> deleteCustomer(customerTable, tableModel));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        customerPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(customerPanel, BorderLayout.CENTER);
        return panel;
    }

    private static void addCustomer(DefaultTableModel tableModel) {
        String id = JOptionPane.showInputDialog("Enter ID:");
        String name = JOptionPane.showInputDialog("Enter Name:");
        String email = JOptionPane.showInputDialog("Enter Email:");
        String phone = JOptionPane.showInputDialog("Enter Phone:");
        Customer customer = new Customer(id, name, email, phone);
        tableModel.addRow(new Object[]{id, name, email, phone});
        customers.add(customer);
        saveCustomersToCSV(); // Save updated customer list
    }

    private static void editCustomer(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String id = JOptionPane.showInputDialog("Edit ID:", tableModel.getValueAt(selectedRow, 0));
            String name = JOptionPane.showInputDialog("Edit Name:", tableModel.getValueAt(selectedRow, 1));
            String email = JOptionPane.showInputDialog("Edit Email:", tableModel.getValueAt(selectedRow, 2));
            String phone = JOptionPane.showInputDialog("Edit Phone:", tableModel.getValueAt(selectedRow, 3));
            Customer customer = new Customer(id, name, email, phone);
            tableModel.setValueAt(id, selectedRow, 0);
            tableModel.setValueAt(name, selectedRow, 1);
            tableModel.setValueAt(email, selectedRow, 2);
            tableModel.setValueAt(phone, selectedRow, 3);
            customers.set(selectedRow, customer);
            saveCustomersToCSV(); // Save updated customer list
        } else {
            JOptionPane.showMessageDialog(table, "Please select a customer to edit.");
        }
    }

    private static void deleteCustomer(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            customers.remove(selectedRow);
            saveCustomersToCSV(); // Save updated customer list
        } else {
            JOptionPane.showMessageDialog(table, "Please select a customer to delete.");
        }
    }

    private static JPanel createSalesManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        JLabel label = new JLabel("Sales Management", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel salesPanel = new JPanel(new BorderLayout());
        salesPanel.setBackground(new Color(248, 249, 250));

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        salesPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = {"Sale ID", "Car ID", "Customer ID", "Total Price"};
        Object[][] data = new Object[sales.size()][4];
        for (int i = 0; i < sales.size(); i++) {
            Sale sale = sales.get(i);
            data[i] = new Object[]{sale.getSaleId(), sale.getCarId(), sale.getCustomerId(), sale.getTotalPrice()};
        }
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable salesTable = new JTable(tableModel);
        salesPanel.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(e -> addSale(tableModel));
        deleteButton.addActionListener(e -> deleteSale(salesTable, tableModel));

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        salesPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(salesPanel, BorderLayout.CENTER);
        return panel;
    }

    private static void addSale(DefaultTableModel tableModel) {
        String saleId = JOptionPane.showInputDialog("Enter Sale ID:");
        String carId = JOptionPane.showInputDialog("Enter Car ID:");
        String customerId = JOptionPane.showInputDialog("Enter Customer ID:");
        String totalPrice = JOptionPane.showInputDialog("Enter Total Price:");
        Sale sale = new Sale(saleId, carId, customerId, Double.parseDouble(totalPrice));
        tableModel.addRow(new Object[]{saleId, carId, customerId, totalPrice});
        sales.add(sale);
        saveSalesToCSV(); // Save updated sales list
    }

    private static void deleteSale(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            sales.remove(selectedRow);
            saveSalesToCSV(); // Save updated sales list
        } else {
            JOptionPane.showMessageDialog(table, "Please select a sale to delete.");
        }
    }

    private static JPanel createCustomizationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        JLabel label = new JLabel("Car Customizations", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel customizationPanel = new JPanel();
        customizationPanel.setBackground(new Color(248, 249, 250));
        customizationPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel carIdLabel = new JLabel("Car ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        customizationPanel.add(carIdLabel, gbc);
        JTextField carIdField = new JTextField(20);
        gbc.gridx = 1;
        customizationPanel.add(carIdField, gbc);

        JLabel colorLabel = new JLabel("Color:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        customizationPanel.add(colorLabel, gbc);
        JComboBox<String> colorComboBox = new JComboBox<>(new String[]{"Red", "Blue", "Black", "White", "Silver"});
        gbc.gridx = 1;
        customizationPanel.add(colorComboBox, gbc);

        JLabel wheelsLabel = new JLabel("Wheels:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        customizationPanel.add(wheelsLabel, gbc);
        JComboBox<String> wheelsComboBox = new JComboBox<>(new String[]{"Standard", "Sport", "Alloy"});
        gbc.gridx = 1;
        customizationPanel.add(wheelsComboBox, gbc);

        JLabel interiorLabel = new JLabel("Interior:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        customizationPanel.add(interiorLabel, gbc);
        JComboBox<String> interiorComboBox = new JComboBox<>(new String[]{"Leather", "Fabric", "Synthetic"});
        gbc.gridx = 1;
        customizationPanel.add(interiorComboBox, gbc);

        JLabel soundSystemLabel = new JLabel("Sound System:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        customizationPanel.add(soundSystemLabel, gbc);
        JComboBox<String> soundSystemComboBox = new JComboBox<>(new String[]{"Standard", "Premium", "Luxury"});
        gbc.gridx = 1;
        customizationPanel.add(soundSystemComboBox, gbc);

        JLabel priceLabelTitle = new JLabel("Price:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        customizationPanel.add(priceLabelTitle, gbc);
        JLabel priceLabel = new JLabel("$0");
        gbc.gridx = 1;
        customizationPanel.add(priceLabel, gbc);

        JButton calculateButton = new JButton("Calculate Price");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        calculateButton.addActionListener(e -> {
            double basePrice = cars.stream()
                    .filter(car -> car.getId().equals(carIdField.getText()))
                    .mapToDouble(Car::getPrice)
                    .findFirst()
                    .orElse(0);
            double customPrice = basePrice +
                    colorComboBox.getSelectedIndex() * 500 +
                    wheelsComboBox.getSelectedIndex() * 800 +
                    interiorComboBox.getSelectedIndex() * 1000 +
                    soundSystemComboBox.getSelectedIndex() * 1200;
            priceLabel.setText("$" + customPrice);
        });
        customizationPanel.add(calculateButton, gbc);

        panel.add(customizationPanel, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        JLabel label = new JLabel("Reports", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel reportsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        reportsPanel.setBackground(new Color(248, 249, 250));
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        reportsPanel.add(createReportPanel("Car Report"));
        reportsPanel.add(createReportPanel("Customer Report"));
        reportsPanel.add(createReportPanel("Sales Report"));
        reportsPanel.add(createReportPanel("Customization Report"));
        reportsPanel.add(createReportPanel("Financial Report"));
        reportsPanel.add(createReportPanel("Inventory Report"));

        panel.add(reportsPanel, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel createReportPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JButton viewButton = new JButton("View Report");
        viewButton.setBackground(new Color(0, 123, 255));
        viewButton.setForeground(Color.WHITE);
        viewButton.addActionListener(e -> JOptionPane.showMessageDialog(panel, title + " is displayed here."));
        panel.add(viewButton, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createLogoutPanel(CardLayout cardLayout, JPanel contentPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        JLabel label = new JLabel("You have been logged out.", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);

        JButton loginAgainButton = new JButton("Login Again");
        loginAgainButton.setBackground(new Color(0, 123, 255));
        loginAgainButton.setForeground(Color.WHITE);
        loginAgainButton.addActionListener(e -> {
            loggedIn = false;
            cardLayout.show(contentPanel, "Login");
        });
        panel.add(loginAgainButton, BorderLayout.SOUTH);

        return panel;
    }
}





