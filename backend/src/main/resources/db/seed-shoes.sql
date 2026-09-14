-- ShoeVerse shoe catalog seed data for MySQL.
-- Safe to run repeatedly: category names, product slugs, and SKUs are unique.

INSERT INTO categories (name, description, image_url, created_at, updated_at)
VALUES
    ('Running Shoes', 'Lightweight shoes for road running, training, and daily miles.', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=85', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('Sneakers', 'Everyday sneakers built for comfort and street style.', 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?auto=format&fit=crop&w=900&q=85', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('Formal Shoes', 'Polished leather footwear for work and special occasions.', 'https://images.unsplash.com/photo-1614252369475-531aba?auto=format&fit=crop&w=900&q=85', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('Boots', 'Durable boots for outdoor days and everyday utility.', 'https://images.unsplash.com/photo-1520639888713-7851133b1ed0?auto=format&fit=crop&w=900&q=85', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6))
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    image_url = VALUES(image_url),
    updated_at = CURRENT_TIMESTAMP(6);

INSERT INTO products (name, slug, description, price, discount_price, stock_quantity, sku, brand, category_id, image_url, active, created_at, updated_at)
SELECT seed.name, seed.slug, seed.description, seed.price, seed.discount_price, seed.stock_quantity, seed.sku, seed.brand, categories.id, seed.image_url, TRUE, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
FROM (
    SELECT 'AeroRun Carbon 2' name, 'aerorun-carbon-2' slug, 'Responsive road running shoes with a breathable mesh upper and carbon-infused midsole.' description, 12999.00 price, 10999.00 discount_price, 30 stock_quantity, 'RUN-AERO-001' sku, 'AeroRun' brand, 'Running Shoes' category_name, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=85' image_url
    UNION ALL SELECT 'StrideFlex Daily', 'strideflex-daily', 'Cushioned daily trainers with a stable heel and flexible forefoot.', 7499.00, 6499.00, 45, 'RUN-STRIDE-002', 'StrideFlex', 'Running Shoes', 'https://images.unsplash.com/photo-1552674605-db6ffd4facb5?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'PaceLite Knit', 'pacelite-knit', 'Lightweight knit runners for comfortable training and city commutes.', 5999.00, NULL, 38, 'RUN-PACE-003', 'PaceLite', 'Running Shoes', 'https://images.unsplash.com/photo-1554135490-3e7c5e3c8f9f?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'Urban Court Low', 'urban-court-low', 'Minimal low-top sneakers with a clean court-inspired silhouette.', 4999.00, 3999.00, 52, 'SNK-URBAN-001', 'Northline', 'Sneakers', 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'Canvas Street 01', 'canvas-street-01', 'Classic canvas sneakers with a reinforced toe and rubber outsole.', 2999.00, NULL, 60, 'SNK-CANVAS-002', 'Common Ground', 'Sneakers', 'https://images.unsplash.com/photo-1495555961986-6d4c1ecb7be3?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'Mono Leather Sneaker', 'mono-leather-sneaker', 'Full-grain leather sneakers with a cushioned footbed for everyday wear.', 6999.00, 5799.00, 25, 'SNK-MONO-003', 'Forma', 'Sneakers', 'https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'Regent Oxford', 'regent-oxford', 'Classic cap-toe Oxford shoes in polished leather for formal occasions.', 8999.00, 7999.00, 22, 'FOR-REGENT-001', 'Regent', 'Formal Shoes', 'https://images.unsplash.com/photo-1614252369475-531aba?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'Milano Derby', 'milano-derby', 'Versatile Derby shoes with a comfortable cushioned insole and leather upper.', 8499.00, NULL, 18, 'FOR-MILANO-002', 'Milano', 'Formal Shoes', 'https://images.unsplash.com/photo-1614252369475-531aba?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'Penny Loafer Classic', 'penny-loafer-classic', 'Slip-on leather loafers with a timeless penny strap detail.', 7799.00, 6799.00, 20, 'FOR-LOAFER-003', 'Heritage Co.', 'Formal Shoes', 'https://images.unsplash.com/photo-1533867617858-e7b97e060509?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'Trailmark Hiker', 'trailmark-hiker', 'Weather-ready hiking boots with a grippy outsole and padded collar.', 9999.00, 8499.00, 16, 'BOT-TRAIL-001', 'Trailmark', 'Boots', 'https://images.unsplash.com/photo-1520639888713-7851133b1ed0?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'Ridge Chelsea', 'ridge-chelsea', 'Pull-on Chelsea boots with elastic side panels and a durable leather finish.', 8999.00, NULL, 14, 'BOT-RIDGE-002', 'Ridge', 'Boots', 'https://images.unsplash.com/photo-1608256246200-53e635b5b65f?auto=format&fit=crop&w=900&q=85'
    UNION ALL SELECT 'Forge Utility Boot', 'forge-utility-boot', 'Hard-wearing utility boots with reinforced eyelets and an all-day comfort footbed.', 10999.00, 9499.00, 12, 'BOT-FORGE-003', 'Forge', 'Boots', 'https://images.unsplash.com/photo-1542840410-3092f99611a3?auto=format&fit=crop&w=900&q=85'
) seed
JOIN categories ON categories.name = seed.category_name
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    discount_price = VALUES(discount_price),
    stock_quantity = VALUES(stock_quantity),
    brand = VALUES(brand),
    category_id = VALUES(category_id),
    image_url = VALUES(image_url),
    active = VALUES(active),
    updated_at = CURRENT_TIMESTAMP(6);