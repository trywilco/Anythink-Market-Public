"""Typings for queries generated by aiosql"""

from typing import Dict, Optional, Sequence

from asyncpg import Connection, Record

class TagsQueriesMixin:
    async def get_all_tags(self, conn: Connection) -> Record: ...
    async def create_new_tags(
        self, conn: Connection, tags: Sequence[Dict[str, str]]
    ) -> None: ...

class UsersQueriesMixin:
    async def get_user_by_email(self, conn: Connection, *, email: str) -> Record: ...
    async def get_user_by_username(
        self, conn: Connection, *, username: str
    ) -> Record: ...
    async def create_new_user(
        self,
        conn: Connection,
        *,
        username: str,
        email: str,
        salt: str,
        hashed_password: str
    ) -> Record: ...
    async def update_user_by_username(
        self,
        conn: Connection,
        *,
        username: str,
        new_username: str,
        new_email: str,
        new_salt: str,
        new_password: str,
        new_bio: Optional[str],
        new_image: Optional[str]
    ) -> Record: ...

class ProfilesQueriesMixin:
    async def is_user_following_for_another(
        self, conn: Connection, *, follower_username: str, following_username: str
    ) -> Record: ...
    async def subscribe_user_to_another(
        self, conn: Connection, *, follower_username: str, following_username: str
    ) -> None: ...
    async def unsubscribe_user_from_another(
        self, conn: Connection, *, follower_username: str, following_username: str
    ) -> None: ...

class CommentsQueriesMixin:
    async def get_comments_for_item_by_slug(
        self, conn: Connection, *, slug: str
    ) -> Record: ...
    async def get_comment_by_id_and_slug(
        self, conn: Connection, *, comment_id: int, item_slug: str
    ) -> Record: ...
    async def create_new_comment(
        self, conn: Connection, *, body: str, item_slug: str, seller_username: str
    ) -> Record: ...
    async def delete_comment_by_id(
        self, conn: Connection, *, comment_id: int, seller_username: str
    ) -> None: ...

class ItemsQueriesMixin:
    async def add_item_to_favorites(
        self, conn: Connection, *, username: str, slug: str
    ) -> None: ...
    async def remove_item_from_favorites(
        self, conn: Connection, *, username: str, slug: str
    ) -> None: ...
    async def is_item_in_favorites(
        self, conn: Connection, *, username: str, slug: str
    ) -> Record: ...
    async def get_favorites_count_for_item(
        self, conn: Connection, *, slug: str
    ) -> Record: ...
    async def get_tags_for_item_by_slug(
        self, conn: Connection, *, slug: str
    ) -> Record: ...
    async def get_item_by_slug(self, conn: Connection, *, slug: str) -> Record: ...
    async def create_new_item(
        self,
        conn: Connection,
        *,
        slug: str,
        title: str,
        description: str,
        body: str,
        seller_username: str,
        image: str
    ) -> Record: ...
    async def add_tags_to_item(
        self, conn: Connection, tags_slugs: Sequence[Dict[str, str]]
    ) -> None: ...
    async def update_item(
        self,
        conn: Connection,
        *,
        slug: str,
        seller_username: str,
        new_title: str,
        new_body: str,
        new_description: str,
        new_image: str
    ) -> Record: ...
    async def delete_item(
        self, conn: Connection, *, slug: str, seller_username: str
    ) -> None: ...
    async def get_items_for_feed(
        self, conn: Connection, *, follower_username: str, limit: int, offset: int
    ) -> Record: ...

class Queries(
    TagsQueriesMixin,
    UsersQueriesMixin,
    ProfilesQueriesMixin,
    CommentsQueriesMixin,
    ItemsQueriesMixin,
): ...

queries: Queries
