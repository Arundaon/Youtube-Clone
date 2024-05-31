create table users(
    username VARCHAR(16) NOT NULL,
    name    VARCHAR(16) NOT NULL,
    password VARCHAR(100) NOT NULL,
    profile VARCHAR(2048),
    bio VARCHAR(1024),
    token VARCHAR(100),
    expired_at BIGINT,
    PRIMARY KEY (username),
    UNIQUE(token)
);

CREATE TABLE videos(
    id VARCHAR(12)  NOT NULL,
    title VARCHAR(64) NOT NULL,
    video VARCHAR(2048) NOT NULL,
    username VARCHAR(16) NOT NULL,
    views BIGINT UNSIGNED NOT NULL DEFAULT 0,
    description TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY fk_users_videos (username) REFERENCES users (username)
);

CREATE TABLE comments(
    id INT UNSIGNED AUTO_INCREMENT,
    video_id VARCHAR(12) NOT NULL,
    username VARCHAR(16) NOT NULL,
    comment TEXT NOT NULL,
    PRIMARY KEY  (id),
    FOREIGN KEY fk_videos_comments (video_id) REFERENCES videos(id),
    FOREIGN KEY fk_users_comments (username) REFERENCES users(username)
);


CREATE TABLE video_likes(
    username VARCHAR(16) NOT NULL,
    video_id VARCHAR(12) NOT NULL,
    FOREIGN KEY fk_videos_likes (video_id) REFERENCES videos(id),
    FOREIGN KEY fk_user_likes (username) REFERENCES users(username)
);

drop table video_likes;
drop table comments;
drop table videos;
drop table users;


alter table videos modify thumbnail VARCHAR(2048);

alter table comments add created_at DATETIME DEFAULT CURRENT_TIMESTAMP;
alter table videos add created_at DATETIME DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE videos MODIFY views BIGINT UNSIGNED NOT NULL DEFAULT 0;

describe videos;