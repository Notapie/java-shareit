CREATE TABLE IF NOT EXISTS "user" (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(64),
    name VARCHAR(64)
);

CREATE UNIQUE INDEX IF NOT EXISTS "user_email_uniq_index"
    ON "user" (email);

CREATE INDEX IF NOT EXISTS "user_name_index"
    ON "user" (name);

CREATE TABLE IF NOT EXISTS "item" (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    owner_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE,
    name VARCHAR(64),
    description VARCHAR(1024),
    is_available BOOLEAN
);

CREATE INDEX IF NOT EXISTS "item_name_index"
    ON "item" (name, is_available);

CREATE INDEX IF NOT EXISTS "item_desc_index"
    ON "item" (description, is_available);
