# ==============================================================================
# Estagio 1: build da SPA com Node e Vite
# ==============================================================================
FROM node:24-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund

COPY . .

# Caminho base da API. Com /api, o nginx deste container faz o proxy para o
# backend, evitando CORS e deixando a porta do backend fora do navegador.
ARG VITE_API_URL=/api
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build

# ==============================================================================
# Estagio 2: nginx servindo os arquivos estaticos e fazendo proxy de /api
# ==============================================================================
FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
