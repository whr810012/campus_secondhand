import requests
import json

# 管理员登录获取token
login_data = {
    "phone": "13800000000",
    "password": "1234567"
}

print("正在登录管理员账户...")
login_response = requests.post('http://localhost:8081/api/auth/login', json=login_data)
print(f"登录响应状态码: {login_response.status_code}")
print(f"登录响应内容: {login_response.text}")

if login_response.status_code == 200:
    login_result = login_response.json()
    if login_result.get('success'):
        token = login_result['data']['token']
        print(f"获取到token: {token[:20]}...")
        
        # 使用token调用封禁接口
        headers = {
            'Authorization': f'Bearer {token}',
            'Content-Type': 'application/json'
        }
        
        ban_data = {
            "adminId": 1,
            "reason": "测试封禁",
            "banDays": 7
        }
        
        print("\n正在调用封禁接口...")
        ban_response = requests.post('http://localhost:8081/api/admin/users/8/ban', 
                                   json=ban_data, headers=headers)
        print(f"封禁响应状态码: {ban_response.status_code}")
        print(f"封禁响应内容: {ban_response.text}")
        
    else:
        print(f"登录失败: {login_result.get('message')}")
else:
    print("登录请求失败")