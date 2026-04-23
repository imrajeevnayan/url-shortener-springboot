-- Seed data for URL Shortener
-- Insert sample URLs for testing

INSERT INTO urls (short_code, original_url, custom_alias, title, description, click_count, created_at, expires_at, last_accessed_at, active, password) VALUES
('abc1234', 'https://www.google.com', NULL, 'Google', 'Search engine', 15, DATEADD('DAY', -5, NOW()), DATEADD('DAY', 25, NOW()), DATEADD('HOUR', -2, NOW()), true, NULL),
('xyz5678', 'https://www.github.com', 'gh', 'GitHub', 'Code repository', 42, DATEADD('DAY', -10, NOW()), DATEADD('DAY', 20, NOW()), DATEADD('HOUR', -5, NOW()), true, NULL),
('def9012', 'https://www.stackoverflow.com', NULL, 'Stack Overflow', 'Developer Q&A', 28, DATEADD('DAY', -3, NOW()), DATEADD('DAY', 27, NOW()), DATEADD('HOUR', -1, NOW()), true, NULL),
('ghi3456', 'https://www.youtube.com', 'yt', 'YouTube', 'Video platform', 100, DATEADD('DAY', -15, NOW()), DATEADD('DAY', 15, NOW()), DATEADD('HOUR', -10, NOW()), true, NULL),
('jkl7890', 'https://www.linkedin.com', NULL, 'LinkedIn', 'Professional network', 8, DATEADD('DAY', -1, NOW()), DATEADD('DAY', 29, NOW()), DATEADD('HOUR', -20, NOW()), true, NULL),
('mno1234', 'https://www.medium.com', NULL, 'Medium', 'Blogging platform', 35, DATEADD('DAY', -7, NOW()), DATEADD('DAY', 23, NOW()), DATEADD('HOUR', -3, NOW()), true, NULL),
('pqr5678', 'https://www.twitter.com', 'tw', 'Twitter/X', 'Social media', 67, DATEADD('DAY', -12, NOW()), DATEADD('DAY', 18, NOW()), DATEADD('HOUR', -8, NOW()), true, NULL),
('stu9012', 'https://www.reddit.com', NULL, 'Reddit', 'Community forum', 23, DATEADD('DAY', -4, NOW()), DATEADD('DAY', 26, NOW()), DATEADD('HOUR', -12, NOW()), true, NULL),
('vwx3456', 'https://www.wikipedia.org', 'wiki', 'Wikipedia', 'Encyclopedia', 56, DATEADD('DAY', -20, NOW()), DATEADD('DAY', 10, NOW()), DATEADD('HOUR', -15, NOW()), true, NULL),
('yza7890', 'https://www.amazon.com', NULL, 'Amazon', 'E-commerce', 89, DATEADD('DAY', -8, NOW()), DATEADD('DAY', 22, NOW()), DATEADD('HOUR', -6, NOW()), true, NULL);

-- Insert sample click events
INSERT INTO click_events (url_id, ip_address, user_agent, referer, country, city, browser, os, device, clicked_at) VALUES
(1, '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://google.com', 'United States', 'New York', 'Chrome', 'Windows 10/11', 'Desktop', DATEADD('HOUR', -2, NOW())),
(1, '192.168.1.2', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://twitter.com', 'United States', 'San Francisco', 'Safari', 'macOS', 'Desktop', DATEADD('HOUR', -5, NOW())),
(2, '10.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://linkedin.com', 'United Kingdom', 'London', 'Chrome', 'Windows 10/11', 'Desktop', DATEADD('HOUR', -3, NOW())),
(2, '10.0.0.2', 'Mozilla/5.0 (iPhone; CPU iPhone OS 16_0)', 'https://google.com', 'Germany', 'Berlin', 'Safari', 'iOS', 'Mobile', DATEADD('HOUR', -8, NOW())),
(3, '172.16.0.1', 'Mozilla/5.0 (Linux; Android 13)', 'https://reddit.com', 'France', 'Paris', 'Chrome', 'Android', 'Mobile', DATEADD('HOUR', -1, NOW())),
(4, '192.168.2.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://facebook.com', 'Canada', 'Toronto', 'Firefox', 'Windows 10/11', 'Desktop', DATEADD('HOUR', -10, NOW())),
(4, '192.168.2.2', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://twitter.com', 'Australia', 'Sydney', 'Chrome', 'macOS', 'Desktop', DATEADD('HOUR', -12, NOW())),
(5, '10.1.0.1', 'Mozilla/5.0 (iPad; CPU OS 16_0)', 'https://google.com', 'Japan', 'Tokyo', 'Safari', 'iOS', 'Tablet', DATEADD('HOUR', -20, NOW())),
(6, '172.17.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://stackoverflow.com', 'India', 'Mumbai', 'Edge', 'Windows 10/11', 'Desktop', DATEADD('HOUR', -3, NOW())),
(7, '192.168.3.1', 'Mozilla/5.0 (Linux; Android 12)', 'https://news.ycombinator.com', 'Brazil', 'Sao Paulo', 'Chrome', 'Android', 'Mobile', DATEADD('HOUR', -8, NOW()));
