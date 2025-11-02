TRUNCATE TABLE orders RESTART IDENTITY;
INSERT INTO orders (customer_id, order_datetime, status, shipping_id)
SELECT
    floor(random() * 100 + 1),
    NOW() - (random() * INTERVAL '30 days'),
    CASE
        WHEN rn <= 10 THEN 'READY_FOR_SHIPMENT'
        WHEN rn <= 20 THEN 'SHIPPED'
        ELSE 'CANCELLED'
    END,
    CASE
        WHEN rn <= 10 THEN NULL
        WHEN rn <= 20 THEN NULL
        ELSE 'SHIP-' || LPAD(CAST(rn AS VARCHAR), 8, '0')
    END
FROM (
    SELECT GENERATE_SERIES(1, 30) AS rn
) AS series;
