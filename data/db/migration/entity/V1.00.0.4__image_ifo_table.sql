-- 이미지 경로 테이블
CREATE TABLE IMAGE_IFO
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    path       VARCHAR(255) NOT NULL,
    alias      VARCHAR(255) NOT NULL DEFAULT '',
    url        TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);