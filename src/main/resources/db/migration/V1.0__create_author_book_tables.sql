CREATE TABLE author (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    birth_date  DATE NOT NULL,
    CONSTRAINT chk_author_birth_date CHECK (birth_date <= CURRENT_DATE)
);

CREATE TABLE book (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    price        INTEGER NOT NULL,
    status       VARCHAR(20) NOT NULL,
    published_at DATE,
    CONSTRAINT chk_book_price CHECK (price >= 0)
);

CREATE TABLE book_author (
    book_id   BIGINT NOT NULL REFERENCES book(id),
    author_id BIGINT NOT NULL REFERENCES author(id),
    PRIMARY KEY (book_id, author_id)
);
