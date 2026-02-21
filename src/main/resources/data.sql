-- Seed: user 1 has 50,000 USDT initial balance (assumption from requirements)
MERGE INTO wallet (user_id, asset, balance, updated_at) KEY(user_id, asset) VALUES (1, 'USDT', 50000, CURRENT_TIMESTAMP);
