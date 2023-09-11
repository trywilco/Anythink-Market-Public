
import asyncio
import asyncpg

from app.db.repositories.users import UsersRepository
from app.core.config import get_app_settings

async def create_users():
    SETTINGS = get_app_settings()
    DATABASE_URL = SETTINGS.database_url.replace("postgres://", "postgresql://")
    conn = await asyncpg.connect(DATABASE_URL)
    usersRepository = UsersRepository(conn=conn)

    await usersRepository.create_user(username="regularuser", password="123456", email="regularuser@gmail.com", role="user")
    await usersRepository.create_user(username="adminuser", password="123456", email=f"adminuser@gmail.com", role="admin")
    
    await conn.close()

loop = asyncio.get_event_loop()
loop.run_until_complete(create_users())