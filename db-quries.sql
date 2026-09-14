USE shopverse;

INSERT INTO categories (
    name,
    description,
    image_url,
    created_at,
    updated_at
)
VALUES
    (
        'Running Shoes',
        'Lightweight shoes for road running, training, and daily miles.',
        'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=85',
        CURRENT_TIMESTAMP(6),
        CURRENT_TIMESTAMP(6)
    )
AS new
ON DUPLICATE KEY UPDATE
    description = new.description,
    image_url = new.image_url,
    updated_at = CURRENT_TIMESTAMP(6);



INSERT INTO products (
    name,
    slug,
    description,
    price,
    discount_price,
    stock_quantity,
    sku,
    brand,
    category_id,
    image_url,
    active,
    created_at,
    updated_at
)
VALUES (
    'Nike Dunk Low',
    'nike-dunk-low',
    'Classic everyday sneakers with a comfortable design.',
    8295.00,
    7999.00,
    100,
    'NIKE-DUNK-001',
    'Nike',
    1,
    'https://sneakerbardetroit.com/wp-content/uploads/2020/12/Nike-Dunk-Low-White-Green-CW1590-102-Release-Date-1-1068x733.png',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
update products set  image_url  = 
'https://cdna.lystit.com/1040/1300/n/photos/farfetch/197b525e/bally-marron-Chaussures-Oxford-Regent-En-Cuir.jpeg'
 where id = 4;
select * from products;
