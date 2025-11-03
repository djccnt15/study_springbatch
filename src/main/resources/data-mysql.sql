TRUNCATE TABLE target_process;
INSERT INTO target_process (name, process_id, terminated_at, status) VALUES
('zombie_process', 'PID_12345', '2024-01-01 12:00:00', 'TERMINATED'),
('sleeping_thread', 'PID_45678', '2024-01-15 15:30:00', 'TERMINATED'),
('memory_leak', 'PID_98765', '2024-02-01 09:15:00', 'RUNNING'),
('infinite_loop', 'PID_24680', '2024-02-15 18:45:00', 'RUNNING');

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
