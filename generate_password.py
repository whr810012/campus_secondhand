import bcrypt

# 生成密码 "Ww810012" 的BCrypt哈希
password = "Ww810012"
salt = bcrypt.gensalt()
hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
print(f"BCrypt hash: {hashed.decode('utf-8')}")

# 验证密码
if bcrypt.checkpw(password.encode('utf-8'), hashed):
    print("Password verification: SUCCESS")
else:
    print("Password verification: FAILED")