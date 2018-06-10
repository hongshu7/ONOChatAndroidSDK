CREATE TABLE `conversation` (
    `belong_id`	TEXT NOT NULL,
    `target_id`	TEXT NOT NULL,
    `contact_time`    INTEGER NOT NULL,
    `conversation_type` INTEGER NOT NULL,
    `unread_count`	INTEGER NOT NULL,
    `last_message_id`	TEXT,
    PRIMARY KEY(belong_id,target_id)
);

CREATE TABLE `message` (
    `message_id`	TEXT NOT NULL,
    `belong_id`	TEXT NOT NULL,
    `target_id`	TEXT NOT NULL,
    `user_id`	TEXT NOT NULL,
    `type`	INTEGER NOT NULL DEFAULT 1,
    `data`	TEXT,
    `timestamp`	REAL DEFAULT 0,
    `is_send`	INTEGER DEFAULT 0,
    `is_self`	INTEGER DEFAULT 0,
    `is_error`	INTEGER DEFAULT 0,
    PRIMARY KEY(message_id)
);
    
CREATE TABLE `user` (
    `user_id`	TEXT NOT NULL,
    `nickname`	TEXT,
    `gender`	INTEGER,
    `avatar`	TEXT,
    `remark`   TEXT,
    PRIMARY KEY(user_id)
);

CREATE TABLE `friend` (
    `user_id`    TEXT NOT NULL,
    `friend_id`    TEXT NOT NULL,
    PRIMARY KEY(user_id, friend_id)
);

CREATE INDEX `conversation_list` ON `conversation` (`belong_id` ASC,`contact_time` DESC);

CREATE UNIQUE INDEX `user_msg_list` ON `message` (`belong_id` ASC,`target_id` ASC, `message_id` DESC);

CREATE TABLE `setting` (
    `ckey`	TEXT NOT NULL,
    `cvalue` TEXT NOT NULL,
    PRIMARY KEY(ckey)
);

INSERT INTO `setting`(ckey,cvalue) VALUES('version', '1');