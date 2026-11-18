CREATE TABLE books (
    isbn             VARCHAR(255) PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    author           VARCHAR(255) NOT NULL,
    publication_year INTEGER      NOT NULL CHECK (publication_year > 0)
);

CREATE TABLE members (
    id         VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL CHECK (email LIKE '%@%')
);

CREATE TABLE loans (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn        VARCHAR(255) NOT NULL REFERENCES books (isbn),
    member_id   VARCHAR(255) NOT NULL REFERENCES members (id),
    loan_date   DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE,
    CHECK (due_date >= loan_date),
    CHECK (return_date IS NULL OR return_date >= loan_date)
);

CREATE UNIQUE INDEX one_active_loan_per_book
    ON loans (isbn) WHERE return_date IS NULL;