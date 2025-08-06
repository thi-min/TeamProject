# 1단계: build
FROM node:20 AS builder

WORKDIR /app

COPY . .

RUN npm install && npm run build

# 2단계: nginx에 정적 파일 복사
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html

# 커스텀 nginx 설정 파일 복사
COPY ../nginx/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
