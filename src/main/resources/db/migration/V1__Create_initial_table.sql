CREATE TABLE products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description_html TEXT,
    seo_title VARCHAR(255),
    seo_description TEXT,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP
);

CREATE TABLE categories (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    parent_id bigint NULL,
    name varchar(255) NOT NULL,
    created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE product_variants (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id bigint NOT NULL,
    sku varchar(255) UNIQUE NOT NULL,
    attributes_json json,
    price_amount int NOT NULL,
    weight_grams int,
    barcode varchar(255),
    is_active bool NOT NULL DEFAULT true,
    quantity int NOT NULL DEFAULT 0,
    created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_variants_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE media_library (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    url VARCHAR(512) NOT NULL,
    alt VARCHAR(255),
    size_bytes BIGINT,
    width_px INT,
    height_px INT,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_categories (
    variant_id bigint NOT NULL,
    categories_id bigint NOT NULL,
    sort_order int NOT NULL DEFAULT 0,
    PRIMARY KEY (variant_id, categories_id),
    CONSTRAINT fk_product_variant_categories_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id),
    CONSTRAINT fk_product_variant_media_media FOREIGN KEY (categories_id) REFERENCES categories(id)
);

CREATE TABLE carts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price_amount INT NOT NULL,
    price_currency VARCHAR(3) NOT NULL,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT fk_cart_items_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id)
);