curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "1234567t",
    "email": "test@example.com",
    "phone": "13800138000"
  }'


#curl -X POST http://localhost:8080/api/auth/register \
#  -H "Content-Type: application/json" \
#  -d '{
#    "username": "testuser2",
#    "password": "123456"
#  }'

#curl -X POST http://localhost:8080/api/auth/register \
#  -H "Content-Type: application/json" \
#  -d '{
#    "username": "te",
#    "password": "123456"
#  }'