import jwt
import datetime

# JWT配置
SECRET_KEY = "campus-secondhand-jwt-secret-key-2023"
ALGORITHM = "HS256"

# 管理员用户信息
user_id = 1
phone = "13800000000"
role = "admin"

# 创建payload
payload = {
    "userId": user_id,
    "phone": phone,
    "role": role,
    "sub": phone,  # subject
    "iat": datetime.datetime.utcnow(),  # issued at
    "exp": datetime.datetime.utcnow() + datetime.timedelta(days=7)  # expires
}

# 生成token
token = jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)

print("Generated JWT Token:")
print(token)
print("\nUse this token in Authorization header:")
print(f"Authorization: Bearer {token}")