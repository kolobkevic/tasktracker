CREATE TABLE user
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    firstname VARCHAR(50)  NOT NULL,
    lastname  VARCHAR(50)  NOT NULL,
    password  VARCHAR(255) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE task
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    user_id    INT,
    status     VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    done_at    TIMESTAMP NULL,
    FOREIGN KEY (owner) REFERENCES user (id)
);