E-commerce System

A fully functional console-based e-commerce system implemented in Java.

🚀 Features

Product Management

Expirable Products: Items like cheese and biscuits with expiration date validation.

Non-expirable Products: Includes electronics like TVs and mobile phones.

Shippable Products: Physical goods with defined weight attributes.

Digital Products: Non-shippable items such as mobile scratch cards.


Shopping Cart

Product addition with quantity and stock validation.

Prevents expired products from being added.

Merges quantities for duplicate items.


Checkout System

Generates shipment summaries and receipts.

Handles customer balance and deducts total costs.

Calculates shipping fees based on weight.


Robust Error Handling

Detects empty carts at checkout.

Ensures sufficient customer balance.

Prevents stock overflows.

Blocks expired items.


🏗️ Architecture

Product (Abstract Class)
├── ElectronicsProduct
├── FoodProduct
└── DigitalProduct

Interfaces:
├── Expirable
└── Shippable

💻 Running the System

1. Compile the program

javac ECommerceMain.java


2. Run the application

java ECommerceMain


3. Expected Console Output

Shipment notices

Checkout receipts

Error handling scenarios




🧪 Test Coverage

Test	Description

1	Mixed cart with both physical and digital items
2	Shipping weight calculation test
3	Digital-only purchase
4	Empty cart checkout attempt
5	Customer with insufficient balance
6	Exceeding available stock
7	Cart with expired products


📋 Sample Output

** Shipment Notice **
2x Cheese 400g
1x Biscuits 700g
Total package weight 1.1kg

** Checkout Receipt **
2x Cheese 200
1x Biscuits 150
1x Mobile Scratch Card 50

Subtotal 400
Shipping 31
Amount 431
Customer balance after payment: 1069

🔧 Implementation Overview

Main Components

Product: Abstract class representing shared product attributes.

ShoppingCart: Handles all cart operations and validation rules.

Customer: Stores balance and personal data.

ECommerceSystem: Orchestrates business logic, from cart to checkout.


Design Principles

Object-Oriented Design: Polymorphism and encapsulation used extensively.

Interface Segregation: Separates product behaviors like expiration and shipping.


