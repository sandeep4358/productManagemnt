-- Drop the table if it already exists
DROP TABLE IF EXISTS products;

-- Create the table with appropriate columns
CREATE TABLE products (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(100),
    stock INT,
    brand VARCHAR(100),
    createdAt timestamp,
    updatedAt timestamp
)

-- Insert sample data into the table
INSERT INTO products (id, name, description, price, category, stock, brand, createdAt, updatedAt) VALUES
(1, 'Apple iPhone 13', 'Latest model smartphone from Apple with advanced features and improved battery life.', 999.99, 'Electronics', 50, 'Apple', '2023-01-01 10:00:00', '2023-01-05 12:00:00'),
(2, 'Samsung Galaxy S22', 'Flagship smartphone from Samsung featuring a high-resolution camera and powerful performance.', 899.99, 'Electronics', 75, 'Samsung', '2023-02-10 09:30:00', '2023-02-15 11:45:00'),
(3, 'Sony WH-1000XM4 Wireless Headphones', 'Industry-leading noise cancellation with exceptional sound quality and comfort.', 349.99, 'Audio', 100, 'Sony', '2023-03-05 14:20:00', '2023-03-10 15:30:00'),
(4, 'Dell XPS 15 Laptop', 'High-performance laptop with a sleek design, ideal for professionals and creatives.', 1299.99, 'Computers', 40, 'Dell', '2023-01-20 08:00:00', '2023-02-01 09:15:00'),
(5, 'Nike Air Max 270', 'Comfortable and stylish sneakers designed for everyday wear and athletic performance.', 150.00, 'Footwear', 200, 'Nike', '2023-04-01 10:00:00', '2023-04-05 12:00:00'),
(6, 'Adidas Ultraboost Running Shoes', 'High-performance running shoes with superior cushioning and support.', 180.00, 'Footwear', 180, 'Adidas', '2023-04-10 11:00:00', '2023-04-12 13:30:00'),
(7, 'KitchenAid Stand Mixer', 'Versatile stand mixer designed for professional-quality baking and cooking.', 399.99, 'Home Appliances', 30, 'KitchenAid', '2023-02-25 16:00:00', '2023-03-01 17:00:00'),
(8, 'Instant Pot Duo 7-in-1', 'Multi-functional electric pressure cooker that simplifies meal preparation.', 99.99, 'Home Appliances', 120, 'Instant Pot', '2023-03-15 12:00:00', '2023-03-20 14:00:00'),
(9, 'LG 55-inch 4K Smart TV', 'Ultra high-definition smart TV with advanced features and an immersive viewing experience.', 799.99, 'Electronics', 35, 'LG', '2023-04-15 13:00:00', '2023-04-20 15:00:00'),
(10, 'Canon EOS Rebel T7 DSLR Camera', 'User-friendly DSLR camera perfect for beginners, offering high-quality image capturing.', 449.99, 'Photography', 45, 'Canon', '2023-03-10 10:30:00', '2023-03-15 11:45:00');
