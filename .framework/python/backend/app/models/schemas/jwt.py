from datetime import datetime

from pydantic import BaseModel

from app.models.domain.users import UserRole

class JWTMeta(BaseModel):
    exp: datetime
    sub: str


class JWTUser(BaseModel):
    username: str
