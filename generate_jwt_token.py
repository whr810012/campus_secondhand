import jwt
import datetime

# JWT配置（从application.yml中获取）
SECRET_KEY = "mySecretKey123456789012345678901234567890"
ALGORITHM = "HS256"

# 管理员用户信息
admin_user = {
    "id": 1,
    "phone": "13800000000",
    "role": "admin"
}

# 生成JWT token
payload = {
    "id": admin_user["id"],
    "phone": admin_user["phone"],
    "role": admin_user["role"],
    "iat": datetime.datetime.utcnow(),
    "exp": datetime.datetime.utcnow() + datetime.timedelta(days=7)
}

token = jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)
print(f"Generated JWT Token: {token}")