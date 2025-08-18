-- Insert sample categories
INSERT INTO categories (name, description) VALUES 
('Electronics', 'Electronic devices and accessories'),
('Clothing', 'Apparel and fashion items'),
('Books', 'Books and educational materials'),
('Home & Kitchen', 'Home appliances and kitchenware');

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category_id) VALUES 
('Smartphone', 'Latest model smartphone with advanced features', 699.99, 50, 1),
('Laptop', 'High-performance laptop for work and gaming', 1299.99, 30, 1),
('T-Shirt', 'Cotton t-shirt in various colors', 19.99, 100, 2),
('Jeans', 'Denim jeans for casual wear', 49.99, 75, 2),
('Java Programming', 'Comprehensive guide to Java programming', 39.99, 50, 3),
('Cookware Set', 'Complete cookware set for your kitchen', 149.99, 25, 4);

-- Insert sample users
INSERT INTO users (name, username, password, role) VALUES 
('Admin User', 'admin', '$2a$10$8K1p/a0dhrxiowP.dnkgNORTWgdEDHn5L2/xjpEWuC.QQv4rKO9jO', 'ADMIN'),
('Staff User', 'staff', '$2a$10$8K1p/a0dhrxiowP.dnkgNORTWgdEDHn5L2/xjpEWuC.QQv4rKO9jO', 'STAFF'),
('Customer User', 'customer', '$2a$10$8K1p/a0dhrxiowP.dnkgNORTWgdEDHn5L2/xjpEWuC.QQv4rKO9jO', 'CUSTOMER');