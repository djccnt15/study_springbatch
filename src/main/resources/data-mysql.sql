TRUNCATE TABLE orders;
INSERT INTO orders (customer_id, order_datetime, status, shipping_id)
SELECT
    FLOOR(RAND() * 100 + 1),
    NOW() - INTERVAL FLOOR(RAND() * 30) DAY,
    CASE
        WHEN rn <= 10 THEN 'READY_FOR_SHIPMENT'
        WHEN rn <= 20 THEN 'SHIPPED'
        ELSE 'CANCELLED'
    END,
    CASE
        WHEN rn <= 10 THEN NULL
        WHEN rn <= 20 THEN NULL
        ELSE CONCAT('SHIP-', LPAD(rn, 8, '0'))
    END
FROM (
    SELECT @rownum := @rownum + 1 AS rn
    FROM information_schema.tables t1,
         information_schema.tables t2,
         (SELECT @rownum := 0) r
    LIMIT 30
) AS series;
