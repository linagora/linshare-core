ALTER TABLE document ADD COLUMN check_sha256_sum BOOLEAN;
UPDATE document SET check_sha256_sum = false;
ALTER TABLE document ALTER COLUMN check_sha256_sum SET NOT NULL;