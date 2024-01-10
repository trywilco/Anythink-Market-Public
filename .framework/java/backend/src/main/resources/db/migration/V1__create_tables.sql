create table users (
  id varchar(255) primary key,
  username varchar(255) UNIQUE,
  password varchar(255),
  email varchar(255) UNIQUE,
  bio text,
  image varchar(511)
);

create table items (
  id varchar(255) primary key,
  seller_id varchar(255),
  slug varchar(255) UNIQUE,
  title varchar(255),
  description text,
  image text,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

create table item_favorites (
  item_id varchar(255) not null,
  user_id varchar(255) not null,
  primary key(item_id, user_id)
);

create table follows (
  user_id varchar(255) not null,
  follow_id varchar(255) not null
);

create table tags (
  id varchar(255) primary key,
  name varchar(255) not null
);

create table item_tags (
  item_id varchar(255) not null,
  tag_id varchar(255) not null,
  created_at TIMESTAMP not null
);

create table comments (
  id varchar(255) primary key,
  body text,
  item_id varchar(255),
  user_id varchar(255),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
